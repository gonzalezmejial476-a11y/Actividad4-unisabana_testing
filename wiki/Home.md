# Taller de Testing - Universidad de Sabana

## 1. Inicio

### Descripción del Dominio y Propósito del Sistema

**Dominio**: Elegibilidad para Licencias de Conducción (`DriverLicense`)

**Propósito**: Aplicar pruebas de integración, pruebas de sistema y cobertura de código en una arquitectura en capas, validando que el sistema funcione correctamente en múltiples niveles.

**Objetivo Académico**: 
- Entender cómo las pruebas de integración validan la comunicación entre capas
- Evaluar defectos que emergen cuando múltiples componentes interactúan
- Medir cobertura de código con JaCoCo
- Documentar defectos encontrados durante pruebas

---

### Diagrama de Arquitectura (Capas Integradas)

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

---

### Integrantes del Equipo

| Nombre | Rol | Responsabilidades |
|--------|-----|-------------------|
| Luis Eduardo Gonzalez Mejia | Desarrollador/Tester | Implementación completa, pruebas y documentación |

---

## 2. Repositorio Principal

### Código Fuente

**URL Pública del Repositorio:**
```
https://github.com/gonzalezmejial476-a11y/Actividad4-unisabana_testing
```

### Contenido Obligatorio del Repositorio

#### ✅ Archivo `.gitignore`
Excluye:
- `target/` - Artefactos compilados y reportes
- `logs/` - Archivos de logs
- Archivos de IDE (`*.iml`, `.idea/`, `.vscode/`, `.DS_Store`)
- `*.class`, `*.jar`
- Otros archivos temporales

#### ✅ Archivo `integrantes.txt`
Contiene los nombres del equipo: Luis Eduardo Gonzalez Mejia

#### ✅ Estructura del Proyecto

```
Actividad4-unisabana_testing/
├── pom.xml                            # Configuración Maven + JaCoCo
├── .gitignore                         # Exclusiones Git
├── integrantes.txt                    # Información del equipo
├── README.md                          # Documentación general
├── defectos_integracion.md            # Registro de defectos
│
├── src/
│   ├── main/java/edu/unisabana/proyecto/
│   │   ├── TestingWorkshopApplication.java         # Aplicación Spring Boot
│   │   ├── domain/
│   │   │   └── DriverLicense.java                  # Entidad de dominio
│   │   ├── application/
│   │   │   └── DriverLicenseService.java           # Servicio de aplicación
│   │   ├── delivery/rest/
│   │   │   └── DriverLicenseController.java        # REST Controller
│   │   └── infrastructure/persistence/
│   │       ├── DriverLicenseEntity.java            # Entity JPA
│   │       └── DriverLicenseRepository.java        # Spring Data JPA
│   │
│   └── test/java/edu/unisabana/proyecto/
│       ├── unit/
│       │   ├── domain/DriverLicenseTest.java       # Tests unitarios
│       │   └── service/DriverLicenseServiceTest.java
│       ├── integration/
│       │   └── persistence/DriverLicenseRepositoryIntegrationTest.java
│       └── system/
│           └── delivery/DriverLicenseControllerMockMvcTest.java
│
├── docs/                              # Documentación técnica
├── screenshots/                       # Evidencia de ejecuciones
└── .github/workflows/                 # CI/CD automatizado
```

### Ejecución Reproducible

**Comando para compilar y ejecutar pruebas:**
```bash
mvn clean verify
```

**Alternativas según tipo de test:**
```bash
# Solo pruebas unitarias
mvn clean test

# Pruebas con reporte JaCoCo
mvn clean test jacoco:report

# Verificar cobertura mínima (≥ 80%)
mvn verify

# Solo tests de integración
mvn -Dtest=DriverLicenseRepositoryIntegrationTest test

# Solo tests del servicio con mocks
mvn -Dtest=DriverLicenseServiceTest test

# Solo tests del controlador REST
mvn -Dtest=DriverLicenseControllerMockMvcTest test
```

---

## 3. Wiki del Proyecto - Estructura Completa

Esta es la documentación oficial de entrega que refleja todo el proceso de diseño, ejecución y resultados de las pruebas.

### Secciones del Wiki

1. **[Inicio](#1-inicio)** - Dominio, arquitectura e integrantes (este documento)

2. **[Pruebas de Integración](./Pruebas-de-Integracion.md)**
   - Escenarios de validación entre capas (service ↔ repository)
   - Uso de H2 y Mockito para aislar dependencias
   - Ejemplo de configuración `@SpringBootTest` y `@DataJpaTest`
   - Capturas de resultados de ejecución

3. **[Pruebas de Sistema](./Pruebas-de-Sistema.md)**
   - Pruebas end-to-end con MockMvc
   - Validación de respuestas HTTP y JSON
   - Ejemplos de tests `shouldReturnValidWhenPostRequest()`
   - Evidencia de endpoints funcionando

4. **[Cobertura y Resultados](./Cobertura-y-Resultados.md)**
   - Captura del reporte JaCoCo (`target/site/jacoco/index.html`)
   - Mínimo 80% de cobertura global
   - Identificación de líneas no cubiertas y justificación
   - Métricas por clase/paquete

5. **[Registro de Defectos](./Registro-de-Defectos.md)**
   - Archivo `defectos_integracion.md` con defectos encontrados
   - Clasificación: unitaria, integración, sistema
   - Estados: Abierto / En progreso / Resuelto
   - Prioridad y severidad

6. **[Conclusiones y Reflexión](./Conclusiones-y-Reflexion.md)**
   - Qué defectos se detectaron antes del despliegue
   - Qué desafíos se presentaron en pruebas multi-capa
   - Cómo mejoran la confianza del sistema
   - Lecciones aprendidas

---

## Requisitos Técnicos

| Componente | Versión |
|-----------|---------|
| Java | 21 |
| Maven | 3.6+ |
| Spring Boot | 3.3.0 |
| JUnit | 5.9.2 |
| Mockito | 5.3.1 |
| JaCoCo | 0.8.15 |
| H2 Database | 2.2.224 |
| AssertJ | 3.24.1 |

---

## Composición del Repositorio

- **Java**: 93.1% (Código de producción y tests)
- **Python**: 6.9% (Scripts de utilidad)

---

## Estado del Proyecto

✅ **Completado**

- ✅ Proyecto compilable: `mvn clean test`
- ✅ Cobertura objetivo: ≥ 80%
- ✅ Tests a múltiples niveles (unitarios, integración, sistema)
- ✅ Arquitectura Hexagonal con dominio aislado
- ✅ Mocks y base de datos H2 para tests
- ✅ CI/CD con GitHub Actions
- ✅ Documentación completa en Wiki

---

## Cómo Navegar esta Documentación

1. **Comienza aquí** → Lee esta página (Inicio)
2. **Entiende las pruebas** → Revisa Pruebas de Integración y Sistema
3. **Valida la calidad** → Consulta Cobertura y Resultados
4. **Analiza problemas** → Lee Registro de Defectos
5. **Reflexiona** → Termina en Conclusiones y Reflexión

---

**Última actualización**: Junio 2026  
**Rama por defecto**: `appmod/java-upgrade-20260606192045`
