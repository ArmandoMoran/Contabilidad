package com.contabilidad.expenses;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "expense_lines")
public class ExpenseLine {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "expense_id", nullable = false)
    private UUID expenseId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "sat_product_code", length = 8)
    private String satProductCode;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "sat_unit_code", length = 10)
    private String satUnitCode;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 6)
    private BigDecimal unitPrice;

    @Column(name = "discount", nullable = false, precision = 18, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "objeto_imp_code", length = 2)
    private String objetoImpCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public ExpenseLine() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    // -- Getters & Setters --

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getExpenseId() { return expenseId; }
    public void setExpenseId(UUID expenseId) { this.expenseId = expenseId; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

    public String getSatProductCode() { return satProductCode; }
    public void setSatProductCode(String satProductCode) { this.satProductCode = satProductCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSatUnitCode() { return satUnitCode; }
    public void setSatUnitCode(String satUnitCode) { this.satUnitCode = satUnitCode; }

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

    public Instant getCreatedAt() { return createdAt; }
}
