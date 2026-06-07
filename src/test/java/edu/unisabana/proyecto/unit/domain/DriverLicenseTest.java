package edu.unisabana.proyecto.unit.domain;

import edu.unisabana.proyecto.domain.DriverLicense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * DriverLicenseTest: Suite de pruebas unitarias para licencia de conducción en Colombia.
 * 
 * DOMINIO: Validar elegibilidad para obtener licencia de conducción de CARROS.
 * 
 * REGLAS COLOMBIA:
 * - Edad mínima conducción regular: 16 años
 * - Edad mínima servicio público (taxis, uber, buses): 23 años
 * - Edad máxima: 88 años
 * - Discapacidad visual severa: NO puede conducir
 * - Antecedentes penales: NO puede obtener licencia
 * 
 * HISTORIA TDD:
 * ITERACIÓN 1 (RED): Test isAdult - persona >= 16 años
 * ITERACIÓN 2 (GREEN): Implementación mínima validación edad
 * ITERACIÓN 3 (REFACTOR): Extracción de constantes MIN_AGE, MAX_AGE
 */
@DisplayName("DriverLicense - Test Suite (Sistema Colombiano de Licencia de Conducción)")
class DriverLicenseTest {

    private DriverLicense regularAdult;
    private DriverLicense publicServiceDriver;
    private DriverLicense youngPerson;
    private DriverLicense senior;

    @BeforeEach
    void setUp() {
        // ARRANGE: Preparar instancias de prueba
        regularAdult = new DriverLicense("1001", "Juan Pérez García", 25, 
                                        false, false, 0, "REGULAR");
        
        publicServiceDriver = new DriverLicense("1002", "Carlos Sánchez López", 30,
                                               false, false, 0, "PUBLIC_SERVICE");
        
        youngPerson = new DriverLicense("1003", "María López Rodríguez", 17,
                                       false, false, 0, "REGULAR");
        
        senior = new DriverLicense("1004", "Don Pedro García", 72,
                                  false, false, 0, "REGULAR");
    }

    @Nested
    @DisplayName("Validación de Edad - Clases de Equivalencia")
    class AgeValidationTest {

        @Test
        @DisplayName("Should throw exception when age is negative")
        void shouldThrowExceptionWhenAgeIsNegative() {
            // ARRANGE & ACT & ASSERT
            assertThatThrownBy(() -> new DriverLicense("1", "Test", -5, false, false, 0, "REGULAR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser negativa");
        }

        @Test
        @DisplayName("Should throw exception when age exceeds realistic range (150)")
        void shouldThrowExceptionWhenAgeIsUnrealistic() {
            // ARRANGE & ACT & ASSERT
            assertThatThrownBy(() -> new DriverLicense("1", "Test", 151, false, false, 0, "REGULAR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("debe ser realista");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 5, 10, 15})
        @DisplayName("Should reject driving license for children under 16 (TOO_YOUNG)")
        void shouldRejectChildrenUnder16(int age) {
            // ARRANGE & ACT
            DriverLicense child = new DriverLicense("1", "Child", age, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(child.getAgeCategory()).isEqualTo("TOO_YOUNG");
            assertThat(child.isAdultForRegularLicense()).isFalse();
        }

        @ParameterizedTest
        @ValueSource(ints = {16, 17})
        @DisplayName("Should allow restricted license for adolescents (16-17 years)")
        void shouldAllowRestrictedLicenseForAdolescents(int age) {
            // ARRANGE & ACT
            DriverLicense adolescent = new DriverLicense("1", "Teen", age, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(adolescent.getAgeCategory()).isEqualTo("ADOLESCENT");
            assertThat(adolescent.isAdultForRegularLicense()).isTrue();
            assertThat(adolescent.isAdultForPublicService()).isFalse();
        }

        @ParameterizedTest
        @ValueSource(ints = {18, 19, 20, 21, 22})
        @DisplayName("Should allow regular license for young adults (18-22 years)")
        void shouldAllowYoungAdults(int age) {
            // ARRANGE & ACT
            DriverLicense youngAdult = new DriverLicense("1", "Young", age, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(youngAdult.getAgeCategory()).isEqualTo("YOUNG_ADULT");
            assertThat(youngAdult.isAdultForRegularLicense()).isTrue();
            assertThat(youngAdult.isAdultForPublicService()).isFalse();
        }

        @ParameterizedTest
        @ValueSource(ints = {23, 30, 40, 64})
        @DisplayName("Should allow regular and public service licenses for adults (23-64 years)")
        void shouldAllowFullLicenseAdults(int age) {
            // ARRANGE & ACT
            DriverLicense adult = new DriverLicense("1", "Adult", age, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(adult.getAgeCategory()).isEqualTo("ADULT");
            assertThat(adult.isAdultForRegularLicense()).isTrue();
            assertThat(adult.isAdultForPublicService()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(ints = {65, 70, 75, 80, 85, 88})
        @DisplayName("Should allow license with renewal for seniors (65-88 years)")
        void shouldAllowSeniorsWithRenewal(int age) {
            // ARRANGE & ACT
            DriverLicense senior = new DriverLicense("1", "Senior", age, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(senior.getAgeCategory()).isEqualTo("SENIOR");
            assertThat(senior.isWithinMaximumAge()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(ints = {89, 90, 100})
        @DisplayName("Should reject licenses for people over 88 years (TOO_OLD)")
        void shouldRejectOver88Years(int age) {
            // ARRANGE & ACT
            DriverLicense tooOld = new DriverLicense("1", "TooOld", age, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(tooOld.getAgeCategory()).isEqualTo("TOO_OLD");
            assertThat(tooOld.isWithinMaximumAge()).isFalse();
            assertThat(tooOld.isEligibleForLicense()).isFalse();
        }
    }

    @Nested
    @DisplayName("Licencia de Servicio Público (Mínimo 23 años) - BDD")
    class PublicServiceLicenseTest {

        @Test
        @DisplayName("Given a 22-year-old When applying for public service license Then should be rejected (too young)")
        void shouldRejectPublicServiceUnder23() {
            // ARRANGE
            DriverLicense youngDriver = new DriverLicense("1", "Young", 22, 
                                                         false, false, 0, "PUBLIC_SERVICE");
            
            // ACT
            boolean isEligible = youngDriver.isEligibleForLicense();
            
            // ASSERT
            assertThat(isEligible).isFalse();
            assertThat(youngDriver.isAdultForPublicService()).isFalse();
            assertThat(youngDriver.yearsUntilPublicService()).isEqualTo(1);
        }

        @Test
        @DisplayName("Given a 23-year-old When applying for public service license Then should be approved")
        void shouldApprovePublicService23Years() {
            // ARRANGE
            DriverLicense driver = new DriverLicense("1", "Driver", 23,
                                                    false, false, 0, "PUBLIC_SERVICE");
            
            // ACT
            boolean isEligible = driver.isEligibleForLicense();
            
            // ASSERT
            assertThat(isEligible).isTrue();
            assertThat(driver.isAdultForPublicService()).isTrue();
            assertThat(driver.yearsUntilPublicService()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should calculate years until eligible for public service")
        void shouldCalculateYearsUntilPublicService() {
            // ARRANGE
            DriverLicense driver = new DriverLicense("1", "Driver", 18,
                                                    false, false, 0, "REGULAR");
            
            // ACT
            int years = driver.yearsUntilPublicService();
            
            // ASSERT
            assertThat(years).isEqualTo(5); // 23 - 18 = 5
        }
    }

    @Nested
    @DisplayName("Discapacidad Visual Severa - BDD")
    class EyeDisabilityTest {

        @Test
        @DisplayName("Given a person with severe eye disability When checking eligibility Then should be rejected")
        void shouldRejectWithSevereEyeDisability() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "Person", 25,
                                                    true, // hasSevereEyeDisability
                                                    false, 0, "REGULAR");
            
            // ACT
            boolean isEligible = person.isEligibleForLicense();
            
            // ASSERT
            assertThat(isEligible).isFalse();
            assertThat(person.canDriveWithEyeDisability()).isFalse();
        }

        @Test
        @DisplayName("Given a person without eye disability When checking eligibility Then should pass this check")
        void shouldApproveWithoutEyeDisability() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "Person", 25,
                                                    false, // No discapacidad ocular
                                                    false, 0, "REGULAR");
            
            // ACT
            boolean canDrive = person.canDriveWithEyeDisability();
            
            // ASSERT
            assertThat(canDrive).isTrue();
        }
    }

    @Nested
    @DisplayName("Discapacidad Auditiva - BDD")
    class HearingDisabilityTest {

        @Test
        @DisplayName("Given a person with hearing disability When checking driving capability Then should be allowed (with adaptations)")
        void shouldAllowHearingDisability() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "Person", 25,
                                                    false, true, // hasHearingDisability
                                                    0, "REGULAR");
            
            // ACT
            boolean canDrive = person.canDriveWithHearingDisability();
            
            // ASSERT
            assertThat(canDrive).isTrue(); // En Colombia, discapacidad auditiva permite conducción
        }
    }

    @Nested
    @DisplayName("Antecedentes Penales Graves - BDD")
    class CriminalRecordsTest {

        @Test
        @DisplayName("Should throw exception when criminal records is negative")
        void shouldThrowExceptionWhenNegativeRecords() {
            // ARRANGE & ACT & ASSERT
            assertThatThrownBy(() -> new DriverLicense("1", "Test", 25, false, false, -1, "REGULAR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser negativo");
        }

        @Test
        @DisplayName("Given a person with clean criminal record When checking eligibility Then should pass this check")
        void shouldApproveCleanRecord() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "Clean", 25, false, false, 0, "REGULAR");
            
            // ACT
            boolean hasClean = person.hasCleanCriminalRecord();
            
            // ASSERT
            assertThat(hasClean).isTrue();
            assertThat(person.getCriminalRecordCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("Given a person with serious criminal records When checking eligibility Then should be rejected")
        void shouldRejectWithCriminalRecords() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "Criminal", 25,
                                                    false, false, 2, "REGULAR");
            
            // ACT
            boolean isEligible = person.isEligibleForLicense();
            
            // ASSERT
            assertThat(isEligible).isFalse();
            assertThat(person.hasCleanCriminalRecord()).isFalse();
            assertThat(person.getCriminalRecordCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Validación de Documento y Tipo de Licencia")
    class DocumentAndTypeValidationTest {

        @Test
        @DisplayName("Should throw exception when document ID is empty")
        void shouldThrowExceptionWhenDocumentEmpty() {
            // ARRANGE & ACT & ASSERT
            assertThatThrownBy(() -> new DriverLicense("", "Test", 25, false, false, 0, "REGULAR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("documento no puede estar vacío");
        }

        @Test
        @DisplayName("Should throw exception when license type is invalid")
        void shouldThrowExceptionWhenInvalidLicenseType() {
            // ARRANGE & ACT & ASSERT
            assertThatThrownBy(() -> new DriverLicense("1", "Test", 25, false, false, 0, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }

        @ParameterizedTest
        @ValueSource(strings = {"REGULAR", "PUBLIC_SERVICE"})
        @DisplayName("Should accept valid license types")
        void shouldAcceptValidLicenseTypes(String type) {
            // ARRANGE & ACT
            DriverLicense license = new DriverLicense("1", "Test", 25, false, false, 0, type);
            
            // ASSERT
            assertThat(license.getLicenseType()).isEqualTo(type);
        }
    }

    @Nested
    @DisplayName("Aprobación y Rechazo de Licencia")
    class LicenseApprovalTest {

        @Test
        @DisplayName("Given eligible person When approving license Then status should be APPROVED")
        void shouldApproveLicense() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "Eligible", 25,
                                                    false, false, 0, "REGULAR");
            
            // ACT
            person.approveLicense();
            
            // ASSERT
            assertThat(person.getStatus()).isEqualTo("APPROVED");
        }

        @Test
        @DisplayName("Given ineligible person When approving license Then should throw exception")
        void shouldThrowExceptionWhenApprovingIneligible() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "Ineligible", 15,
                                                    false, false, 0, "REGULAR");
            
            // ACT & ASSERT
            assertThatThrownBy(() -> person.approveLicense())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no cumple");
        }

        @Test
        @DisplayName("Should suspend and reactivate license")
        void shouldSuspendAndReactivate() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "Driver", 25,
                                                    false, false, 0, "REGULAR");
            person.approveLicense();
            
            // ACT
            person.suspendLicense();
            
            // ASSERT
            assertThat(person.getStatus()).isEqualTo("SUSPENDED");
            
            // Reactivate
            person.reactivateLicense();
            assertThat(person.getStatus()).isEqualTo("APPROVED");
        }
    }

    @Nested
    @DisplayName("Elegibilidad General (Todos los requisitos)")
    class FullEligibilityTest {

        @Test
        @DisplayName("Should be eligible when all requirements are met")
        void shouldBeEligibleWhenAllRequirementsMet() {
            // ARRANGE & ACT
            boolean isEligible = regularAdult.isEligibleForLicense();
            
            // ASSERT
            assertThat(isEligible).isTrue();
        }

        @Test
        @DisplayName("Should get rejection reason when not eligible")
        void shouldGetRejectionReason() {
            // ARRANGE
            DriverLicense person = new DriverLicense("1", "TooYoung", 15,
                                                    false, false, 0, "REGULAR");
            
            // ACT
            String reason = person.getRejectionReason();
            
            // ASSERT
            assertThat(reason).contains("edad mínima");
        }
    }

    @Nested
    @DisplayName("Valores Límite - Boundary Values")
    class BoundaryValuesTest {

        @Test
        @DisplayName("BV: Age = 15 (just before minimum 16)")
        void boundaryValue_Age15() {
            // ARRANGE & ACT
            DriverLicense person = new DriverLicense("1", "Test", 15, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(person.isAdultForRegularLicense()).isFalse();
            assertThat(person.getAgeCategory()).isEqualTo("TOO_YOUNG");
        }

        @Test
        @DisplayName("BV: Age = 16 (minimum for regular license)")
        void boundaryValue_Age16() {
            // ARRANGE & ACT
            DriverLicense person = new DriverLicense("1", "Test", 16, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(person.isAdultForRegularLicense()).isTrue();
            assertThat(person.getAgeCategory()).isEqualTo("ADOLESCENT");
        }

        @Test
        @DisplayName("BV: Age = 22 (just before public service minimum 23)")
        void boundaryValue_Age22() {
            // ARRANGE & ACT
            DriverLicense person = new DriverLicense("1", "Test", 22,
                                                    false, false, 0, "PUBLIC_SERVICE");
            
            // ASSERT
            assertThat(person.isAdultForPublicService()).isFalse();
            assertThat(person.yearsUntilPublicService()).isEqualTo(1);
        }

        @Test
        @DisplayName("BV: Age = 23 (minimum for public service)")
        void boundaryValue_Age23() {
            // ARRANGE & ACT
            DriverLicense person = new DriverLicense("1", "Test", 23,
                                                    false, false, 0, "PUBLIC_SERVICE");
            
            // ASSERT
            assertThat(person.isAdultForPublicService()).isTrue();
            assertThat(person.yearsUntilPublicService()).isEqualTo(0);
        }

        @Test
        @DisplayName("BV: Age = 88 (maximum age)")
        void boundaryValue_Age88() {
            // ARRANGE & ACT
            DriverLicense person = new DriverLicense("1", "Test", 88, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(person.isWithinMaximumAge()).isTrue();
            assertThat(person.getAgeCategory()).isEqualTo("SENIOR");
            assertThat(person.isEligibleForLicense()).isTrue();
        }

        @Test
        @DisplayName("BV: Age = 89 (just after maximum 88)")
        void boundaryValue_Age89() {
            // ARRANGE & ACT
            DriverLicense person = new DriverLicense("1", "Test", 89, false, false, 0, "REGULAR");
            
            // ASSERT
            assertThat(person.isWithinMaximumAge()).isFalse();
            assertThat(person.getAgeCategory()).isEqualTo("TOO_OLD");
            assertThat(person.isEligibleForLicense()).isFalse();
        }
    }

    @Nested
    @DisplayName("Getters y Atributos")
    class AttributesTest {

        @Test
        @DisplayName("Should retrieve all driver attributes correctly")
        void shouldRetrieveAllAttributes() {
            // ARRANGE & ACT & ASSERT
            assertThat(regularAdult.getDocumentId()).isEqualTo("1001");
            assertThat(regularAdult.getFullName()).isEqualTo("Juan Pérez García");
            assertThat(regularAdult.getAge()).isEqualTo(25);
            assertThat(regularAdult.getLicenseType()).isEqualTo("REGULAR");
            assertThat(regularAdult.getStatus()).isEqualTo("PENDING");
        }
    }
}
