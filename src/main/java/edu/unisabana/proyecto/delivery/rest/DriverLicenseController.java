package edu.unisabana.proyecto.delivery.rest;

import edu.unisabana.proyecto.domain.DriverLicense;
import edu.unisabana.proyecto.infrastructure.persistence.DriverLicenseEntity;
import edu.unisabana.proyecto.application.DriverLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para solicitudes de licencias de conducción.
 * Expone endpoints HTTP para crear, consultar y actualizar solicitudes.
 * 
 * ENDPOINTS:
 * POST   /api/licenses              - Crear nueva solicitud
 * GET    /api/licenses/{id}         - Obtener solicitud por ID
 * GET    /api/licenses/document/{id} - Obtener por documento
 * PUT    /api/licenses/{id}/approve - Aprobar solicitud
 * PUT    /api/licenses/{id}/reject  - Rechazar solicitud
 * GET    /api/licenses/status/{status} - Listar por estado
 */
@RestController
@RequestMapping("/api/licenses")
public class DriverLicenseController {
    
    @Autowired
    private DriverLicenseService service;
    
    /**
     * Crea una nueva solicitud de licencia
     */
    @PostMapping
    public ResponseEntity<?> createLicense(@RequestBody CreateLicenseRequest request) {
        try {
            // Valida primero con la lógica de dominio
            new DriverLicense(
                request.getDocumentId(),
                request.getFullName(),
                request.getAge(),
                request.isHasSevereEyeDisability(),
                request.isHasHearingDisability(),
                request.getSeriousCriminalRecords(),
                request.getLicenseType()
            );
            
            // Persiste en H2
            DriverLicenseEntity created = service.createLicenseRequest(
                request.getDocumentId(),
                request.getFullName(),
                request.getAge(),
                request.isHasSevereEyeDisability(),
                request.isHasHearingDisability(),
                request.getSeriousCriminalRecords(),
                request.getLicenseType()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation Error", e.getMessage()));
        }
    }
    
    /**
     * Obtiene una solicitud por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLicense(@PathVariable Long id) {
        // En una aplicación real, esto buscaría por ID
        // Por ahora, retornamos 404
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Not Found", "License with ID " + id + " not found"));
    }
    
    /**
     * Obtiene una solicitud por número de documento
     */
    @GetMapping("/document/{documentId}")
    public ResponseEntity<?> getLicenseByDocument(@PathVariable String documentId) {
        Optional<DriverLicenseEntity> license = service.findByDocumentId(documentId);
        
        if (license.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not Found", "License with document " + documentId + " not found"));
        }
        
        return ResponseEntity.ok(license.get());
    }
    
    /**
     * Aprueba una solicitud
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLicense(@PathVariable Long id) {
        try {
            // En una aplicación real, buscaríamos por ID
            // Por ahora, retornamos error
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not Found", "License with ID " + id + " not found"));
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Error", e.getMessage()));
        }
    }
    
    /**
     * Rechaza una solicitud con motivo
     */
    @PutMapping("/{documentId}/reject")
    public ResponseEntity<?> rejectLicense(
            @PathVariable String documentId,
            @RequestParam String reason) {
        try {
            DriverLicenseEntity rejected = service.rejectLicense(documentId, reason);
            return ResponseEntity.ok(rejected);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not Found", e.getMessage()));
        }
    }
    
    /**
     * Lista todas las solicitudes pendientes
     */
    @GetMapping("/pending")
    public ResponseEntity<List<DriverLicenseEntity>> getPendingRequests() {
        List<DriverLicenseEntity> pending = service.getPendingRequests();
        return ResponseEntity.ok(pending);
    }
    
    /**
     * Lista todas las licencias aprobadas
     */
    @GetMapping("/approved")
    public ResponseEntity<List<DriverLicenseEntity>> getApprovedLicenses() {
        List<DriverLicenseEntity> approved = service.getApprovedLicenses();
        return ResponseEntity.ok(approved);
    }
    
    /**
     * DTO para crear una nueva solicitud
     */
    public static class CreateLicenseRequest {
        private String documentId;
        private String fullName;
        private int age;
        private boolean hasSevereEyeDisability;
        private boolean hasHearingDisability;
        private int seriousCriminalRecords;
        private String licenseType;

        public CreateLicenseRequest() {}

        public CreateLicenseRequest(String documentId, String fullName, int age,
                                   boolean hasSevereEyeDisability, boolean hasHearingDisability,
                                   int seriousCriminalRecords, String licenseType) {
            this.documentId = documentId;
            this.fullName = fullName;
            this.age = age;
            this.hasSevereEyeDisability = hasSevereEyeDisability;
            this.hasHearingDisability = hasHearingDisability;
            this.seriousCriminalRecords = seriousCriminalRecords;
            this.licenseType = licenseType;
        }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public boolean isHasSevereEyeDisability() { return hasSevereEyeDisability; }
        public void setHasSevereEyeDisability(boolean hasSevereEyeDisability) { this.hasSevereEyeDisability = hasSevereEyeDisability; }

        public boolean isHasHearingDisability() { return hasHearingDisability; }
        public void setHasHearingDisability(boolean hasHearingDisability) { this.hasHearingDisability = hasHearingDisability; }

        public int getSeriousCriminalRecords() { return seriousCriminalRecords; }
        public void setSeriousCriminalRecords(int seriousCriminalRecords) { this.seriousCriminalRecords = seriousCriminalRecords; }

        public String getLicenseType() { return licenseType; }
        public void setLicenseType(String licenseType) { this.licenseType = licenseType; }
    }
    
    /**
     * Respuesta de error
     */
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
