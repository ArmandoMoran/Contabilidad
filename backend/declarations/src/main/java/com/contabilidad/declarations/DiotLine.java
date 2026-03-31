package com.contabilidad.declarations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "diot_lines")
public class DiotLine {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "declaration_run_id", nullable = false)
    private UUID declarationRunId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "supplier_rfc", length = 13)
    private String supplierRfc;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "nationality", nullable = false, length = 20)
    private String nationality = "NATIONAL";

    @Column(name = "third_party_type", nullable = false, length = 2)
    private String thirdPartyType;

    @Column(name = "iva_16_paid", nullable = false, precision = 18, scale = 2)
    private BigDecimal iva16Paid = BigDecimal.ZERO;

    @Column(name = "iva_16_accrued", nullable = false, precision = 18, scale = 2)
    private BigDecimal iva16Accrued = BigDecimal.ZERO;

    @Column(name = "iva_8_paid", nullable = false, precision = 18, scale = 2)
    private BigDecimal iva8Paid = BigDecimal.ZERO;

    @Column(name = "iva_8_accrued", nullable = false, precision = 18, scale = 2)
    private BigDecimal iva8Accrued = BigDecimal.ZERO;

    @Column(name = "iva_0", nullable = false, precision = 18, scale = 2)
    private BigDecimal iva0 = BigDecimal.ZERO;

    @Column(name = "iva_exempt", nullable = false, precision = 18, scale = 2)
    private BigDecimal ivaExempt = BigDecimal.ZERO;

    @Column(name = "iva_withheld", nullable = false, precision = 18, scale = 2)
    private BigDecimal ivaWithheld = BigDecimal.ZERO;

    @Column(name = "isr_withheld", nullable = false, precision = 18, scale = 2)
    private BigDecimal isrWithheld = BigDecimal.ZERO;

    @Column(name = "period_key", nullable = false, length = 7)
    private String periodKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DiotLine() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDeclarationRunId() { return declarationRunId; }
    public void setDeclarationRunId(UUID declarationRunId) { this.declarationRunId = declarationRunId; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getSupplierId() { return supplierId; }
    public void setSupplierId(UUID supplierId) { this.supplierId = supplierId; }
    public String getSupplierRfc() { return supplierRfc; }
    public void setSupplierRfc(String supplierRfc) { this.supplierRfc = supplierRfc; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getThirdPartyType() { return thirdPartyType; }
    public void setThirdPartyType(String thirdPartyType) { this.thirdPartyType = thirdPartyType; }
    public BigDecimal getIva16Paid() { return iva16Paid; }
    public void setIva16Paid(BigDecimal iva16Paid) { this.iva16Paid = iva16Paid; }
    public BigDecimal getIva16Accrued() { return iva16Accrued; }
    public void setIva16Accrued(BigDecimal iva16Accrued) { this.iva16Accrued = iva16Accrued; }
    public BigDecimal getIva8Paid() { return iva8Paid; }
    public void setIva8Paid(BigDecimal iva8Paid) { this.iva8Paid = iva8Paid; }
    public BigDecimal getIva8Accrued() { return iva8Accrued; }
    public void setIva8Accrued(BigDecimal iva8Accrued) { this.iva8Accrued = iva8Accrued; }
    public BigDecimal getIva0() { return iva0; }
    public void setIva0(BigDecimal iva0) { this.iva0 = iva0; }
    public BigDecimal getIvaExempt() { return ivaExempt; }
    public void setIvaExempt(BigDecimal ivaExempt) { this.ivaExempt = ivaExempt; }
    public BigDecimal getIvaWithheld() { return ivaWithheld; }
    public void setIvaWithheld(BigDecimal ivaWithheld) { this.ivaWithheld = ivaWithheld; }
    public BigDecimal getIsrWithheld() { return isrWithheld; }
    public void setIsrWithheld(BigDecimal isrWithheld) { this.isrWithheld = isrWithheld; }
    public String getPeriodKey() { return periodKey; }
    public void setPeriodKey(String periodKey) { this.periodKey = periodKey; }
    public Instant getCreatedAt() { return createdAt; }
}
