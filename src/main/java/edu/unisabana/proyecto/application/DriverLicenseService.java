package edu.unisabana.proyecto.application;

import edu.unisabana.proyecto.domain.DriverLicense;
import edu.unisabana.proyecto.infrastructure.persistence.DriverLicenseEntity;
import edu.unisabana.proyecto.infrastructure.persistence.DriverLicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para solicitudes de licencia de conducción.
 * Orquesta la lógica de negocio (DriverLicense) con la persistencia (Repository).
 */
@Service
public class DriverLicenseService {
    
    @Autowired
    private DriverLicenseRepository repository;
    
    /**
     * Crea una nueva solicitud de licencia y la persiste en H2.
     */
    public DriverLicenseEntity createLicenseRequest(String documentId, String fullName, int age,
                                                     boolean hasSevereEyeDisability,
                                                     boolean hasHearingDisability,
                                                     int seriousCriminalRecords,
                                                     String licenseType) {
        // Valida con la lógica de dominio
        DriverLicense domainLicense = new DriverLicense(
            documentId, fullName, age,
            hasSevereEyeDisability, hasHearingDisability,
            seriousCriminalRecords, licenseType
        );
        
        // Persiste en H2
        DriverLicenseEntity entity = new DriverLicenseEntity(
            documentId, fullName, age,
            hasSevereEyeDisability, hasHearingDisability,
            seriousCriminalRecords, licenseType, "PENDING"
        );
        
        return repository.save(entity);
    }
    
    /**
     * Busca una solicitud por documento
     */
    public Optional<DriverLicenseEntity> findByDocumentId(String documentId) {
        return repository.findByDocumentId(documentId);
    }
    
    /**
     * Aprueba una solicitud
     */
    public DriverLicenseEntity approveLicense(String documentId) {
        Optional<DriverLicenseEntity> optionalLicense = repository.findByDocumentId(documentId);
        
        if (optionalLicense.isEmpty()) {
            throw new IllegalArgumentException("Solicitud no encontrada: " + documentId);
        }
        
        DriverLicenseEntity license = optionalLicense.get();
        license.setStatus("APPROVED");
        return repository.save(license);
    }
    
    /**
     * Rechaza una solicitud
     */
    public DriverLicenseEntity rejectLicense(String documentId, String reason) {
        Optional<DriverLicenseEntity> optionalLicense = repository.findByDocumentId(documentId);
        
        if (optionalLicense.isEmpty()) {
            throw new IllegalArgumentException("Solicitud no encontrada: " + documentId);
        }
        
        DriverLicenseEntity license = optionalLicense.get();
        license.setStatus("REJECTED");
        license.setRejectionReason(reason);
        return repository.save(license);
    }
    
    /**
     * Suspende una licencia aprobada
     */
    public DriverLicenseEntity suspendLicense(String documentId) {
        Optional<DriverLicenseEntity> optionalLicense = repository.findByDocumentId(documentId);
        
        if (optionalLicense.isEmpty()) {
            throw new IllegalArgumentException("Solicitud no encontrada: " + documentId);
        }
        
        DriverLicenseEntity license = optionalLicense.get();
        license.setStatus("SUSPENDED");
        return repository.save(license);
    }
    
    /**
     * Obtiene todas las solicitudes pendientes
     */
    public List<DriverLicenseEntity> getPendingRequests() {
        return repository.findByStatus("PENDING");
    }
    
    /**
     * Obtiene todas las solicitudes aprobadas
     */
    public List<DriverLicenseEntity> getApprovedLicenses() {
        return repository.findByStatus("APPROVED");
    }
}
