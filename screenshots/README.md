# Capturas de evidencia

Este directorio está destinado a almacenar capturas de pantalla y evidencias del taller.

## Archivos recomendados

- `screenshots/jacoco-coverage.png` - Captura del reporte JaCoCo con el porcentaje de cobertura.
- `screenshots/verify-success.png` - Captura de la ejecución `mvn verify` exitosa.
- `screenshots/h2-test.png` - Captura de la ejecución de pruebas de integración con H2.
- `screenshots/mocks-test.png` - Captura de la ejecución de pruebas que muestran el uso de mocks.

## Cómo generar las capturas

1. Abra el reporte HTML de JaCoCo en un navegador:
   - `target/site/jacoco/index.html`
2. Tome una captura de pantalla del porcentaje de líneas cubiertas.
3. Ejecute los comandos de prueba clave y capture las salidas en pantalla:
   - `mvn clean test`
   - `mvn verify`
   - `mvn -Dtest=DriverLicenseRepositoryIntegrationTest test`
   - `mvn -Dtest=DriverLicenseServiceTest test`
   - `mvn -Dtest=DriverLicenseControllerMockMvcTest test`

## Uso

Guarde las capturas en este directorio y utilice las rutas relativas en la documentación del taller.
