# Taller de Testing - Universidad de Sabana

## Descripción del Proyecto

**Dominio**: Elegibilidad para Licencias de Conducción (`DriverLicense`)  
**Objetivo**: Aplicar TDD, BDD, AAA, clases de equivalencia y cobertura de código  
**Arquitectura**: Arquitectura Hexagonal (Domain-Driven Design)

## Integrantes

- Luis Eduardo Gonzalez Mejia

## Contenido del Wiki

Para la documentación completa del taller, consulte el **[Wiki del Repositorio](https://github.com/LEGM121/testing-unisabana/wiki)**.

### Secciones del Wiki:

1. **[Inicio](https://github.com/LEGM121/testing-unisabana/wiki)** - Dominio, alcance y equipo
2. **[TDD: Ciclo Rojo-Verde-Refactor](https://github.com/LEGM121/testing-unisabana/wiki/TDD-History)** - 3+ iteraciones
3. **[Patrón AAA](https://github.com/LEGM121/testing-unisabana/wiki/AAA-Pattern)** - Arrange-Act-Assert
4. **[Clases de Equivalencia](https://github.com/LEGM121/testing-unisabana/wiki/Equivalence-Classes)** - Tabla y justificación
5. **[BDD: Dado-Cuando-Entonces](BDD-Scenarios.md)** - Escenarios
6. **[Resultados](Results.md)** - JaCoCo y conclusiones
7. **[Historial TDD](TDD-HISTORY.md)** - Ciclos Rojo/Verde/Refactor
8. **[Defectos](https://github.com/LEGM121/testing-unisabana/wiki/Defects)** - Análisis de defectos

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

## Entrega requerida (descripciones y significados)

Esta sección sustituye las evidencias gráficas por descripciones claras de qué se espera y qué significa cada punto de entrega.

1) Repositorio principal — qué significa
- **Código fuente y pruebas**: El repositorio debe contener el código de producción y todas las pruebas automatizadas (unitarias, de integración y de sistema). Esto garantiza que el comportamiento está verificado por pruebas reproducibles.
- **`.gitignore`**: Archivo que excluye artefactos generados (por ejemplo `target/`, `logs/`, directorios/archivos de IDE). Su propósito es evitar subir binarios y archivos locales que no forman parte del código fuente.
- **Integrantes**: Archivo `integrantes.txt` o sección `Integrantes` en el `README.md` que identifique claramente a las personas responsables.
- **Ejecución reproducible**: Ejecutar el build y las pruebas con el comando siguiente; incluir la salida de la ejecución como texto (logs) en `Results.md` o en el Wiki.

```bash
mvn clean verify
```

- **URL pública**: Dirección del repositorio (GitHub/GitLab). Significa que cualquiera puede clonar, ejecutar y validar la entrega siguiendo las instrucciones.

2) Wiki del proyecto — propósito y qué debe contener
El Wiki es el documento oficial de entrega: debe describir el diseño, la ejecución de pruebas y los resultados, explicando por qué y cómo se validó el sistema.

- **Inicio**: Breve descripción del dominio, propósito del sistema y el contexto de uso. Incluir un diagrama de arquitectura (capas o componentes) que explique responsabilidades y límites entre capas.
- **Pruebas de Integración**: Explica qué interacciones entre capas se verifican (por ejemplo, `service ↔ repository`), cuándo se usan bases en memoria (H2) y cuándo se usan dobles (Mockito). Debe incluir fragmentos de configuración (`@DataJpaTest`, `@SpringBootTest`, `@AutoConfigureTestDatabase`) y aclarar qué se valida (persistencia, consultas, transacciones).
- **Pruebas de Sistema**: Describe pruebas end-to-end (por ejemplo con MockMvc) y qué acuerdos se verifican (códigos HTTP, estructura JSON, contratos de API). Incluir un ejemplo de prueba (`shouldReturnValidWhenPostRequest()`) y explicar qué valida.
- **Cobertura y Resultados**: Definir la métrica esperada (p. ej. ≥ 80% líneas) y cómo se midió (JaCoCo). Incluir la ruta del reporte (`target/site/jacoco/index.html`) y adjuntar en el Wiki el resumen numérico y las razones para las líneas no cubiertas.
- **Registro de Defectos**: Describir el formato del registro (archivo `defectos.md` o `defectos_integracion.md`), la clasificación por tipo (unitaria, integración, sistema) y los estados (Abierto / En progreso / Resuelto). Para cada defecto añadir: descripción, pasos para reproducir y estado actual.
- **Conclusiones y Reflexión**: Resumen ejecutivo con los defectos detectados antes del despliegue, lecciones aprendidas, y recomendaciones para futuras pruebas o mejoras.

3) Qué incluir en `Results.md` o en el Wiki
- Salida textual del comando `mvn clean verify` (logs clave y resultado final).
- Tabla con las métricas de cobertura (líneas, instrucciones, ramas) y la conclusión respecto al umbral establecido.
- Listado de tests representativos que validan flows críticos (unitarios, integración, sistema) y qué cubren.

4) Notas prácticas
- Si no se usan capturas, incluya fragmentos de logs relevantes o extractos del HTML/CSV generado por las herramientas de reporting.
- Asegure enlaces desde el `README.md` al Wiki y a `Results.md` para facilitar la revisión.

---


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

## Entrega requerida (checklist con capturas)

1) Repositorio principal
- **Código fuente**: Debe incluir todo el código del proyecto y las pruebas automatizadas (unitarias, integración y sistema).
- **.gitignore**: Incluir un archivo `.gitignore` que excluya `target/`, `logs/`, archivos de IDE (IntelliJ, VSCode), y otros artefactos.
- **Integrantes**: Asegurar que `integrantes.txt` o la sección **Integrantes** en este `README.md` contenga los nombres del equipo.
- **Ejecución reproducible**: Ejecutar y capturar la salida de:

```bash
mvn clean verify
```

- **URL pública**: Incluir la URL pública del repositorio (GitHub o GitLab) en la cabecera del README o en la sección del Wiki.

Adjuntar capturas recomendadas en la carpeta `screenshots/`:

- `verify-success.png` → salida de `mvn clean verify` exitosa
- `jacoco-coverage.png` → captura del dashboard JaCoCo (`target/site/jacoco/index.html`)
- `service-unit-test.png` → resultado de tests unitarios del servicio
- `h2-integration-test.png` → evidencia de tests de integración con H2
- `controller-system-test.png` → evidencia de tests del controlador (MockMvc)

2) Wiki del proyecto (documentación obligatoria)
El Wiki es el documento oficial de entrega y debe reflejar diseño, ejecución y resultados. Se sugiere la siguiente estructura dentro del Wiki o en `WIKI.md`:

- **Inicio**
  - Descripción del dominio y propósito del sistema.
  - Diagrama de arquitectura (capas o componentes integrados). Añadir imagen en `docs/`.
  - Integrantes del equipo (enlazar a `integrantes.txt`).

- **Pruebas de Integración**
  - Describir escenarios de validación entre capas (service ↔ repository). Indicar si alguno NO está OK y por qué.
  - Uso de H2 o Mockito para aislar dependencias (indicar configuración usada).
  - Ejemplo de configuración: `@SpringBootTest`, `@DataJpaTest`, `@AutoConfigureTestDatabase`.
  - Capturas de resultados de ejecución y reportes JaCoCo (en `screenshots/jacoco-coverage.png`).

- **Pruebas de Sistema**
  - Describir pruebas end-to-end con MockMvc u otra herramienta HTTP.
  - Validación de respuestas, códigos HTTP y mensajes JSON.
  - Incluir ejemplo de prueba `shouldReturnValidWhenPostRequest()` y captura de su ejecución.

- **Cobertura y Resultados**
  - Incluir captura del reporte JaCoCo: `target/site/jacoco/index.html` (`screenshots/jacoco-coverage.png`).
  - Indicar cobertura global (objetivo ≥ 80%) y adjuntar evidencia (logs o salida de `mvn verify`).
  - Identificar líneas no cubiertas y justificar brevemente por qué no están testeadas.

- **Registro de Defectos**
  - Incluir `defectos_integracion.md` o utilizar `defectos.md` existente con la clasificación por tipo (unitaria, integración, sistema).
  - Para cada defecto: descripción, pasos para reproducir, estado (Abierto / En progreso / Resuelto) y captura de pantalla si aplica.

- **Conclusiones y Reflexión**
  - Resumen de defectos detectados antes del despliegue.
  - Desafíos al probar múltiples capas.
  - Cómo las pruebas de integración mejoran la confianza del sistema.

**Notas finales**: Añadir en el Wiki enlaces a los artefactos generados por CI (artifacts de Actions) y a los reportes en `target/site/`. Asegúrese de que todas las capturas estén dentro de `screenshots/` y referenciadas desde el Wiki o `README.md`.

*** Fin de la sección de entrega requerida ***

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

## CI Trigger (auto)

Preparado para ejecutar el pipeline CI/CD en GitHub. Si empujas la rama `trigger/ci-run-20260607` al remoto, el workflow se iniciará automáticamente según la configuración en `.github/workflows/ci-cd.yml`.

Trigger preparado en: 2026-06-07T21:44:33-05:00
