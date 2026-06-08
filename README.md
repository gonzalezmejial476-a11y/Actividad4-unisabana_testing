# Taller de Testing - Universidad de Sabana

## Descripción del Proyecto

**Dominio**: Elegibilidad para Licencias de Conducción (`DriverLicense`)  
**Objetivo**: Aplicar TDD, BDD, AAA, clases de equivalencia y cobertura de código  
**Arquitectura**: Arquitectura Hexagonal (Domain-Driven Design)

## Integrantes

- Luis Eduardo Gonzalez Mejia

## Contenido del Wiki
porfa el archivose encuentra en la estructura del repo consulte todo
<img width="1487" height="405" alt="image" src="https://github.com/user-attachments/assets/07a2604e-ff8b-43d1-a084-7d096c05e2fd" />

## Cómo Ejecutar

### Compilar y ejecutar pruebas:
```bash
mvn clean test
```

### Generar reporte de cobertura:
```bash
mvn clean test jacoco:report
```

El reporte se generará en `target/site/jacoco/index.html`

### Verificar cobertura mínima:
```bash
mvn verify
```

## Pipeline CI/CD Automático

El proyecto incluye un **pipeline de integración continua (CI/CD)** configurado con GitHub Actions.

### ¿Qué hace el pipeline?

El workflow `.github/workflows/ci-cd.yml` ejecuta automáticamente:

1. **Build y pruebas** (en Java 21 y 25):
   - Compila el proyecto (`mvn clean compile`)
   - Ejecuta pruebas unitarias de dominio (`DriverLicenseTest`)
   - Ejecuta tests de servicio con mocks (`DriverLicenseServiceTest`)
   - Ejecuta tests de integración con H2 (`DriverLicenseRepositoryIntegrationTest`)
   - Ejecuta tests del controlador con MockMvc (`DriverLicenseControllerMockMvcTest`)
   - Verifica cobertura con JaCoCo (≥ 80%)

2. **Validación de calidad**:
   - Checkstyle (si está configurado)
   - SpotBugs (si está configurado)

3. **Cobertura**:
   - Genera reportes JaCoCo
   - Valida que la cobertura cumpla el mínimo requerido

4. **Generación de artefactos**:
   - Sube reportes de JaCoCo
   - Sube reportes de Surefire para consulta posterior

### Cuándo se ejecuta

- En cada **push** a las ramas: `main`, `develop`, `appmod/**`
- En cada **pull request** hacia `main` o `develop`
- Los resultados aparecen en la pestaña **Actions** del repositorio

### Cómo ver resultados

1. Ve a la pestaña **Actions** en GitHub
2. Selecciona la rama o el PR
3. Consulta el estado de cada job:
   - ✓ Verde: Todas las pruebas pasaron
   - ✗ Rojo: Alguna prueba o validación falló
4. Descarga los artefactos (reportes JaCoCo y Surefire) desde el resumen de la ejecución

### Protección de rama (recomendado)

Para evitar que se mergee código que no pase las pruebas:

1. Ve a **Settings** > **Branches**
2. Selecciona `main` o `develop`
3. Habilita **Require status checks to pass before merging**
4. Marca como obligatorios: `build-and-test` y `coverage-report`

Esto impedirá que un PR se pueda mergear si las pruebas fallan o la cobertura no cumple el mínimo.

## Estructura del Proyecto

```
actividad_4-testing-unisabana/
├── pom.xml                            # Configuración Maven + JaCoCo (Java 21, Spring Boot 3.3.0)
├── .gitignore                         # Exclusiones Git
├── integrantes.txt                    # Información del equipo
├── README.md                          # Este archivo
├── BDD-Scenarios.md                   # Escenarios BDD (Dado-Cuando-Entonces)
├── Equivalence-Classes.md             # Clases de equivalencia
├── Results.md                         # Resultados y conclusiones
├── TDD-HISTORY.md                     # Ciclo TDD (Rojo-Verde-Refactor)
├── WIKI.md                            # Wiki del proyecto
├── defectos.md                        # Registro de defectos
├── docs/                              # Documentación y diagramas
├── screenshots/                       # Capturas de evidencia (JaCoCo, tests, etc.)
├── tools/                             # Scripts de apoyo
│
└── src/
    ├── main/java/edu/unisabana/proyecto/
    │   ├── TestingWorkshopApplication.java         # Aplicación Spring Boot
    │   │
    │   ├── domain/
    │   │   └── DriverLicense.java                  # Entidad de dominio: Lógica de elegibilidad
    │   │
    │   ├── application/
    │   │   └── DriverLicenseService.java           # Servicio de aplicación: Casos de uso
    │   │
    │   ├── delivery/rest/
    │   │   └── DriverLicenseController.java        # REST Controller: Endpoints HTTP
    │   │
    │   └── infrastructure/persistence/
    │       ├── DriverLicenseEntity.java            # Entity JPA para persistencia
    │       └── DriverLicenseRepository.java        # Spring Data JPA Repository
    │
    └── test/java/edu/unisabana/proyecto/
        ├── unit/
        │   ├── domain/
        │   │   └── DriverLicenseTest.java          # Tests unitarios: Lógica de dominio
        │   └── service/
        │       └── DriverLicenseServiceTest.java   # Tests unitarios: Servicio con mocks
        │
        ├── integration/
        │   └── persistence/
        │       └── DriverLicenseRepositoryIntegrationTest.java  # Tests con BD H2
        │
        └── system/
            └── delivery/
                └── DriverLicenseControllerMockMvcTest.java      # Tests REST con MockMvc
```

## Arquitectura del Proyecto

El proyecto implementa **Arquitectura Hexagonal** (puertos y adaptadores) con capas bien definidas:

```
┌─────────────────────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN (REST)                     │
│      DriverLicenseController                            │
│  GET /api/driver-licenses                              │
│  POST /api/driver-licenses                             │
│  GET /api/driver-licenses/{id}                         │
└────────────────────┬────────────────────────────────────┘
                     │ Solicitud/Respuesta HTTP
                     ↓
┌─────────────────────────────────────────────────────────┐
│       CAPA DE APLICACIÓN (SERVICIOS)                    │
│      DriverLicenseService                               │
│  • isEligibleForLicense()                              │
│  • getAllDriverLicenses()                              │
│  • getDriverLicenseById()                              │
└────────────────────┬────────────────────────────────────┘
                     │ Interfaz de Negocio
                     ↓
┌─────────────────────────────────────────────────────────┐
│    CAPA DE DOMINIO (NÚCLEO DE NEGOCIO)                  │
│           DriverLicense                                 │
│  Reglas de Elegibilidad:                               │
│  • Edad mínima: 16 años (con restricciones)            │
│  • Edad máxima: 80 años                                │
│  • Estados: PENDIENTE, APROBADA, RECHAZADA             │
│  • Tipos: REGULAR, SERVICIO PÚBLICO                    │
└────────────────────┬────────────────────────────────────┘
                     │ Persistencia
                     ↓
┌─────────────────────────────────────────────────────────┐
│  CAPA DE INFRAESTRUCTURA (PERSISTENCIA)                 │
│  DriverLicenseRepository (Spring Data JPA)             │
│  DriverLicenseEntity (JPA Entity)                      │
│  ↓                                                      │
│  Base de Datos H2 (en memoria para tests)              │
└─────────────────────────────────────────────────────────┘
```

### Componentes por Capa

| Capa | Componente | Responsabilidad | Tests |
|------|-----------|-----------------|-------|
| **Presentación** | `DriverLicenseController` | Endpoints REST, mapeo HTTP | `DriverLicenseControllerMockMvcTest` |
| **Aplicación** | `DriverLicenseService` | Orquestación, casos de uso | `DriverLicenseServiceTest` |
| **Dominio** | `DriverLicense` | Lógica de negocio, validaciones | `DriverLicenseTest` |
| **Infraestructura** | `DriverLicenseRepository`, `DriverLicenseEntity` | Persistencia con JPA/H2 | `DriverLicenseRepositoryIntegrationTest` |

## Estrategia de Testing

El proyecto implementa testing en **3 niveles**:

### 1️⃣ Tests Unitarios
- **Ubicación**: `src/test/java/edu/unisabana/proyecto/unit/`
- **Cobertura**:
  - `DriverLicenseTest.java`: Valida la lógica de dominio directamente
  - `DriverLicenseServiceTest.java`: Tests del servicio con Mockito (@Mock)
- **Patrón**: AAA (Arrange-Act-Assert)
- **Ejecución**: `mvn clean test`

### 2️⃣ Tests de Integración
- **Ubicación**: `src/test/java/edu/unisabana/proyecto/integration/`
- **Cobertura**:
  - `DriverLicenseRepositoryIntegrationTest.java`: Pruebas con BD H2 (@DataJpaTest)
- **Propósito**: Validar persistencia y recuperación de datos
- **Ejecución**: `mvn -Dtest=DriverLicenseRepositoryIntegrationTest test`

### 3️⃣ Tests de Sistema
- **Ubicación**: `src/test/java/edu/unisabana/proyecto/system/`
- **Cobertura**:
  - `DriverLicenseControllerMockMvcTest.java`: Tests REST con MockMvc (@SpringBootTest)
- **Propósito**: Validar endpoints HTTP y flujos completos
- **Ejecución**: `mvn -Dtest=DriverLicenseControllerMockMvcTest test`

## Clases de Equivalencia

| Clase | Rango de Edad | Descripción | Test |
|-------|-------|-------------|-------|
| MUY_JOVEN | < 16 | Menores de edad (rechazados) | `shouldRejectChildrenUnder16` |
| ADOLESCENTE | 16-17 | Adolescentes con restricciones | `shouldAllowRestrictedLicenseForAdolescents` |
| ADULTO_JOVEN | 18-22 | Adultos jóvenes | `shouldAllowYoungAdults` |
| ADULTO | 23-64 | Adultos plenos | `shouldAllowFullLicenseAdults` |
| JUBILADO | 65-80 | Jubilados (renovación obligatoria) | `shouldAllowSeniorsWithRenewal` |
| MUY_MAYOR | > 80 | Mayor a 80 años (rechazados) | `shouldRejectOver80Years` |

## Valores Límite

| Límite Crítico | Valor | Test | Justificación |
|--------|-------|------|---------------|
| Mayoría de edad | 18 | `boundaryValue_AgeEighteen` | Transición menor→adulto |
| Justo antes mayoría | 17 | `boundaryValue_AgeSeventeen` | Último día como menor |
| Inicio jubilación | 65 | `boundaryValue_AgeSixtyfive` | Edad legal de jubilación |
| Fin edad activa | 64 | `boundaryValue_AgeSixtyfour` | Último año de adulto |
| Inicio adolescencia | 16 | `boundaryValue_AgeSixteen` | Mínima edad permitida |
| Máximo permitido | 80 | `boundaryValue_AgeEighty` | Límite superior |

## Patrón AAA (Arrange-Act-Assert)

Todos los tests siguen esta estructura estándar:

```java
@Test
@DisplayName("Debe recuperar los atributos del conductor correctamente")
void debeRecuperarAtributosCorrectamente() {
    // ARRANGE: Preparar datos de prueba
    DriverLicense persona = new DriverLicense("1001", "Juan Pérez García", 25, false, false, 0, "REGULAR");

    // ACT: Ejecutar la acción
    String nombre = persona.getFullName();

    // ASSERT: Verificar el resultado esperado
    assertThat(nombre).isEqualTo("Juan Pérez García");
}
```

## BDD: Escenarios Dado-Cuando-Entonces

Los tests siguen el estilo BDD en su descripción:

```java
@Test
@DisplayName("Dado un conductor de 22 años Cuando solicita licencia de servicio público Entonces debe ser rechazado")
void debeRechazarServicioPublicoMenor23() {
    // Dado
    DriverLicense conductorJoven = new DriverLicense("1", "Joven", 22, false, false, 0, "PUBLIC_SERVICE");
    
    // Cuando
    boolean esElegible = conductorJoven.isEligibleForLicense();
    
    // Entonces
    assertThat(esElegible).isFalse();
}
```

## Requisitos

- **Java**: 21
- **Maven**: 3.6+
- **Spring Boot**: 3.3.0
- **JUnit**: 5.9.2
- **Mockito**: 5.3.1
- **AssertJ**: 3.24.1
- **JaCoCo**: 0.8.15
- **H2 Database**: 2.2.224

## Composición de Lenguajes

- **Java**: 93.1% (Código de producción y tests)
- **Python**: 6.9% (Scripts de utilidad)

## Cobertura de Código

**Objetivo**: ≥ 80% de cobertura de líneas

- Configuración en `pom.xml` con JaCoCo
- Verificación automática en fase `verify`
- Reporte HTML en `target/site/jacoco/index.html`

### Comandos para validar cobertura:
```bash
# Generar reporte
mvn clean test jacoco:report

# Verificar mínimo de cobertura
mvn verify

# Ver reporte en navegador
open target/site/jacoco/index.html
```

## Notas Importantes

- ✅ Proyecto completamente compilable: `mvn clean test` sin pasos adicionales
- ✅ Cobertura objetivo: ≥ 80%
- ✅ Todos los tests siguen nomenclatura: `debe<Esperado>Cuando<Condición>()`
- ✅ Arquitectura Hexagonal: Dominio aislado, independencia de frameworks
- ✅ Tests a múltiples niveles: Unitarios, Integración, Sistema
- ✅ Uso de mocks para aislar capas
- ✅ Base de datos H2 para tests de integración

## Recolección de Capturas de Pantalla

Para documentar el taller se recomienda capturar:

1. **Cobertura JaCoCo**: Porcentaje de líneas cubiertas
2. **Verificación Maven**: `mvn verify` exitosa
3. **Tests de Integración**: Pruebas con BD H2
4. **Tests Unitarios**: Servicio con mocks
5. **Tests REST**: Endpoints con MockMvc
6. **Reportes Surefire**: XML y TXT de cada ejecución

### Estructura de capturas:
```
screenshots/
├── jacoco-coverage.png
├── verify-success.png
├── h2-integration-test.png
├── service-unit-test.png
└── controller-system-test.png
```

### Comandos Útiles

```bash
# Tests unitarios
mvn clean test

# Validar cobertura mínima
mvn verify

# Tests de integración
mvn -Dtest=DriverLicenseRepositoryIntegrationTest test

# Tests del servicio
mvn -Dtest=DriverLicenseServiceTest test

# Tests del controlador
mvn -Dtest=DriverLicenseControllerMockMvcTest test

# Generar reporte JaCoCo
mvn jacoco:report
```

## Puntos del Taller

| Punto | Implementación | Ubicación | Estado |
|---|---|---|---|
| **TDD** | Tests unitarios Red-Verde-Refactor | `DriverLicenseTest`, `DriverLicenseServiceTest` | ✅ |
| **BDD** | Escenarios Dado-Cuando-Entonces | `BDD-Scenarios.md` | ✅ |
| **Clases de Equivalencia** | 6 clases definidas y cubiertas | `Equivalence-Classes.md` | ✅ |
| **Valores Límite** | Pruebas en límites críticos | Tests de boundary | ✅ |
| **Patrón AAA** | Arrange-Act-Assert | Todos los tests | ✅ |
| **Cobertura ≥ 80%** | JaCoCo validado | `pom.xml` | ✅ |
| **BD H2** | Integración JPA | `DriverLicenseRepositoryIntegrationTest` | ✅ |
| **Mocks** | Mockito + MockMvc | Service y Controller tests | ✅ |

## Stack de Tecnologías

```
Java 21 + Spring Boot 3.3.0
    ├── Testing
    │   ├── JUnit 5
    │   ├── Mockito
    │   ├── AssertJ
    │   └── MockMvc
    ├── Persistencia
    │   ├── Spring Data JPA
    │   └── H2 Database
    ├── Calidad
    │   └── JaCoCo
    └── Build
        └── Maven 3.6+
```

---

**Última actualización**: Junio 2026  
**Estado**: ✅ Completado  
**Rama por defecto**: `appmod/java-upgrade-20260606192045`
