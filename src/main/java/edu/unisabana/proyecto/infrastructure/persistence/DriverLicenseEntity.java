package edu.unisabana.proyecto.infrastructure.persistence;

import jakarta.persistence.*;

/**
 * Entidad JPA para persistencia de solicitudes de licencia de conducción en H2.
 */
@Entity
@Table(name = "DRIVER_LICENSES")
public class DriverLicenseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String documentId;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false)
    private int age;
    
    @Column(nullable = false)
    private boolean hasSevereEyeDisability;
    
    @Column(nullable = false)
    private boolean hasHearingDisability;
    
    @Column(nullable = false)
    private int seriousCriminalRecords;
    
    @Column(nullable = false)
    private String licenseType;
    
    @Column(nullable = false)
    private String status;
    
    @Column
    private String rejectionReason;
    
    // Constructores
    public DriverLicenseEntity() {
    }
    
    public DriverLicenseEntity(String documentId, String fullName, int age,
                              boolean hasSevereEyeDisability, boolean hasHearingDisability,
                              int seriousCriminalRecords, String licenseType, String status) {
        this.documentId = documentId;
        this.fullName = fullName;
        this.age = age;
        this.hasSevereEyeDisability = hasSevereEyeDisability;
        this.hasHearingDisability = hasHearingDisability;
        this.seriousCriminalRecords = seriousCriminalRecords;
        this.licenseType = licenseType;
        this.status = status;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public boolean isHasSevereEyeDisability() {
        return hasSevereEyeDisability;
    }
    
    public void setHasSevereEyeDisability(boolean hasSevereEyeDisability) {
        this.hasSevereEyeDisability = hasSevereEyeDisability;
    }
    
    public boolean isHasHearingDisability() {
        return hasHearingDisability;
    }
    
    public void setHasHearingDisability(boolean hasHearingDisability) {
        this.hasHearingDisability = hasHearingDisability;
    }
    
    public int getSeriousCriminalRecords() {
        return seriousCriminalRecords;
    }
    
    public void setSeriousCriminalRecords(int seriousCriminalRecords) {
        this.seriousCriminalRecords = seriousCriminalRecords;
    }
    
    public String getLicenseType() {
        return licenseType;
    }
    
    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
