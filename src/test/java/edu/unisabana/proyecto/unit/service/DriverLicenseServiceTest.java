package edu.unisabana.proyecto.unit.service;

import edu.unisabana.proyecto.application.DriverLicenseService;
import edu.unisabana.proyecto.infrastructure.persistence.DriverLicenseEntity;
import edu.unisabana.proyecto.infrastructure.persistence.DriverLicenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del servicio DriverLicenseService.
 * Utiliza Mockito para mockear el repositorio y evitar acceso a la BD real.
 * 
 * AQUI SE USAN LOS MOCKS:
 * - @Mock DriverLicenseRepository: Simula la base de datos sin usar H2
 * - @InjectMocks DriverLicenseService: Inyecta el mock en el servicio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DriverLicenseService - Tests con Mockito")
class DriverLicenseServiceTest {
    
    @Mock
    private DriverLicenseRepository repositoryMock;
    
    @InjectMocks
    private DriverLicenseService service;
    
    private DriverLicenseEntity mockEntity;
    
    @BeforeEach
    void setUp() {
        // Crea una entidad simulada para usar en los mocks
        mockEntity = new DriverLicenseEntity(
            "1001", "Juan Pérez", 25,
            false, false, 0, "REGULAR", "PENDING"
        );
        mockEntity.setId(1L);
    }
    
    @Nested
    @DisplayName("Creación de solicitud")
    class CreateLicenseRequestTest {
        
        @Test
        @DisplayName("When creating license Then repository.save() should be called")
        void shouldCallRepositorySaveWhenCreating() {
            // ARRANGE - Configura el mock para retornar la entidad simulada
            when(repositoryMock.save(any(DriverLicenseEntity.class)))
                .thenReturn(mockEntity);
            
            // ACT - Llama al servicio
            DriverLicenseEntity result = service.createLicenseRequest(
                "1001", "Juan Pérez", 25,
                false, false, 0, "REGULAR"
            );
            
            // ASSERT - Verifica que se llamó save() exactamente una vez
            verify(repositoryMock, times(1)).save(any(DriverLicenseEntity.class));
            assertThat(result).isNotNull();
            assertThat(result.getDocumentId()).isEqualTo("1001");
            assertThat(result.getStatus()).isEqualTo("PENDING");
        }
    }
    
    @Nested
    @DisplayName("Búsqueda por documento")
    class FindByDocumentTest {
        
        @Test
        @DisplayName("When finding by document Then mock should return entity")
        void shouldReturnEntityWhenFound() {
            // ARRANGE - Configura el mock para retornar la entidad cuando se busca
            when(repositoryMock.findByDocumentId("1001"))
                .thenReturn(Optional.of(mockEntity));
            
            // ACT
            Optional<DriverLicenseEntity> result = service.findByDocumentId("1001");
            
            // ASSERT
            verify(repositoryMock, times(1)).findByDocumentId("1001");
            assertThat(result).isPresent();
            assertThat(result.get().getFullName()).isEqualTo("Juan Pérez");
        }
        
        @Test
        @DisplayName("When document not found Then mock should return empty")
        void shouldReturnEmptyWhenNotFound() {
            // ARRANGE - Configura el mock para retornar vacío
            when(repositoryMock.findByDocumentId("9999"))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<DriverLicenseEntity> result = service.findByDocumentId("9999");
            
            // ASSERT
            verify(repositoryMock, times(1)).findByDocumentId("9999");
            assertThat(result).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Aprobación de solicitud")
    class ApproveLicenseTest {
        
        @Test
        @DisplayName("When approving license Then status should be APPROVED")
        void shouldApproveLicense() {
            // ARRANGE
            mockEntity.setStatus("PENDING");
            when(repositoryMock.findByDocumentId("1001"))
                .thenReturn(Optional.of(mockEntity));
            when(repositoryMock.save(any(DriverLicenseEntity.class)))
                .thenReturn(mockEntity);
            
            // ACT
            DriverLicenseEntity result = service.approveLicense("1001");
            
            // ASSERT
            verify(repositoryMock).findByDocumentId("1001");
            verify(repositoryMock).save(any(DriverLicenseEntity.class));
            assertThat(result.getStatus()).isEqualTo("APPROVED");
        }
        
        @Test
        @DisplayName("When approving non-existent license Then should throw exception")
        void shouldThrowExceptionWhenDocumentNotFound() {
            // ARRANGE
            when(repositoryMock.findByDocumentId(anyString()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> service.approveLicense("9999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Solicitud no encontrada: 9999");
            
            verify(repositoryMock).findByDocumentId("9999");
            verify(repositoryMock, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("Rechazo de solicitud")
    class RejectLicenseTest {
        
        @Test
        @DisplayName("When rejecting license Then status should be REJECTED with reason")
        void shouldRejectLicense() {
            // ARRANGE
            when(repositoryMock.findByDocumentId("1001"))
                .thenReturn(Optional.of(mockEntity));
            when(repositoryMock.save(any(DriverLicenseEntity.class)))
                .thenReturn(mockEntity);
            
            // ACT
            DriverLicenseEntity result = service.rejectLicense("1001", "Antecedentes penales");
            
            // ASSERT
            verify(repositoryMock).findByDocumentId("1001");
            verify(repositoryMock).save(any(DriverLicenseEntity.class));
            assertThat(result.getStatus()).isEqualTo("REJECTED");
            assertThat(result.getRejectionReason()).isEqualTo("Antecedentes penales");
        }
    }
    
    @Nested
    @DisplayName("Suspensión de licencia")
    class SuspendLicenseTest {
        
        @Test
        @DisplayName("When suspending license Then status should be SUSPENDED")
        void shouldSuspendLicense() {
            // ARRANGE
            mockEntity.setStatus("APPROVED");
            when(repositoryMock.findByDocumentId("1001"))
                .thenReturn(Optional.of(mockEntity));
            when(repositoryMock.save(any(DriverLicenseEntity.class)))
                .thenReturn(mockEntity);
            
            // ACT
            DriverLicenseEntity result = service.suspendLicense("1001");
            
            // ASSERT
            verify(repositoryMock).findByDocumentId("1001");
            verify(repositoryMock).save(any(DriverLicenseEntity.class));
            assertThat(result.getStatus()).isEqualTo("SUSPENDED");
        }
    }
}
