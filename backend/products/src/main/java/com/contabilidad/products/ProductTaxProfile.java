package com.contabilidad.products;

import com.contabilidad.shared.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "product_tax_profiles")
public class ProductTaxProfile extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "tax_code", nullable = false)
    private String taxCode;

    @Column(name = "factor_type", nullable = false)
    private String factorType;

    @Column(precision = 10, scale = 6)
    private BigDecimal rate;

    @Column(name = "is_transfer")
    private boolean isTransfer;

    @Column(name = "is_withholding")
    private boolean isWithholding;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    private boolean active = true;

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
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
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
