package com.contabilidad.catalogs;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sat_tasa_o_cuota")
public class SatTasaOCuota {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "impuesto_code")
    private String impuestoCode;

    @Column(name = "tipo_factor_code")
    private String tipoFactorCode;

    @Column(precision = 10, scale = 6)
    private BigDecimal rate;

    private boolean transfer;

    private boolean withholding;

    @Column(name = "min_value", precision = 10, scale = 6)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 10, scale = 6)
    private BigDecimal maxValue;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    private boolean active;

    @Column(name = "sat_release_tag")
    private String satReleaseTag;

    @Column(name = "loaded_at")
    private Instant loadedAt;

    public SatTasaOCuota() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getImpuestoCode() { return impuestoCode; }
    public void setImpuestoCode(String impuestoCode) { this.impuestoCode = impuestoCode; }
    public String getTipoFactorCode() { return tipoFactorCode; }
    public void setTipoFactorCode(String tipoFactorCode) { this.tipoFactorCode = tipoFactorCode; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public boolean isTransfer() { return transfer; }
    public void setTransfer(boolean transfer) { this.transfer = transfer; }
    public boolean isWithholding() { return withholding; }
    public void setWithholding(boolean withholding) { this.withholding = withholding; }
    public BigDecimal getMinValue() { return minValue; }
    public void setMinValue(BigDecimal minValue) { this.minValue = minValue; }
    public BigDecimal getMaxValue() { return maxValue; }
    public void setMaxValue(BigDecimal maxValue) { this.maxValue = maxValue; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getSatReleaseTag() { return satReleaseTag; }
    public void setSatReleaseTag(String satReleaseTag) { this.satReleaseTag = satReleaseTag; }
    public Instant getLoadedAt() { return loadedAt; }
    public void setLoadedAt(Instant loadedAt) { this.loadedAt = loadedAt; }
}
