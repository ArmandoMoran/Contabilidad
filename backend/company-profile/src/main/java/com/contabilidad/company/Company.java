package com.contabilidad.company;

import com.contabilidad.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @Column(name = "rfc", nullable = false, unique = true, length = 13)
    private String rfc;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "taxpayer_type", nullable = false, length = 20)
    private String taxpayerType;

    @Column(name = "fiscal_regime_code", nullable = false, length = 3)
    private String fiscalRegimeCode;

    @Column(name = "tax_zone_profile", nullable = false, length = 20)
    private String taxZoneProfile = "STANDARD";

    @Column(name = "postal_code", length = 5)
    private String postalCode;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getTaxpayerType() { return taxpayerType; }
    public void setTaxpayerType(String taxpayerType) { this.taxpayerType = taxpayerType; }
    public String getFiscalRegimeCode() { return fiscalRegimeCode; }
    public void setFiscalRegimeCode(String fiscalRegimeCode) { this.fiscalRegimeCode = fiscalRegimeCode; }
    public String getTaxZoneProfile() { return taxZoneProfile; }
    public void setTaxZoneProfile(String taxZoneProfile) { this.taxZoneProfile = taxZoneProfile; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
