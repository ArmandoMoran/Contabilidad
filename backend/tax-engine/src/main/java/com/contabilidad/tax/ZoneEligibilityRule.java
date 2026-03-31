package com.contabilidad.tax;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "zone_eligibility_rules")
public class ZoneEligibilityRule {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "zone_code", nullable = false, length = 20)
    private String zoneCode;

    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "eligible_states", columnDefinition = "jsonb")
    private String eligibleStates;

    @Column(name = "eligible_municipalities", columnDefinition = "jsonb")
    private String eligibleMunicipalities;

    @Column(name = "eligible_postal_codes", columnDefinition = "jsonb")
    private String eligiblePostalCodes;

    @Column(name = "stimulus_type", nullable = false, length = 40)
    private String stimulusType;

    @Column(name = "iva_rate_override", precision = 12, scale = 6)
    private BigDecimal ivaRateOverride;

    @Column(name = "isr_rate_override", precision = 12, scale = 6)
    private BigDecimal isrRateOverride;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getZoneCode() { return zoneCode; }
    public void setZoneCode(String zoneCode) { this.zoneCode = zoneCode; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEligibleStates() { return eligibleStates; }
    public void setEligibleStates(String eligibleStates) { this.eligibleStates = eligibleStates; }
    public String getEligibleMunicipalities() { return eligibleMunicipalities; }
    public void setEligibleMunicipalities(String eligibleMunicipalities) { this.eligibleMunicipalities = eligibleMunicipalities; }
    public String getEligiblePostalCodes() { return eligiblePostalCodes; }
    public void setEligiblePostalCodes(String eligiblePostalCodes) { this.eligiblePostalCodes = eligiblePostalCodes; }
    public String getStimulusType() { return stimulusType; }
    public void setStimulusType(String stimulusType) { this.stimulusType = stimulusType; }
    public BigDecimal getIvaRateOverride() { return ivaRateOverride; }
    public void setIvaRateOverride(BigDecimal ivaRateOverride) { this.ivaRateOverride = ivaRateOverride; }
    public BigDecimal getIsrRateOverride() { return isrRateOverride; }
    public void setIsrRateOverride(BigDecimal isrRateOverride) { this.isrRateOverride = isrRateOverride; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
