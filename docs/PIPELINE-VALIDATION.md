# Validación del Pipeline CI/CD - Requisitos del Taller

## ✅ Checklist de Cumplimiento

### 1. Script de Pruebas Automatizadas
**Estado**: ✓ Configurado

El pipeline ejecuta automáticamente:
- Pruebas unitarias del dominio (`DriverLicenseTest`)
- Pruebas de servicio con mocks (`DriverLicenseServiceTest`)
- Pruebas de integración con H2 (`DriverLicenseRepositoryIntegrationTest`)
- Pruebas del controlador con MockMvc (`DriverLicenseControllerMockMvcTest`)

**Ubicación en el workflow**: `.github/workflows/ci-cd.yml` - Job `build-and-test`

```yaml
- name: Run unit tests
  run: mvn -Dtest=DriverLicenseTest test

- name: Run service unit tests with mocks
  run: mvn -Dtest=DriverLicenseServiceTest test

- name: Run integration tests (H2)
  run: mvn -Dtest=DriverLicenseRepositoryIntegrationTest test

- name: Run controller tests (MockMvc)
  run: mvn -Dtest=DriverLicenseControllerMockMvcTest test
```

---

### 2. Pipeline CI/CD Funcional
**Estado**: ✓ Operativo

El workflow se ejecuta en:
- **Triggers**: Push a ramas (`main`, `develop`, `appmod/**`) y Pull Requests
- **Arquitectura**: 4 jobs paralelos + notificación final
- **Java versions**: Compila en Java 21 y 25 simultáneamente
- **Tiempo estimado de ejecución**: 5-8 minutos por ejecución

**Jobs ejecutándose**:
1. `build-and-test` - Compilación y pruebas
2. `code-quality` - Validación de calidad
3. `coverage-report` - Validación de cobertura
4. `notify-status` - Resumen y notificación

---

### 3. Métricas de Cubrimiento de Código
**Estado**: ✓ Validadas con JaCoCo

**Mínimo requerido**: 80% cobertura de líneas (BUNDLE level)

**Configuración en pom.xml**:
```xml
<rule>
  <element>BUNDLE</element>
  <excludes>
    <exclude>*Test</exclude>
  </excludes>
  <limits>
    <limit>
      <counter>LINE</counter>
      <value>COVEREDRATIO</value>
      <minimum>0.80</minimum>
    </limit>
  </limits>
</rule>
```

**Validación en el pipeline**:
```yaml
- name: Verify coverage meets minimum threshold
  run: |
    mvn clean verify
    echo "✓ All tests passed with JaCoCo verification"
```

**Cómo ver el reporte**:
1. Ve a **Actions** en GitHub
2. Selecciona la ejecución más reciente
3. Descarga el artefacto `jacoco-report-java21` o `jacoco-report-java25`
4. Abre `index.html` en el navegador
5. Verifica que `LINE` coverage sea ≥ 80%

---

### 4. Reporte de Resultados de Ejecución
**Estado**: ✓ Generados automáticamente

**Artefactos generados por ejecución**:

| Artefacto | Descripción | Duración |
|-----------|-------------|----------|
| `jacoco-report-java21` | Reporte HTML/CSV/XML de cobertura (Java 21) | 30 días |
| `jacoco-report-java25` | Reporte HTML/CSV/XML de cobertura (Java 25) | 30 días |
| `surefire-reports-java21` | Resultados detallados de JUnit (Java 21) | 30 días |
| `surefire-reports-java25` | Resultados detallados de JUnit (Java 25) | 30 días |

**Cómo acceder**:
1. Ve a GitHub → **Actions**
2. Abre la ejecución del workflow
3. Scrollea hasta "Artifacts"
4. Descarga el ZIP que necesites

**Contenido del reporte JaCoCo**:
- `index.html` - Resumen de cobertura por paquete/clase
- `jacoco.xml` - Formato XML para integración con herramientas
- `*.csv` - Formato CSV para análisis

---

### 5. Restricción de Integración - Branch Protection
**Estado**: ⚠️ Requiere configuración manual en GitHub

**¿Qué es?**: Impide que se mergee un PR si el pipeline falla o la cobertura no cumple.

#### Pasos para activar:

1. **Ve a GitHub**:
   - Repositorio: `https://github.com/lm4038882-ui/actividad_4-testing-unisabana`
   - **Settings** → **Branches**

2. **Añade una regla de protección**:
   - Click en **Add rule**
   - Branch name pattern: `main` (o `develop` si también deseas)

3. **Configura los requisitos**:
   - ✓ **Require status checks to pass before merging**
     - Busca y marca como obligatorios:
       - `build-and-test`
       - `coverage-report`
   - ✓ **Require branches to be up to date before merging**
   - ✓ **Require code reviews before merging** (opcional, recomendado)
   - ✓ **Dismiss stale pull request approvals when new commits are pushed** (opcional)

4. **Guarda los cambios**

#### Resultado:
- Un PR **NO podrá mergearse** si:
  - El job `build-and-test` falla
  - El job `coverage-report` falla (cobertura < 80%)
  - Las pruebas no pasan
  - El código está desactualizado

- Un PR **PODRÁ mergearse** si:
  - ✓ Todas las pruebas pasan
  - ✓ Cobertura ≥ 80%
  - ✓ La rama está actualizada
  - ✓ (Si está configurado) Tiene al menos 1 revisión de aprobación

---

## 📊 Ejemplo de Ejecución

### Cuando haces un Push:

```
1. GitHub detecta el push
   ↓
2. Activa el workflow ci-cd.yml
   ↓
3. Inicia los 4 jobs en paralelo
   ├── build-and-test (Java 21 + Java 25)
   ├── code-quality
   ├── coverage-report
   └── (esperando que terminen los anteriores)
   ↓
4. notify-status resume los resultados
   ↓
5. ✓ Si todo pasa → "ready to merge"
   ✗ Si falla algo → "review required" + logs detallados
```

### Cuando haces un Pull Request:

```
1. Abres un PR
   ↓
2. GitHub ejecuta el pipeline automáticamente
   ↓
3. En la sección "Checks" del PR ves el estado
   ├── ✓ build-and-test: PASS
   ├── ✓ code-quality: PASS
   ├── ✓ coverage-report: PASS
   └── ✓ Puedes mergear
   
   O si falla:
   ├── ✗ build-and-test: FAILED
   └── ✗ No puedes mergear (si está protegida la rama)
```

---

## 🔍 Cómo Verificar que Todo Funciona

### Local (antes de hacer push):
```powershell
# Asegúrate de que las pruebas pasan localmente
mvn clean verify

# Verifica la cobertura
mvn clean test jacoco:report
# Abre: target/site/jacoco/index.html
```

### En GitHub (después de hacer push):
```
1. Ve a: https://github.com/lm4038882-ui/actividad_4-testing-unisabana/actions
2. Verifica que el workflow "CI/CD Pipeline - Testing Workshop" esté corriendo
3. Espera a que termine (5-8 min)
4. Revisa el resumen:
   - ✓ Todos los jobs pasan = Pipeline OK
   - ✗ Alguno falla = Revisa los logs
```

---

## 📋 Requisitos Cumplidos

| Requisito | Status | Evidencia |
|-----------|--------|-----------|
| Script de pruebas automatizadas | ✓ | 4 suites de pruebas en el workflow |
| Pipeline CI/CD funcional | ✓ | `.github/workflows/ci-cd.yml` activo |
| Métricas de cubrimiento | ✓ | JaCoCo configurado, 80% mínimo |
| Reporte de resultados | ✓ | Artefactos auto-descargables |
| Restricción de integración | ⚠️ | Requiere activar branch protection |

---

## 🚀 Próximos Pasos

1. **Activa branch protection** en GitHub (ver sección anterior)
2. **Haz un push** a una rama feature para probar
3. **Abre un PR** hacia `main` o `develop`
4. **Verifica** que el pipeline se ejecuta y bloquea si falla
5. **Descarga** los reportes y comprueba que la cobertura cumple

---

## 📞 Troubleshooting

### El workflow no aparece en Actions
- **Causa**: El archivo `ci-cd.yml` no está en `.github/workflows/`
- **Solución**: Verifica que el archivo existe y está en la rama correcta

### El workflow comienza pero falla en `build-and-test`
- **Causa**: Error en compilación o en las pruebas
- **Solución**: Revisa los logs en GitHub Actions, copia el error y corre `mvn clean verify` localmente

### La cobertura falla pero localmente pasa
- **Causa**: Diferencias en versiones de Java (Java 21 vs 25)
- **Solución**: Asegúrate de que tu local usa Java 21 o 25

### No puedo mergear un PR aunque todo pasa
- **Causa**: Branch protection no está configurada o hay otras reglas
- **Solución**: Ve a **Settings** → **Branches** y verifica las reglas

---

## 📖 Referencias

- [GitHub Actions Documentation](https://docs.github.com/actions)
- [Branch Protection Rules](https://docs.github.com/repositories/configuring-branches-and-merges-in-your-repository/managing-a-branch-protection-rule)
- [JaCoCo Coverage Reports](https://www.eclemma.org/jacoco/)
- [Maven Verify Goal](https://maven.apache.org/plugins/maven-verify-plugin/)
