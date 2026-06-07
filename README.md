# Testing Workshop - Universidad de Sabana

## Descripción del Proyecto

**Dominio**: Elegibilidad para Licencias de Conducción (`DriverLicense`)
**Objetivo**: Aplicar TDD, BDD, AAA, clases de equivalencia y cobertura de código

## Integrantes

- Luis Eduardo Gonzalez Mejia

## Contenido del Wiki

Para la documentación completa del taller, consulte el **[Wiki del Repositorio](https://github.com/LEGM121/testing-unisabana/wiki)**.

### Secciones del Wiki:

1. **[Inicio](https://github.com/LEGM121/testing-unisabana/wiki)** - Dominio, alcance y equipo
2. **[TDD: Red-Green-Refactor](https://github.com/LEGM121/testing-unisabana/wiki/TDD-History)** - 3+ iteraciones
3. **[Patrón AAA](https://github.com/LEGM121/testing-unisabana/wiki/AAA-Pattern)** - Arrange-Act-Assert
4. **[Clases de Equivalencia](https://github.com/LEGM121/testing-unisabana/wiki/Equivalence-Classes)** - Tabla y justificación
5. **[BDD: Given-When-Then](BDD-Scenarios.md)** - Escenarios
6. **[Resultados](Results.md)** - JaCoCo y conclusiones
7. **[TDD History](TDD-HISTORY.md)** - Ciclos Rojo/Verde/Refactor
7. **[Defectos](https://github.com/LEGM121/testing-unisabana/wiki/Defects)** - Análisis de defectos

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
testing-unisabana/
├── pom.xml                            # Configuración Maven + JaCoCo
├── .gitignore                         # Exclusiones Git
├── integrantes.txt                    # Información del equipo
├── README.md                          # Este archivo
├── BDD-Scenarios.md                   # Escenarios BDD
├── Equivalence-Classes.md             # Clases de equivalencia
├── Results.md                         # Resultados y conclusiones
├── TDD-HISTORY.md                     # Ciclo TDD
├── defectos.md                        # Registro de defectos
├── docs/                              # Documentación y diagramas
├── tools/                             # Scripts de apoyo
└── src/
    ├── main/
    │   └── java/
    │       └── com/unisabana/domain/
    │           └── DriverLicense.java     # Clase de dominio principal
    └── test/
        └── java/
            └── com/unisabana/domain/
                └── DriverLicenseTest.java # Suite de pruebas unitarias
```

## Arquitectura del Proyecto

El proyecto está organizado en un diseño simple y enfocado en el dominio:

- `DriverLicense.java` contiene la lógica y reglas de negocio de elegibilidad de la licencia.
- `DriverLicenseTest.java` valida los criterios de edad y estado de licencia usando pruebas unitarias.
- La versión local actual no incluye capas adicionales de servicio, controlador o persistencia H2.

### Diagrama arquitectónico

```text
+--------------------------+
|      Dominio / Modelo    |
|  DriverLicense.java      |
+--------------------------+
           /
           |
           v
+--------------------------+
|     Pruebas Unitarias    |
|  DriverLicenseTest.java  |
+--------------------------+
```

- La aplicación se centra en asegurar que el comportamiento del dominio sea correcto.
- Las pruebas se ejecutan directamente sobre la clase de dominio para validar reglas y valores límite.
- En el taller también se documenta la extensión de la solución hacia `H2` y `MockMvc` como capa de integración y pruebas de aceptación complementarias.

## Clases de Equivalencia Cubiertas (DriverLicense)

| Clase | Rango | Tests |
|-------|-------|-------|
| TOO_YOUNG | < 16 | `shouldRejectChildrenUnder16` |
| ADOLESCENT | 16-17 | `shouldAllowRestrictedLicenseForAdolescents` |
| YOUNG_ADULT | 18-22 | `shouldAllowYoungAdults` |
| ADULT | 23-64 | `shouldAllowFullLicenseAdults` |
| SENIOR | 65-80 | `shouldAllowSeniorsWithRenewal` |
| TOO_OLD | > 80 | `shouldRejectOver80Years` |

## Valores Límite Identificados

| Límite | Valor | Test | Justificación |
|--------|-------|------|---------------|
| Mayoría de edad | 18 | `boundaryValue_AgeEighteen` | Transición minor→adult |
| Justo antes mayoría | 17 | `boundaryValue_AgeSeventeen` | Último día menor |
| Jubilación | 65 | `boundaryValue_AgeSixtyfive` | Edad legal jubilación |
| Justo antes jubilación | 64 | `boundaryValue_AgeSixtyfour` | Último año activo |
| Cambio niño→adolescente | 13 | `boundaryValue_AgeThirteen` | Inicio adolescencia |
| Último año infantil | 12 | `boundaryValue_AgeEleven` | Fin infancia |

## Patrón AAA (Arrange-Act-Assert)

Todos los tests siguen la estructura. Ejemplo aplicado a `DriverLicense`:

```java
@Test
@DisplayName("Should retrieve driver attributes correctly")
void shouldRetrieveAllAttributes() {
    // ARRANGE: Preparar datos de prueba
    DriverLicense person = new DriverLicense("1001", "Juan Pérez García", 25, false, false, 0, "REGULAR");

    // ACT: Obtener atributos
    String name = person.getFullName();

    // ASSERT: Verificar el resultado esperado
    assertThat(name).isEqualTo("Juan Pérez García");
}
```

## BDD: Escenarios Given-When-Then

Los tests siguen el estilo Given–When–Then en su descripción. Ejemplo:

```java
@Test
@DisplayName("Given a 22-year-old When applying for public service license Then should be rejected (too young)")
void shouldRejectPublicServiceUnder23() {
    // Given
    DriverLicense youngDriver = new DriverLicense("1", "Young", 22, false, false, 0, "PUBLIC_SERVICE");
    // When
    boolean isEligible = youngDriver.isEligibleForLicense();
    // Then
    assertThat(isEligible).isFalse();
}
```

## Requisitos

- Java 11+
- Maven 3.6+
- JUnit 5
- AssertJ
- JaCoCo

## Notas

- El proyecto es totalmente compilable: `mvn clean test` sin pasos adicionales
- Cobertura objetivo: ≥ 80%
- Todos los tests siguen nomenclatura: `should<Expected>When<Condition>()`
- El Wiki contiene documentación oficial (no PDF)

---

## Recolección de capturas de pantalla y evidencia

Para documentar el taller se recomienda capturar las siguientes evidencias:

1. `target/site/jacoco/index.html` con el porcentaje de cobertura de líneas.
2. Resultados de `mvn verify` mostrando compilación y pruebas exitosas.
3. Salida de `mvn -Dtest=DriverLicenseRepositoryIntegrationTest test` para el uso de H2.
4. Salida de `mvn -Dtest=DriverLicenseServiceTest test` y `mvn -Dtest=DriverLicenseControllerMockMvcTest test` para los mocks.
5. Reportes en `target/surefire-reports/` con los XML y TXT de cada ejecución.

### Cómo guardar capturas

- Tome capturas de pantalla (PNG) del reporte JaCoCo en el navegador.
- Incluya los archivos dentro de `docs/` o en una carpeta `screenshots/`.
- Ejemplo de ruta: `screenshots/jacoco-coverage.png`, `screenshots/verify-success.png`, `screenshots/h2-test.png`.
- Si prefiere, use un subdirectorio dentro de `docs/`: `docs/screenshots/jacoco-coverage.png`.

### Comandos útiles

```powershell
mvn clean test
mvn verify
mvn -Dtest=DriverLicenseRepositoryIntegrationTest test
mvn -Dtest=DriverLicenseServiceTest test
mvn -Dtest=DriverLicenseControllerMockMvcTest test
```

## Puntos del taller y resolución

| Punto del taller | Implementación | Evidencia |
|---|---|---|
| TDD / ciclo rojo-verde-refactor | Tests unitarios en `DriverLicenseServiceTest` y `DriverLicenseTest` con Mockito y JUnit 5 | `TDD-HISTORY.md`, `mvn verify` |
| BDD / escenarios Given-When-Then | Escenarios documentados en `BDD-Scenarios.md` y nombres de tests descriptivos | `BDD-Scenarios.md`, `@DisplayName` en pruebas |
| Clases de equivalencia | Definidas en `Equivalence-Classes.md` y cubiertas en tests de edad y tipo de licencia | `Equivalence-Classes.md`, tablas en README |
| Cobertura de código | JaCoCo configurado en `pom.xml`, verificación global `BUNDLE` ≥ 80% | `target/site/jacoco/index.html`, `mvn verify` |
| Base de datos H2 | Pruebas de integración con `@DataJpaTest` en `DriverLicenseRepositoryIntegrationTest` | `application-test.properties`, H2 en memoria |
| Mocks | `DriverLicenseServiceTest` usa `@Mock`, `DriverLicenseControllerMockMvcTest` usa `@MockBean` | `mvn -Dtest=...` y pruebas unitarias/integración |
| Compilación y pruebas completas | Proyecto pasó `mvn verify` con exit code `0` | Salida del terminal y reportes en `target/` |

## Conclusiones del taller

- El proyecto está completo para los objetivos de testing: lógica de dominio, pruebas unitarias, integración con H2, y pruebas de controlador con mocks.
- La estrategia de pruebas es sólida: los casos cubren límites de edad, roles de licencia, estados `PENDING`, `APPROVED`, `REJECTED` y condiciones de búsqueda.
- La cobertura se validó con JaCoCo y se mantiene por encima del mínimo esperado.
- Las pruebas locales se ejecutan correctamente y la solución está registrada con evidencia en los reportes de Maven.
- La documentación del taller incluye los artefactos clave: TDD, BDD, clases de equivalencia, resultados y defectos.

## Conclusión final

Este repositorio evidencia que:

- La aplicación compila y prueba correctamente con Maven.
- El comportamiento del dominio está validado con pruebas precisas.
- La integración con H2 funciona para los repositorios de datos.
- Los mocks permiten aislar las capas de servicio y controlador.
- La entrega está lista para presentar el taller con evidencia de pruebas, cobertura y resultados.

---

**Última actualización**: Junio 2026  
**Estado**: Completado
