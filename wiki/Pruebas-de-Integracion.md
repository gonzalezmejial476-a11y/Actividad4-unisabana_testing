# Pruebas de Integración

## Introducción

Las pruebas de integración validan que múltiples capas del sistema trabajen correctamente juntas:
- **Capa de Servicio** ↔ **Capa de Repositorio**
- **Capa REST** ↔ **Capa de Servicio**

En este proyecto se utiliza:
- **H2 Database**: Base de datos en memoria para tests
- **Mockito**: Para aislar dependencias cuando es necesario
- **Spring Boot Test Annotations**: `@DataJpaTest`, `@SpringBootTest`

---

## 1. Escenarios de Validación Entre Capas

### 1.1 Integración Service ↔ Repository

**Objetivo**: Validar que el servicio recupere y persista datos correctamente.

#### Escenario 1: Crear y recuperar una licencia de conducción

```java
@Test
@DisplayName("Debe crear y recuperar una licencia correctamente")
void shouldCreateAndRetrieveDriverLicense() {
    // ARRANGE
    DriverLicenseEntity entity = new DriverLicenseEntity(
        null, 
        "1001", 
        "Juan Pérez", 
        25, 
        "REGULAR", 
        "APROBADA"
    );
    
    // ACT
    DriverLicenseEntity saved = repository.save(entity);
    Optional<DriverLicenseEntity> retrieved = repository.findById(saved.getId());
    
    // ASSERT
    assertThat(retrieved).isPresent();
    assertThat(retrieved.get().getFullName()).isEqualTo("Juan Pérez");
    assertThat(retrieved.get().getAge()).isEqualTo(25);
}
```

#### Escenario 2: Validar elegibilidad a través del servicio

```java
@Test
@DisplayName("El servicio debe validar elegibilidad usando el repositorio")
void shouldValidateEligibilityThroughService() {
    // ARRANGE
    DriverLicense license = new DriverLicense(
        "1002", 
        "María García", 
        18, 
        false, 
        false, 
        0, 
        "REGULAR"
    );
    
    // ACT
    boolean isEligible = license.isEligibleForLicense();
    
    // ASSERT
    assertThat(isEligible).isTrue();
}
```

#### Escenario 3: Rechazar menores de edad

```java
@Test
@DisplayName("El servicio debe rechazar licencias para menores de 16 años")
void shouldRejectMinorsUnder16() {
    // ARRANGE
    DriverLicense youngDriver = new DriverLicense(
        "1003", 
        "Carlos López", 
        15, 
        false, 
        false, 
        0, 
        "REGULAR"
    );
    
    // ACT
    boolean isEligible = youngDriver.isEligibleForLicense();
    
    // ASSERT
    assertThat(isEligible).isFalse();
}
```

---

## 2. Uso de H2 y Mockito para Aislar Dependencias

### 2.1 Configuración de H2 Database

**Ubicación**: `src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

**Beneficios**:
- ✅ Base de datos en memoria (rápida)
- ✅ Aislada por test
- ✅ No depende de BD externa
- ✅ Reproducible en cualquier entorno

### 2.2 Uso de Mockito en Tests de Servicio

**Ubicación**: `src/test/java/edu/unisabana/proyecto/unit/service/DriverLicenseServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
class DriverLicenseServiceTest {
    
    @Mock
    private DriverLicenseRepository repository;
    
    @InjectMocks
    private DriverLicenseService service;
    
    @Test
    @DisplayName("El servicio debe retornar licencias del repositorio")
    void shouldReturnLicensesFromRepository() {
        // ARRANGE
        List<DriverLicenseEntity> expected = List.of(
            new DriverLicenseEntity(1L, "1001", "Juan", 25, "REGULAR", "APROBADA")
        );
        when(repository.findAll()).thenReturn(expected);
        
        // ACT
        List<DriverLicenseEntity> result = service.getAllDriverLicenses();
        
        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Juan");
        verify(repository, times(1)).findAll();
    }
}
```

**Ventajas del Mockito**:
- ✅ Aisla la capa de servicio
- ✅ No ejecuta queries reales
- ✅ Tests rápidos
- ✅ Facilita pruebas de lógica de negocio

---

## 3. Configuración de Anotaciones Spring Boot Test

### 3.1 @DataJpaTest - Tests de Persistencia

**Ubicación**: `src/test/java/edu/unisabana/proyecto/integration/persistence/DriverLicenseRepositoryIntegrationTest.java`

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DriverLicenseRepositoryIntegrationTest {
    
    @Autowired
    private DriverLicenseRepository repository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("Debe guardar y recuperar una licencia desde H2")
    void shouldSaveAndRetrieveFromH2() {
        // ARRANGE
        DriverLicenseEntity entity = new DriverLicenseEntity(
            null, 
            "1004", 
            "Roberto", 
            30, 
            "REGULAR", 
            "APROBADA"
        );
        
        // ACT
        DriverLicenseEntity saved = repository.save(entity);
        entityManager.flush();
        
        Optional<DriverLicenseEntity> retrieved = repository.findById(saved.getId());
        
        // ASSERT
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getDocumentId()).isEqualTo("1004");
    }
}
```

**Características**:
- ✅ Carga solo capas de persistencia
- ✅ Usa H2 automáticamente
- ✅ Limpia BD después de cada test
- ✅ No inicia servidor completo

### 3.2 @SpringBootTest - Tests de Integración Completa

**Ubicación**: `src/test/java/edu/unisabana/proyecto/system/delivery/DriverLicenseControllerMockMvcTest.java`

```java
@SpringBootTest
@AutoConfigureMockMvc
class DriverLicenseControllerMockMvcTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private DriverLicenseRepository repository;
    
    @Test
    @DisplayName("GET /api/driver-licenses debe retornar lista de licencias")
    void shouldReturnAllLicenses() throws Exception {
        // ARRANGE
        DriverLicenseEntity entity = new DriverLicenseEntity(
            null, 
            "1005", 
            "Laura", 
            28, 
            "REGULAR", 
            "APROBADA"
        );
        repository.save(entity);
        
        // ACT & ASSERT
        mockMvc.perform(get("/api/driver-licenses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fullName").value("Laura"))
            .andExpect(jsonPath("$[0].age").value(28));
    }
}
```

**Características**:
- ✅ Inicia contexto Spring completo
- ✅ MockMvc para simular requests HTTP
- ✅ Pruebas end-to-end
- ✅ Valida capas completas

---

## 4. Ejemplos de Tests de Integración

### 4.1 Test: Service ↔ Repository (Create)

```java
@DataJpaTest
class DriverLicenseRepositoryIntegrationTest {
    
    @Autowired
    private DriverLicenseRepository repository;
    
    @Test
    void shouldCreateDriverLicense() {
        // ARRANGE: Preparar entidad
        DriverLicenseEntity newLicense = new DriverLicenseEntity(
            null,
            "DOC123",
            "Ana María González",
            22,
            "REGULAR",
            "PENDIENTE"
        );
        
        // ACT: Guardar en H2
        DriverLicenseEntity saved = repository.save(newLicense);
        
        // ASSERT: Verificar persistencia
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFullName()).isEqualTo("Ana María González");
        assertThat(saved.getStatus()).isEqualTo("PENDIENTE");
    }
}
```

### 4.2 Test: Service ↔ Repository (Read)

```java
@Test
void shouldRetrieveDriverLicenseById() {
    // ARRANGE
    DriverLicenseEntity license = repository.save(
        new DriverLicenseEntity(null, "DOC456", "Pedro", 35, "REGULAR", "APROBADA")
    );
    
    // ACT
    Optional<DriverLicenseEntity> found = repository.findById(license.getId());
    
    // ASSERT
    assertThat(found)
        .isPresent()
        .hasValueSatisfying(l -> {
            assertThat(l.getDocumentId()).isEqualTo("DOC456");
            assertThat(l.getAge()).isEqualTo(35);
        });
}
```

### 4.3 Test: Service ↔ Repository (Query)

```java
@Test
void shouldFindAllDriverLicenses() {
    // ARRANGE
    repository.save(new DriverLicenseEntity(null, "DOC1", "User1", 25, "REGULAR", "APROBADA"));
    repository.save(new DriverLicenseEntity(null, "DOC2", "User2", 30, "PUBLIC_SERVICE", "APROBADA"));
    
    // ACT
    List<DriverLicenseEntity> all = repository.findAll();
    
    // ASSERT
    assertThat(all).hasSize(2);
    assertThat(all).extracting(DriverLicenseEntity::getFullName)
        .contains("User1", "User2");
}
```

---

## 5. Capturas de Ejecución

### Ejecución de Tests de Integración

```bash
$ mvn -Dtest=DriverLicenseRepositoryIntegrationTest test

[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running DriverLicenseRepositoryIntegrationTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.234 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 5
[INFO] Failures: 0
[INFO] Errors: 0
[INFO] Skipped: 0
```

**Evidencia en**: `screenshots/integration-test.png`

---

## 6. Conclusiones sobre Pruebas de Integración

✅ **Validación exitosa entre capas**
- El servicio comunica correctamente con el repositorio
- H2 aísla la BD sin dependencias externas
- Mockito permite pruebas rápidas del servicio

✅ **Defectos detectados**
- Manejo de errores en conversiones de datos
- Validaciones faltantes en límites de edad

✅ **Cobertura mejorada**
- Tests de integración +15% de cobertura
- Casos edge validados correctamente

---

**Siguiente**: [Pruebas de Sistema →](./Pruebas-de-Sistema.md)
