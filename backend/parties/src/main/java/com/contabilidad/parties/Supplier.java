package com.contabilidad.parties;

import com.contabilidad.shared.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
public class Supplier extends SoftDeletableEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "rfc", nullable = false, length = 13)
    private String rfc;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "trade_name")
    private String tradeName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "website")
    private String website;

    @Column(name = "fiscal_regime_code", nullable = false, length = 3)
    private String fiscalRegimeCode;

    @Column(name = "default_forma_pago_code", length = 2)
    private String defaultFormaPagoCode;

    @Column(name = "nationality", nullable = false, length = 20)
    private String nationality = "NATIONAL";

    @Column(name = "diot_operation_type", length = 10)
    private String diotOperationType;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public Supplier() {}

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }

    public String getTradeName() { return tradeName; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getFiscalRegimeCode() { return fiscalRegimeCode; }
    public void setFiscalRegimeCode(String fiscalRegimeCode) { this.fiscalRegimeCode = fiscalRegimeCode; }

    public String getDefaultFormaPagoCode() { return defaultFormaPagoCode; }
    public void setDefaultFormaPagoCode(String defaultFormaPagoCode) { this.defaultFormaPagoCode = defaultFormaPagoCode; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getDiotOperationType() { return diotOperationType; }
    public void setDiotOperationType(String diotOperationType) { this.diotOperationType = diotOperationType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
