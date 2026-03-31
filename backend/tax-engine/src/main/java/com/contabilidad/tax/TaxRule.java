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
@Table(name = "tax_rules")
public class TaxRule {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "tax_code", nullable = false, length = 3)
    private String taxCode;

    @Column(name = "factor_type", nullable = false, length = 10)
    private String factorType = "Tasa";

    @Column(name = "rate", nullable = false, precision = 12, scale = 6)
    private BigDecimal rate;

    @Column(name = "is_transfer", nullable = false)
    private boolean isTransfer = true;

    @Column(name = "is_withholding", nullable = false)
    private boolean isWithholding = false;

    @Column(name = "applies_to_taxpayer_type", length = 20)
    private String appliesToTaxpayerType;

    @Column(name = "applies_to_regime_code", length = 3)
    private String appliesToRegimeCode;

    @Column(name = "applies_to_operation", length = 60)
    private String appliesToOperation;

    @Column(name = "applies_to_region", length = 20)
    private String appliesToRegion;

    @Column(name = "requires_zone_eligibility", nullable = false)
    private boolean requiresZoneEligibility = false;

    @Column(name = "inactive_by_default", nullable = false)
    private boolean inactiveByDefault = false;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }
    public String getFactorType() { return factorType; }
    public void setFactorType(String factorType) { this.factorType = factorType; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public boolean isTransfer() { return isTransfer; }
    public void setTransfer(boolean transfer) { isTransfer = transfer; }
    public boolean isWithholding() { return isWithholding; }
    public void setWithholding(boolean withholding) { isWithholding = withholding; }
    public String getAppliesToTaxpayerType() { return appliesToTaxpayerType; }
    public void setAppliesToTaxpayerType(String appliesToTaxpayerType) { this.appliesToTaxpayerType = appliesToTaxpayerType; }
    public String getAppliesToRegimeCode() { return appliesToRegimeCode; }
    public void setAppliesToRegimeCode(String appliesToRegimeCode) { this.appliesToRegimeCode = appliesToRegimeCode; }
    public String getAppliesToOperation() { return appliesToOperation; }
    public void setAppliesToOperation(String appliesToOperation) { this.appliesToOperation = appliesToOperation; }
    public String getAppliesToRegion() { return appliesToRegion; }
    public void setAppliesToRegion(String appliesToRegion) { this.appliesToRegion = appliesToRegion; }
    public boolean isRequiresZoneEligibility() { return requiresZoneEligibility; }
    public void setRequiresZoneEligibility(boolean requiresZoneEligibility) { this.requiresZoneEligibility = requiresZoneEligibility; }
    public boolean isInactiveByDefault() { return inactiveByDefault; }
    public void setInactiveByDefault(boolean inactiveByDefault) { this.inactiveByDefault = inactiveByDefault; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
