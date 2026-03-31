package com.contabilidad.tax;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "obligation_rules")
public class ObligationRule {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "obligation_code", nullable = false, length = 40)
    private String obligationCode;

    @Column(name = "obligation_name", nullable = false, length = 200)
    private String obligationName;

    @Column(name = "frequency", nullable = false, length = 20)
    private String frequency;

    @Column(name = "tax_type", length = 10)
    private String taxType;

    @Column(name = "applies_to_taxpayer_type", length = 20)
    private String appliesToTaxpayerType;

    @Column(name = "applies_to_regime_code", length = 3)
    private String appliesToRegimeCode;

    @Column(name = "applies_to_region", length = 20)
    private String appliesToRegion;

    @Column(name = "formula_key", length = 60)
    private String formulaKey;

    @Column(name = "due_day")
    private Integer dueDay;

    @Column(name = "grace_days", nullable = false)
    private int graceDays = 0;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getObligationCode() { return obligationCode; }
    public void setObligationCode(String obligationCode) { this.obligationCode = obligationCode; }
    public String getObligationName() { return obligationName; }
    public void setObligationName(String obligationName) { this.obligationName = obligationName; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getTaxType() { return taxType; }
    public void setTaxType(String taxType) { this.taxType = taxType; }
    public String getAppliesToTaxpayerType() { return appliesToTaxpayerType; }
    public void setAppliesToTaxpayerType(String appliesToTaxpayerType) { this.appliesToTaxpayerType = appliesToTaxpayerType; }
    public String getAppliesToRegimeCode() { return appliesToRegimeCode; }
    public void setAppliesToRegimeCode(String appliesToRegimeCode) { this.appliesToRegimeCode = appliesToRegimeCode; }
    public String getAppliesToRegion() { return appliesToRegion; }
    public void setAppliesToRegion(String appliesToRegion) { this.appliesToRegion = appliesToRegion; }
    public String getFormulaKey() { return formulaKey; }
    public void setFormulaKey(String formulaKey) { this.formulaKey = formulaKey; }
    public Integer getDueDay() { return dueDay; }
    public void setDueDay(Integer dueDay) { this.dueDay = dueDay; }
    public int getGraceDays() { return graceDays; }
    public void setGraceDays(int graceDays) { this.graceDays = graceDays; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
