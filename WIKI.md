# Wiki del Taller de Pruebas Unitarias

## 1. Inicio

**Dominio**: Elegibilidad para Licencias de Conducción en Colombia.

**Alcance del taller**:
- Validar reglas de edad mínima, edad máxima y tipo de licencia.
- Evaluar discapacidad visual y antecedentes penales.
- Asegurar elegibilidad con pruebas unitarias de alta cobertura.

**Equipo**:
- LEGM121 (Estudiante)

**Estructura del proyecto**:
- `src/main/java/com/unisabana/domain/DriverLicense.java`
- `src/test/java/com/unisabana/domain/DriverLicenseTest.java`
- `pom.xml` con JUnit 5, AssertJ y JaCoCo.

> Nota: la versión local actual es un ejercicio centrado en el dominio y pruebas unitarias. En el contexto del taller se conservan referencias pedagógicas a H2 y MockMvc como extensiones de integración que pueden añadirse en iteraciones posteriores.

## 2. TDD (Red → Green → Refactor)

### Historia TDD

1. **Iteración 1 (Red)**: Crear un test que rechace una solicitud de licencia para una persona menor de 16 años.
2. **Iteración 2 (Green)**: Implementar la validación mínima de edad en `DriverLicense` para devolver rechazo cuando `age < 16`.
3. **Iteración 3 (Refactor)**: Extraer constantes como `MINIMUM_REGULAR_AGE`, `MINIMUM_PUBLIC_SERVICE_AGE` y mejorar la legibilidad.
4. **Iteración 4 (Green/Refactor)**: Agregar reglas adicionales para licencia de servicio público, discapacidades y antecedentes penales.

### Ejemplos de tests generados por TDD

- `shouldRejectChildrenUnder16`
- `shouldAllowRestrictedLicenseForAdolescents`
- `shouldRejectPublicServiceUnder23`
- `shouldRejectWithSevereEyeDisability`
- `shouldRejectWithCriminalRecords`

> El proceso siguió el ciclo Red → Green → Refactor en cada regla de negocio clave.

## 3. Patrón AAA (Arrange–Act–Assert)

### Pautas usadas

- **Arrange**: preparar los datos y el estado inicial.
- **Act**: ejecutar la operación bajo prueba.
- **Assert**: verificar el resultado esperado.

### Ejemplo

```java
@Test
@DisplayName("Should retrieve all driver attributes correctly")
void shouldRetrieveAllAttributes() {
    // ARRANGE
    DriverLicense person = new DriverLicense("1001", "Juan Pérez García", 25,
                                            false, false, 0, "REGULAR");

    // ACT
    String name = person.getFullName();

    // ASSERT
    assertThat(name).isEqualTo("Juan Pérez García");
}
```

## 4. Clases de Equivalencia y Valores Límite

### Clases de equivalencia

| Clase | Rango | Comportamiento esperado | Test |
|------|-------|-------------------------|------|
| TOO_YOUNG | < 16 | Rechazo inmediato | `shouldRejectChildrenUnder16` |
| ADOLESCENT | 16-17 | Licencia restringida regular permitida | `shouldAllowRestrictedLicenseForAdolescents` |
| YOUNG_ADULT | 18-22 | Licencia regular permitida, servicio público rechazado | `shouldAllowYoungAdults` |
| ADULT | 23-64 | Licencia regular y servicio público permitidos | `shouldAllowFullLicenseAdults` |
| SENIOR | 65-80 | Licencia permitida con renovación | `shouldAllowSeniorsWithRenewal` |
| TOO_OLD | > 80 | Rechazo por edad máxima | `shouldRejectOver80Years` |

### Valores límite

| Límite | Valor | Resultado esperado | Test |
|--------|-------|-------------------|------|
| 16 | 15 / 16 | Rechazo antes de 16; aprobado restringido a los 16 | `boundaryValue_Age15`, `boundaryValue_Age16` |
| 23 | 22 / 23 | Rechazo servicio público antes de 23; aprobado a los 23 | `boundaryValue_Age22`, `boundaryValue_Age23` |
| 80 | 80 / 81 | Aceptado hasta 80; rechazado a 81 | `boundaryValue_Age80`, `boundaryValue_Age81` |

## 5. BDD (Given–When–Then)

### Escenarios clave

- **Given** un joven de 15 años
  - **When** solicita licencia regular
  - **Then** es rechazado
  - Test: `shouldRejectChildrenUnder16`

- **Given** un adolescente de 16 años
  - **When** solicita licencia regular
  - **Then** recibe licencia restringida
  - Test: `shouldAllowRestrictedLicenseForAdolescents`

- **Given** una persona de 22 años
  - **When** solicita licencia de servicio público
  - **Then** se rechaza la solicitud y `yearsUntilPublicService()` devuelve 1
  - Test: `shouldRejectPublicServiceUnder23`

- **Given** una persona de 23 años
  - **When** solicita licencia de servicio público
  - **Then** la solicitud se aprueba
  - Test: `shouldApprovePublicService23Years`

- **Given** una persona con discapacidad visual severa
  - **When** verifica elegibilidad
  - **Then** es rechazada
  - Test: `shouldRejectWithSevereEyeDisability`

- **Given** una persona con antecedentes penales graves
  - **When** verifica elegibilidad
  - **Then** es rechazada
  - Test: `shouldRejectWithCriminalRecords`

## 6. Pruebas de Sistema

### Enfoque y alcance
- La versión local actual no incluye una capa REST ni un repositorio H2 funcional.
- El taller documenta el enfoque para agregar `delivery/rest` y `JdbcDriverLicenseRepository` como una extensión de la solución.
- Las pruebas de sistema conceptuales serían realizadas con `MockMvc` para el controlador y con H2 para la persistencia en memoria.

### Casos de prueba incluidos
- En el diseño del taller se propone un test `DriverLicenseControllerMockMvcTest` para validar el controlador REST.
- También se propone un test `JdbcDriverLicenseRepositoryIntegrationTest` para validar la integración con H2.

### Objetivos
- Verificar la integración entre la capa de entrega y la capa de aplicación.
- Confirmar que un repositorio H2 en memoria puede persistir y consultar correctamente las solicitudes en una versión ampliada del proyecto.

## 7. Resultados

### Ejecución local

- Ejecutar todas las pruebas:
  - `mvn clean test`
- Ejecutar una prueba específica:
  - `mvn -Dtest=DriverLicenseTest#shouldRejectPublicServiceUnder23 test`
- Generar reporte JaCoCo:
  - `mvn clean test jacoco:report`

### Cobertura y evidencia

- Reporte HTML en: `target/site/jacoco/index.html`
- Captura disponible en: `docs/jacoco-overview.png`

### Conclusiones técnicas

- La implementación cubre las reglas de negocio clave para la licencia de conducción.
- Se aplicaron TDD, BDD y patrón AAA en la construcción de los tests.
- La estrategia de pruebas incluye clases de equivalencia, valores límite y escenarios de rechazo/aceptación.

## 7. Enlaces al código

- `src/main/java/com/unisabana/domain/DriverLicense.java`
- `src/test/java/com/unisabana/domain/DriverLicenseTest.java`
- `README.md`
- `BDD-Scenarios.md`
- `Equivalence-Classes.md`
- `TDD-HISTORY.md`
- `Results.md`

## 8. Requisitos del repositorio

- Repositorio Git público con URL de acceso.
- `.gitignore` con exclusiones como `target/`.
- `integrantes.txt` con nombres de equipo.
- Rama principal compilable con `mvn clean test`.
luis