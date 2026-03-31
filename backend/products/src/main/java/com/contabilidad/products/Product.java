package com.contabilidad.products;

import com.contabilidad.shared.SoftDeletableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product extends SoftDeletableEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "internal_code")
    private String internalCode;

    @Column(name = "internal_name", nullable = false)
    private String internalName;

    private String description;

    @Column(name = "sat_product_code", nullable = false)
    private String satProductCode;

    @Column(name = "sat_unit_code", nullable = false)
    private String satUnitCode;

    @Column(name = "unit_price", precision = 18, scale = 6)
    private BigDecimal unitPrice;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode = "MXN";

    @Column(name = "objeto_imp_code", nullable = false)
    private String objetoImpCode = "02";

    @Column(name = "cuenta_predial")
    private String cuentaPredial;

    private boolean active = true;

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public String getInternalCode() { return internalCode; }
    public void setInternalCode(String internalCode) { this.internalCode = internalCode; }
    public String getInternalName() { return internalName; }
    public void setInternalName(String internalName) { this.internalName = internalName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSatProductCode() { return satProductCode; }
    public void setSatProductCode(String satProductCode) { this.satProductCode = satProductCode; }
    public String getSatUnitCode() { return satUnitCode; }
    public void setSatUnitCode(String satUnitCode) { this.satUnitCode = satUnitCode; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public String getObjetoImpCode() { return objetoImpCode; }
    public void setObjetoImpCode(String objetoImpCode) { this.objetoImpCode = objetoImpCode; }
    public String getCuentaPredial() { return cuentaPredial; }
    public void setCuentaPredial(String cuentaPredial) { this.cuentaPredial = cuentaPredial; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
