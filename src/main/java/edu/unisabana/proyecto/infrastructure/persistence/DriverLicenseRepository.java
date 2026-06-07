package edu.unisabana.proyecto.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para DriverLicenseEntity con H2.
 * Permite persistencia en memoria durante tests.
 */
@Repository
public interface DriverLicenseRepository extends JpaRepository<DriverLicenseEntity, Long> {
    
    /**
     * Busca una solicitud por número de documento
     */
    Optional<DriverLicenseEntity> findByDocumentId(String documentId);
    
    /**
     * Busca todas las solicitudes por estado
     */
    java.util.List<DriverLicenseEntity> findByStatus(String status);
}
