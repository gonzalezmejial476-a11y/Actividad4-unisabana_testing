package edu.unisabana.proyecto.domain;

/**
 * DriverLicense: Clase de dominio para validar elegibilidad de licencia de conducción.
 * Implementa reglas de negocio colombianas para obtener licencia de conducir.
 * 
 * REGLAS COLOMBIA:
 * - Edad mínima: 16 años (conducción normal)
 * - Edad mínima servicio público: 23 años (buses, taxis, uber)
 * - Edad máxima renovación: 80 años (con restricciones)
 * - Discapacidad visual severa: NO puede conducir
 * - Antecedentes penales: NO puede obtener
 */
public class DriverLicense {
    
    // Constantes - Edades según normativa colombiana
    private static final int MIN_AGE_REGULAR = 16;           // Conducción normal de carros
    private static final int MIN_AGE_PUBLIC_SERVICE = 23;    // Servicio público (taxi, uber, buses)
    private static final int MAX_AGE = 88;                   // Edad máxima legal
    
    // Identificación
    private String documentId;
    private String fullName;
    private int age;
    
    // Condiciones médicas
    private boolean hasSevereEyeDisability;  // Discapacidad visual severa (impide conducir)
    private boolean hasHearingDisability;    // Discapacidad auditiva (permite con adaptaciones)
    
    // Antecedentes
    private int seriousCriminalRecords;  // Número de antecedentes penales graves
    
    // Estado de la licencia
    private String licenseType;  // REGULAR, PUBLIC_SERVICE
    private String status;       // PENDING, APPROVED, REJECTED, SUSPENDED, EXPIRED
    private String rejectionReason; // Motivo del rechazo cuando aplica
    
    /**
     * Constructor para solicitud de licencia de conducción de carros.
     */
    public DriverLicense(String documentId, String fullName, int age,
                        boolean hasSevereEyeDisability, boolean hasHearingDisability,
                        int seriousCriminalRecords, String licenseType) {
        
        validateAge(age);
        validateDocument(documentId);
        validateCriminalRecords(seriousCriminalRecords);
        validateLicenseType(licenseType);
        
        this.documentId = documentId;
        this.fullName = fullName;
        this.age = age;
        this.hasSevereEyeDisability = hasSevereEyeDisability;
        this.hasHearingDisability = hasHearingDisability;
        this.seriousCriminalRecords = seriousCriminalRecords;
        this.licenseType = licenseType;
        this.status = "PENDING";
    }
    
    /**
     * REGLA PRINCIPAL: ¿Es elegible para obtener licencia de conducción?
     * 
     * Requisitos:
     * 1. Edad válida según tipo de licencia
     * 2. NO tener discapacidad visual severa
     * 3. NO tener antecedentes penales graves
     */
    public boolean isEligibleForLicense() {
        // Validar edad según tipo de licencia
        if (!isAgeValidForLicenseType()) {
            return false;
        }
        
        // NO puede tener discapacidad ocular severa
        if (hasSevereEyeDisability) {
            return false;
        }
        
        // NO puede tener antecedentes penales graves
        if (seriousCriminalRecords > 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Verifica si la edad es válida para el tipo de licencia.
     */
    private boolean isAgeValidForLicenseType() {
        if ("REGULAR".equals(licenseType)) {
            return age >= MIN_AGE_REGULAR && age <= MAX_AGE;
        } else if ("PUBLIC_SERVICE".equals(licenseType)) {
            return age >= MIN_AGE_PUBLIC_SERVICE && age <= MAX_AGE;
        }
        return false;
    }
    
    /**
     * ¿Es mayor de edad para conducción regular (16 años)?
     */
    public boolean isAdultForRegularLicense() {
        return age >= MIN_AGE_REGULAR;
    }
    
    /**
     * ¿Es mayor de edad para servicio público (23 años)?
     */
    public boolean isAdultForPublicService() {
        return age >= MIN_AGE_PUBLIC_SERVICE;
    }
    
    /**
     * ¿Está dentro del rango de edad máxima?
     */
    public boolean isWithinMaximumAge() {
        return age <= MAX_AGE;
    }
    
    /**
     * Obtiene la categoría de edad para conducción.
     */
    public String getAgeCategory() {
        if (age < MIN_AGE_REGULAR) {
            return "TOO_YOUNG";
        } else if (age < 18) {
            return "ADOLESCENT";
        } else if (age < MIN_AGE_PUBLIC_SERVICE) {
            return "YOUNG_ADULT";
        } else if (age < 65) {
            return "ADULT";
        } else if (age <= MAX_AGE) {
            return "SENIOR";
        } else {
            return "TOO_OLD";
        }
    }
    
    /**
     * Calcula años hasta poder acceder a servicio público.
     */
    public int yearsUntilPublicService() {
        if (age >= MIN_AGE_PUBLIC_SERVICE) {
            return 0;
        }
        return MIN_AGE_PUBLIC_SERVICE - age;
    }
    
    /**
     * ¿Puede conducir con discapacidad visual severa?
     */
    public boolean canDriveWithEyeDisability() {
        return !hasSevereEyeDisability;
    }
    
    /**
     * ¿Puede conducir con discapacidad auditiva?
     */
    public boolean canDriveWithHearingDisability() {
        return true; // Discapacidad auditiva permite conducción (con adaptaciones)
    }
    
    /**
     * ¿Tiene antecedentes penales limpios?
     */
    public boolean hasCleanCriminalRecord() {
        return seriousCriminalRecords == 0;
    }
    
    /**
     * Obtiene el número de antecedentes penales graves.
     */
    public int getCriminalRecordCount() {
        return seriousCriminalRecords;
    }
    
    /**
     * APRUEBA la licencia si cumple todos los requisitos.
     */
    public void approveLicense() {
        if (!isEligibleForLicense()) {
            throw new IllegalStateException("Solicitante no cumple los requisitos para obtener licencia");
        }
        this.status = "APPROVED";
    }
    
    /**
     * RECHAZA la licencia.
     */
    public void rejectLicense(String reason) {
        this.status = "REJECTED";
        this.rejectionReason = reason;
    }
    
    /**
     * SUSPENDE la licencia.
     */
    public void suspendLicense() {
        if ("APPROVED".equals(status)) {
            this.status = "SUSPENDED";
        }
    }
    
    /**
     * REACTIVA una licencia suspendida.
     */
    public void reactivateLicense() {
        if ("SUSPENDED".equals(status)) {
            this.status = "APPROVED";
        }
    }
    
    /**
     * Obtiene el motivo de rechazo.
     */
    public String getRejectionReason() {
        // Si se almacenó una razón explícita al rechazar, devolverla
        if ("REJECTED".equals(this.status) && this.rejectionReason != null && !this.rejectionReason.trim().isEmpty()) {
            return this.rejectionReason;
        }
        if (!isWithinMaximumAge()) {
            return "Excede la edad máxima permitida (80 años)";
        }
        if ("REGULAR".equals(licenseType) && !isAdultForRegularLicense()) {
            return "No cumple edad mínima para licencia regular (16 años)";
        }
        if ("PUBLIC_SERVICE".equals(licenseType) && !isAdultForPublicService()) {
            return "No cumple edad mínima para servicio público (23 años)";
        }
        if (hasSevereEyeDisability) {
            return "Tiene discapacidad visual severa";
        }
        if (seriousCriminalRecords > 0) {
            return "Tiene antecedentes penales graves";
        }
        return "Cumple todos los requisitos";
    }
    
    // Validaciones privadas
    
    private void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        if (age > 150) {
            throw new IllegalArgumentException("La edad debe ser realista");
        }
    }
    
    private void validateDocument(String documentId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de documento no puede estar vacío");
        }
    }
    
    private void validateCriminalRecords(int records) {
        if (records < 0) {
            throw new IllegalArgumentException("El número de antecedentes no puede ser negativo");
        }
    }
    
    private void validateLicenseType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de licencia no puede estar vacío");
        }
        if (!type.matches("REGULAR|PUBLIC_SERVICE")) {
            throw new IllegalArgumentException("Tipo de licencia inválido: " + type);
        }
    }
    
    // Getters
    
    public String getDocumentId() {
        return documentId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public int getAge() {
        return age;
    }
    
    public String getLicenseType() {
        return licenseType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public boolean hasSevereEyeDisability() {
        return hasSevereEyeDisability;
    }
    
    public boolean hasHearingDisability() {
        return hasHearingDisability;
    }
}
