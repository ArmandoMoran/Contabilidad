package com.contabilidad.invoicing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tax_lines")
public class TaxLine {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "source_type", nullable = false, length = 20)
    private String sourceType;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "source_line_id")
    private UUID sourceLineId;

    @Column(name = "tax_code", nullable = false, length = 3)
    private String taxCode;

    @Column(name = "factor_type", nullable = false, length = 10)
    private String factorType;

    @Column(name = "rate", nullable = false, precision = 12, scale = 6)
    private BigDecimal rate;

    @Column(name = "base_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "tax_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "is_transfer", nullable = false)
    private boolean isTransfer;

    @Column(name = "is_withholding", nullable = false)
    private boolean isWithholding;

    @Column(name = "period_key", nullable = false, length = 7)
    private String periodKey;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "payment_at")
    private Instant paymentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TaxLine() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    // -- Getters & Setters --

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public UUID getSourceId() { return sourceId; }
    public void setSourceId(UUID sourceId) { this.sourceId = sourceId; }

    public UUID getSourceLineId() { return sourceLineId; }
    public void setSourceLineId(UUID sourceLineId) { this.sourceLineId = sourceLineId; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getFactorType() { return factorType; }
    public void setFactorType(String factorType) { this.factorType = factorType; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public boolean isTransfer() { return isTransfer; }
    public void setTransfer(boolean transfer) { isTransfer = transfer; }

    public boolean isWithholding() { return isWithholding; }
    public void setWithholding(boolean withholding) { isWithholding = withholding; }

    public String getPeriodKey() { return periodKey; }
    public void setPeriodKey(String periodKey) { this.periodKey = periodKey; }

    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }

    public Instant getPaymentAt() { return paymentAt; }
    public void setPaymentAt(Instant paymentAt) { this.paymentAt = paymentAt; }

    public Instant getCreatedAt() { return createdAt; }
}
