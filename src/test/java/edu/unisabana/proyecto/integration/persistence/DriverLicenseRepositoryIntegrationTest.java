package edu.unisabana.proyecto.integration.persistence;

import edu.unisabana.proyecto.infrastructure.persistence.DriverLicenseEntity;
import edu.unisabana.proyecto.infrastructure.persistence.DriverLicenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de integración del repositorio con H2.
 * Utiliza una base de datos H2 en memoria (real, no mock).
 * 
 * AQUI SE USA H2:
 * - @DataJpaTest: Crea un contexto Spring con H2 en memoria
 * - @TestPropertySource: Carga la configuración de H2 para tests
 * - DriverLicenseRepository: Interactúa con la BD H2 real
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("DriverLicenseRepository - Tests de Integración con H2")
class DriverLicenseRepositoryIntegrationTest {
    
    @Autowired
    private DriverLicenseRepository repository;
    
    private DriverLicenseEntity entity1;
    private DriverLicenseEntity entity2;
    
    @BeforeEach
    void setUp() {
        // Limpiar antes de cada test
        repository.deleteAll();
        
        // Crear entidades para persistir en H2
        entity1 = new DriverLicenseEntity(
            "1001", "Juan Pérez", 25,
            false, false, 0, "REGULAR", "PENDING"
        );
        
        entity2 = new DriverLicenseEntity(
            "1002", "María García", 30,
            false, false, 0, "PUBLIC_SERVICE", "APPROVED"
        );
    }
    
    @Nested
    @DisplayName("Persistencia básica")
    class PersistenceTest {
        
        @Test
        @DisplayName("When saving entity Then H2 should persist it")
        void shouldPersistInH2() {
            // ACT - Guarda en H2
            DriverLicenseEntity saved = repository.save(entity1);
            
            // ASSERT
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getDocumentId()).isEqualTo("1001");
        }
        
        @Test
        @DisplayName("When reading from H2 Then should return persisted entity")
        void shouldReadFromH2() {
            // ARRANGE - Guarda en H2
            repository.save(entity1);
            
            // ACT - Lee de H2
            Optional<DriverLicenseEntity> found = repository.findByDocumentId("1001");
            
            // ASSERT
            assertThat(found).isPresent();
            assertThat(found.get().getFullName()).isEqualTo("Juan Pérez");
        }

        @Test
        @DisplayName("When saving multiple entities Then count should reflect them")
        void shouldCountSavedEntities() {
            repository.save(entity1);
            repository.save(entity2);

            assertThat(repository.count()).isEqualTo(2);
        }
    }
    
    @Nested
    @DisplayName("Búsquedas en H2")
    class QueryTest {
        
        @Test
        @DisplayName("When finding by document in H2 Then should return correct entity")
        void shouldFindByDocumentId() {
            // ARRANGE
            repository.save(entity1);
            repository.save(entity2);
            
            // ACT
            Optional<DriverLicenseEntity> result = repository.findByDocumentId("1002");
            
            // ASSERT
            assertThat(result).isPresent();
            assertThat(result.get().getFullName()).isEqualTo("María García");
            assertThat(result.get().getLicenseType()).isEqualTo("PUBLIC_SERVICE");
        }
        
        @Test
        @DisplayName("When finding by status in H2 Then should return matching entities")
        void shouldFindByStatus() {
            // ARRANGE
            repository.save(entity1);
            repository.save(entity2);
            
            // ACT
            var pending = repository.findByStatus("PENDING");
            var approved = repository.findByStatus("APPROVED");
            
            // ASSERT
            assertThat(pending).hasSize(1);
            assertThat(pending.get(0).getDocumentId()).isEqualTo("1001");
            
            assertThat(approved).hasSize(1);
            assertThat(approved.get(0).getDocumentId()).isEqualTo("1002");
        }
    }
    
    @Nested
    @DisplayName("Actualizaciones en H2")
    class UpdateTest {
        
        @Test
        @DisplayName("When updating entity in H2 Then changes should persist")
        void shouldUpdateInH2() {
            // ARRANGE
            DriverLicenseEntity saved = repository.save(entity1);
            
            // ACT - Modifica y guarda nuevamente en H2
            saved.setStatus("APPROVED");
            repository.save(saved);
            
            // ASSERT
            Optional<DriverLicenseEntity> updated = repository.findByDocumentId("1001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getStatus()).isEqualTo("APPROVED");
        }
    }
    
    @Nested
    @DisplayName("Eliminación en H2")
    class DeletionTest {
        
        @Test
        @DisplayName("When deleting entity from H2 Then should not be found")
        void shouldDeleteFromH2() {
            // ARRANGE
            DriverLicenseEntity saved = repository.save(entity1);
            
            // ACT
            repository.delete(saved);
            
            // ASSERT
            Optional<DriverLicenseEntity> deleted = repository.findByDocumentId("1001");
            assertThat(deleted).isEmpty();
        }
    }

    @Nested
    @DisplayName("Restricciones y búsquedas adicionales en H2")
    class AdditionalH2Tests {

        @Test
        @DisplayName("When no licenses match status Then should return empty list")
        void shouldReturnEmptyListForUnknownStatus() {
            repository.save(entity1);
            repository.save(entity2);

            var result = repository.findByStatus("REJECTED");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("When saving duplicate documentId Then H2 should enforce unique constraint")
        void shouldEnforceUniqueDocumentIdConstraint() {
            repository.saveAndFlush(entity1);

            DriverLicenseEntity duplicate = new DriverLicenseEntity(
                "1001", "Duplicado", 40,
                false, false, 0, "REGULAR", "PENDING"
            );

            assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
