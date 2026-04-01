package com.contabilidad.invoicing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invoice_lines")
public class InvoiceLine {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "sat_product_code", nullable = false, length = 8)
    private String satProductCode;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "sat_unit_code", nullable = false, length = 10)
    private String satUnitCode;

    @Column(name = "unit_name", length = 100)
    private String unitName;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 6)
    private BigDecimal unitPrice;

    @Column(name = "discount", nullable = false, precision = 18, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "objeto_imp_code", nullable = false, length = 2)
    private String objetoImpCode = "02";

    @Column(name = "cuenta_predial", length = 50)
    private String cuentaPredial;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tax_profile_snapshot", columnDefinition = "jsonb", nullable = false)
    private String taxProfileSnapshot = "[]";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public InvoiceLine() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    // -- Getters & Setters --

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getInvoiceId() { return invoiceId; }
    public void setInvoiceId(UUID invoiceId) { this.invoiceId = invoiceId; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getSatProductCode() { return satProductCode; }
    public void setSatProductCode(String satProductCode) { this.satProductCode = satProductCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSatUnitCode() { return satUnitCode; }
    public void setSatUnitCode(String satUnitCode) { this.satUnitCode = satUnitCode; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getObjetoImpCode() { return objetoImpCode; }
    public void setObjetoImpCode(String objetoImpCode) { this.objetoImpCode = objetoImpCode; }

    public String getCuentaPredial() { return cuentaPredial; }
    public void setCuentaPredial(String cuentaPredial) { this.cuentaPredial = cuentaPredial; }

    public String getTaxProfileSnapshot() { return taxProfileSnapshot; }
    public void setTaxProfileSnapshot(String taxProfileSnapshot) { this.taxProfileSnapshot = taxProfileSnapshot; }

    public Instant getCreatedAt() { return createdAt; }
}
