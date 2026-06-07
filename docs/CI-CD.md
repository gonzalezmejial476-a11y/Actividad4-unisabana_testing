# Pipeline CI/CD - Automatización de Pruebas

## Descripción General

El proyecto utiliza **GitHub Actions** para implementar un pipeline de integración continua (CI/CD) que valida automáticamente todas las pruebas, cobertura y calidad de código antes de permitir la integración de cambios.

## Configuración

El pipeline está definido en `.github/workflows/ci-cd.yml` y se activa en:
- Push a ramas: `main`, `develop`, `appmod/**`
- Pull requests hacia `main` o `develop`

## Jobs del Pipeline

### 1. `build-and-test` (Build and Test)
**Responsabilidad**: Compilar y ejecutar todas las pruebas.

**Ejecución en paralelo**:
- Java 21
- Java 25

**Pasos**:
1. Checkout del código
2. Configuración de Java (versión según la matrix)
3. Mostrar versiones (Java, Maven)
4. Compilación del proyecto
5. Ejecutar pruebas unitarias del dominio
6. Ejecutar tests de servicio con mocks
7. Ejecutar tests de integración con H2
8. Ejecutar tests del controlador con MockMvc
9. Generar reporte JaCoCo completo
10. Subir reportes como artefactos

**Salida esperada**:
- ✓ Todas las pruebas pasan
- ✓ Cobertura JaCoCo ≥ 80%

**Artefactos generados**:
- `jacoco-report-java21/` (reporte HTML/CSV/XML)
- `jacoco-report-java25/` (reporte HTML/CSV/XML)
- `surefire-reports-java21/` (resultados detallados de JUnit)
- `surefire-reports-java25/` (resultados detallados de JUnit)

### 2. `code-quality` (Code Quality Check)
**Responsabilidad**: Validar calidad de código (si está configurado).

**Pasos**:
1. Checkout del código
2. Configuración de Java 21
3. Ejecución de Checkstyle (si existe en pom.xml)
4. Ejecución de SpotBugs (si existe en pom.xml)

**Nota**: Actualmente está en modo "tolerante" (no falla si no está configurado).

### 3. `coverage-report` (Coverage Validation)
**Responsabilidad**: Validar que la cobertura cumpla el mínimo (80%).

**Pasos**:
1. Checkout del código
2. Configuración de Java 21
3. Ejecución de `mvn clean verify`
4. Si es un Pull Request: Comenta en el PR con el reporte de cobertura

**Salida esperada**:
- Archivo `target/site/jacoco/jacoco.xml` analizado
- Validación de cobertura de líneas ≥ 80%
- Comentario automático en el PR (si aplica)

### 4. `notify-status` (Notify Build Status)
**Responsabilidad**: Resumen final y notificación de estado.

**Depende de**: Los tres jobs anteriores

**Salida**:
- ✓ Si todos los checks pasaron: "ready to merge"
- ✗ Si alguno falló: "review required" + exit code 1

## Métricas y Validaciones

| Métrica | Mínimo | Validado por |
|---------|--------|--------------|
| Cobertura de líneas | 80% | JaCoCo + `BUNDLE` rule |
| Pruebas unitarias | ✓ Todas pasan | `DriverLicenseTest` |
| Pruebas de servicio (mocks) | ✓ Todas pasan | `DriverLicenseServiceTest` |
| Pruebas de integración (H2) | ✓ Todas pasan | `DriverLicenseRepositoryIntegrationTest` |
| Pruebas del controlador (MockMvc) | ✓ Todas pasan | `DriverLicenseControllerMockMvcTest` |
| Compilación | ✓ Sin errores | Maven compile |

## Cómo Leer los Resultados

### En la Interfaz de GitHub

1. **Actions Tab**:
   - Selecciona el workflow "CI/CD Pipeline - Testing Workshop"
   - Visualiza el estado de cada ejecución (verde/rojo)

2. **Detalles de la Ejecución**:
   - Click en el nombre de la ejecución
   - Expande cada job para ver los pasos
   - Verifica los logs de cada paso

3. **Artefactos**:
   - Ve a "Summary" de la ejecución
   - Descarga los reportes (ZIP) desde "Artifacts"
   - Extrae y abre `index.html` localmente

### En un Pull Request

- Los resultados aparecen en la sección **Checks**
- Se muestra el estado de cada job (required o optional)
- Si está configurada la protección de rama, muestra si se puede mergear

### En Terminal (Local)

```powershell
# Ver logs del último workflow
git log --oneline -n 5

# Revisar status local
mvn clean verify
```

## Configuración de Protección de Rama

Para bloquear merges si el pipeline falla:

1. Ve a **Settings** > **Branches** en GitHub
2. Selecciona la rama (ej. `main`)
3. En "Branch protection rules", habilita:
   - **Require status checks to pass before merging**
   - Marca como obligatorios:
     - `build-and-test`
     - `coverage-report`
   - **Require branches to be up to date before merging**
4. Guarda los cambios

Con esto, un PR no se podrá mergear si:
- Las pruebas fallan
- La cobertura cae por debajo del 80%
- El código está desactualizado respecto a la rama destino

## Troubleshooting

### El pipeline falla con error de compilación

**Causa**: Código con errores de sintaxis o dependencias no resueltas.

**Solución**:
```powershell
mvn clean compile
```
Revisa los errores y corrígelos localmente.

### Las pruebas fallan en GitHub pero pasan localmente

**Causa**: Diferencia en versión de Java.

**Solución**:
- Verifica que usas Java 21 o 25 localmente
- Compara los logs del pipeline con tu terminal local

### La cobertura falla pero los tests pasan

**Causa**: JaCoCo reporta cobertura < 80%.

**Solución**:
```powershell
mvn clean verify
# Abre target/site/jacoco/index.html
# Identifica las clases/métodos no cubiertos
# Añade tests para esas partes
```

### El workflow no se ejecuta en un push

**Posibles causas**:
1. La rama no está en la lista de activación
2. El archivo `.github/workflows/ci-cd.yml` tiene errores YAML
3. El repositorio no tiene Actions habilitado

**Solución**:
1. Ve a **Actions** tab
2. Verifica que "Workflows" esté habilitado
3. Revisa la sintaxis del YAML en línea

## Mejoras Futuras

- [ ] Añadir integración con SonarQube para análisis estático profundo
- [ ] Generación automática de reportes en una rama `gh-pages`
- [ ] Notificaciones Slack/Teams cuando falla el pipeline
- [ ] Ejecutar tests de performance en cada push
- [ ] Integración con Docker para tests en contenedores
- [ ] Análisis de seguridad (SAST) con herramientas como Snyk

## Referencias

- [GitHub Actions Documentation](https://docs.github.com/actions)
- [Maven Verify Goal](https://maven.apache.org/plugins/maven-verify-plugin/)
- [JaCoCo Maven Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)
- [Branch Protection Rules](https://docs.github.com/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests)
