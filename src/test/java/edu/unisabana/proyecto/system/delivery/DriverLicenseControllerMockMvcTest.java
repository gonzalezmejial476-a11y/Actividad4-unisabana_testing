package edu.unisabana.proyecto.system.delivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.unisabana.proyecto.delivery.rest.DriverLicenseController;
import edu.unisabana.proyecto.infrastructure.persistence.DriverLicenseEntity;
import edu.unisabana.proyecto.application.DriverLicenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración del controlador REST con MockMvc.
 * Prueba los endpoints HTTP sin iniciar un servidor real.
 * 
 * AQUI SE USAN:
 * - @WebMvcTest: Cargaloa solo la capa web (MVC) sin Spring Boot context completo
 * - MockMvc: Simula requests HTTP al controlador
 * - @MockBean: Mockea el servicio para aislar el controlador
 */
@WebMvcTest(DriverLicenseController.class)
@DisplayName("DriverLicenseController - Integration Tests with MockMvc")
class DriverLicenseControllerMockMvcTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private DriverLicenseService serviceMock;
    
    private DriverLicenseEntity mockEntity;
    private DriverLicenseController.CreateLicenseRequest createRequest;
    
    @BeforeEach
    void setUp() {
        // Entidad simulada
        mockEntity = new DriverLicenseEntity(
            "1001", "Juan Pérez", 25,
            false, false, 0, "REGULAR", "PENDING"
        );
        mockEntity.setId(1L);
        
        // Request DTO
        createRequest = new DriverLicenseController.CreateLicenseRequest(
            "1001", "Juan Pérez", 25,
            false, false, 0, "REGULAR"
        );
    }
    
    @Nested
    @DisplayName("POST /api/licenses - Create License")
    class CreateLicenseEndpointTest {
        
        @Test
        @DisplayName("When POST valid license request Then should return 201 CREATED")
        void shouldCreateLicenseWithValidRequest() throws Exception {
            // ARRANGE
            when(serviceMock.createLicenseRequest(
                "1001", "Juan Pérez", 25,
                false, false, 0, "REGULAR"
            )).thenReturn(mockEntity);
            
            // ACT & ASSERT
            mockMvc.perform(post("/api/licenses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentId").value("1001"))
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"))
                .andExpect(jsonPath("$.status").value("PENDING"));
            
            verify(serviceMock, times(1)).createLicenseRequest(
                "1001", "Juan Pérez", 25, false, false, 0, "REGULAR"
            );
        }
        
        @Test
        @DisplayName("When POST invalid age Then should return 400 BAD REQUEST")
        void shouldReturnBadRequestWhenAgeInvalid() throws Exception {
            // ARRANGE - Age is 10 (invalid, min is 16)
            DriverLicenseController.CreateLicenseRequest invalidRequest = 
                new DriverLicenseController.CreateLicenseRequest(
                    "1001", "Young Person", 10,
                    false, false, 0, "REGULAR"
                );
            
            when(serviceMock.createLicenseRequest(anyString(), anyString(), eq(10),
                    anyBoolean(), anyBoolean(), anyInt(), anyString()))
                .thenThrow(new IllegalArgumentException("Age must be between 16 and 88"));
            
            // ACT & ASSERT
            mockMvc.perform(post("/api/licenses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
        }
    }
    
    @Nested
    @DisplayName("GET /api/licenses/document/{id} - Find by Document")
    class FindByDocumentEndpointTest {
        
        @Test
        @DisplayName("When GET with valid document Then should return 200 OK")
        void shouldFindLicenseByDocument() throws Exception {
            // ARRANGE
            when(serviceMock.findByDocumentId("1001"))
                .thenReturn(Optional.of(mockEntity));
            
            // ACT & ASSERT
            mockMvc.perform(get("/api/licenses/document/1001")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value("1001"))
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"));
            
            verify(serviceMock, times(1)).findByDocumentId("1001");
        }
        
        @Test
        @DisplayName("When GET with non-existent document Then should return 404 NOT FOUND")
        void shouldReturnNotFoundWhenDocumentDoesNotExist() throws Exception {
            // ARRANGE
            when(serviceMock.findByDocumentId("9999"))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            mockMvc.perform(get("/api/licenses/document/9999")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
            
            verify(serviceMock, times(1)).findByDocumentId("9999");
        }
    }
    
    @Nested
    @DisplayName("PUT /api/licenses/{document}/reject - Reject License")
    class RejectLicenseEndpointTest {
        
        @Test
        @DisplayName("When PUT reject with valid document Then should return 200 OK with REJECTED status")
        void shouldRejectLicense() throws Exception {
            // ARRANGE
            mockEntity.setStatus("REJECTED");
            mockEntity.setRejectionReason("Antecedentes penales");
            
            when(serviceMock.rejectLicense("1001", "Antecedentes penales"))
                .thenReturn(mockEntity);
            
            // ACT & ASSERT
            mockMvc.perform(put("/api/licenses/1001/reject")
                    .param("reason", "Antecedentes penales")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rejectionReason").value("Antecedentes penales"));
            
            verify(serviceMock, times(1)).rejectLicense("1001", "Antecedentes penales");
        }
        
        @Test
        @DisplayName("When PUT reject non-existent document Then should return 404 NOT FOUND")
        void shouldReturnNotFoundWhenRejectingNonExistent() throws Exception {
            // ARRANGE
            when(serviceMock.rejectLicense("9999", "Invalid"))
                .thenThrow(new IllegalArgumentException("Solicitud no encontrada: 9999"));
            
            // ACT & ASSERT
            mockMvc.perform(put("/api/licenses/9999/reject")
                    .param("reason", "Invalid")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
            
            verify(serviceMock, times(1)).rejectLicense("9999", "Invalid");
        }
    }
    
    @Nested
    @DisplayName("GET /api/licenses/pending - Get Pending Requests")
    class GetPendingRequestsEndpointTest {
        
        @Test
        @DisplayName("When GET pending Then should return list of PENDING licenses")
        void shouldGetPendingRequests() throws Exception {
            // ARRANGE
            when(serviceMock.getPendingRequests())
                .thenReturn(java.util.List.of(mockEntity));
            
            // ACT & ASSERT
            mockMvc.perform(get("/api/licenses/pending")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].documentId").value("1001"));
            
            verify(serviceMock, times(1)).getPendingRequests();
        }
    }
    
    @Nested
    @DisplayName("GET /api/licenses/approved - Get Approved Licenses")
    class GetApprovedLicensesEndpointTest {
        
        @Test
        @DisplayName("When GET approved Then should return list of APPROVED licenses")
        void shouldGetApprovedLicenses() throws Exception {
            // ARRANGE
            mockEntity.setStatus("APPROVED");
            when(serviceMock.getApprovedLicenses())
                .thenReturn(java.util.List.of(mockEntity));
            
            // ACT & ASSERT
            mockMvc.perform(get("/api/licenses/approved")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
            
            verify(serviceMock, times(1)).getApprovedLicenses();
        }
    }
}
