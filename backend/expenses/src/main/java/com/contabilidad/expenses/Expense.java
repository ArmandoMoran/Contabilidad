package com.contabilidad.expenses;

import com.contabilidad.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "expense_type", nullable = false, length = 20)
    private String expenseType = "CFDI_RECEIVED";

    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING";

    @Column(name = "cfdi_uuid")
    private UUID cfdiUuid;

    @Column(name = "cfdi_version", length = 10)
    private String cfdiVersion;

    @Column(name = "issuer_rfc", length = 13)
    private String issuerRfc;

    @Column(name = "issuer_name")
    private String issuerName;

    @Column(name = "receiver_rfc", length = 13)
    private String receiverRfc;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "invoice_type", length = 1)
    private String invoiceType;

    @Column(name = "series", length = 25)
    private String series;

    @Column(name = "folio", length = 40)
    private String folio;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "payment_method_code", length = 3)
    private String paymentMethodCode;

    @Column(name = "payment_form_code", length = 2)
    private String paymentFormCode;

    @Column(name = "uso_cfdi_code", length = 4)
    private String usoCfdiCode;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "MXN";

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 6)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Column(name = "subtotal", nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount", nullable = false, precision = 18, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "transferred_tax_total", nullable = false, precision = 18, scale = 2)
    private BigDecimal transferredTaxTotal = BigDecimal.ZERO;

    @Column(name = "withheld_tax_total", nullable = false, precision = 18, scale = 2)
    private BigDecimal withheldTaxTotal = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 18, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "category", length = 60)
    private String category;

    @Column(name = "deductible", nullable = false)
    private boolean deductible = true;

    @Column(name = "accounting_account", length = 30)
    private String accountingAccount;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "sat_validation_status", length = 30)
    private String satValidationStatus;

    @Column(name = "sat_validated_at")
    private Instant satValidatedAt;

    @Column(name = "xml_object_key", length = 500)
    private String xmlObjectKey;

    @Column(name = "pdf_object_key", length = 500)
    private String pdfObjectKey;

    @Column(name = "fiscal_snapshot", columnDefinition = "jsonb", nullable = false)
    private String fiscalSnapshot = "{}";

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    public Expense() {}

    // -- Getters & Setters --

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public UUID getSupplierId() { return supplierId; }
    public void setSupplierId(UUID supplierId) { this.supplierId = supplierId; }

    public String getExpenseType() { return expenseType; }
    public void setExpenseType(String expenseType) { this.expenseType = expenseType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getCfdiUuid() { return cfdiUuid; }
    public void setCfdiUuid(UUID cfdiUuid) { this.cfdiUuid = cfdiUuid; }

    public String getCfdiVersion() { return cfdiVersion; }
    public void setCfdiVersion(String cfdiVersion) { this.cfdiVersion = cfdiVersion; }

    public String getIssuerRfc() { return issuerRfc; }
    public void setIssuerRfc(String issuerRfc) { this.issuerRfc = issuerRfc; }

    public String getIssuerName() { return issuerName; }
    public void setIssuerName(String issuerName) { this.issuerName = issuerName; }

    public String getReceiverRfc() { return receiverRfc; }
    public void setReceiverRfc(String receiverRfc) { this.receiverRfc = receiverRfc; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }

    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }

    public String getPaymentMethodCode() { return paymentMethodCode; }
    public void setPaymentMethodCode(String paymentMethodCode) { this.paymentMethodCode = paymentMethodCode; }

    public String getPaymentFormCode() { return paymentFormCode; }
    public void setPaymentFormCode(String paymentFormCode) { this.paymentFormCode = paymentFormCode; }

    public String getUsoCfdiCode() { return usoCfdiCode; }
    public void setUsoCfdiCode(String usoCfdiCode) { this.usoCfdiCode = usoCfdiCode; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTransferredTaxTotal() { return transferredTaxTotal; }
    public void setTransferredTaxTotal(BigDecimal transferredTaxTotal) { this.transferredTaxTotal = transferredTaxTotal; }

    public BigDecimal getWithheldTaxTotal() { return withheldTaxTotal; }
    public void setWithheldTaxTotal(BigDecimal withheldTaxTotal) { this.withheldTaxTotal = withheldTaxTotal; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isDeductible() { return deductible; }
    public void setDeductible(boolean deductible) { this.deductible = deductible; }

    public String getAccountingAccount() { return accountingAccount; }
    public void setAccountingAccount(String accountingAccount) { this.accountingAccount = accountingAccount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getSatValidationStatus() { return satValidationStatus; }
    public void setSatValidationStatus(String satValidationStatus) { this.satValidationStatus = satValidationStatus; }

    public Instant getSatValidatedAt() { return satValidatedAt; }
    public void setSatValidatedAt(Instant satValidatedAt) { this.satValidatedAt = satValidatedAt; }

    public String getXmlObjectKey() { return xmlObjectKey; }
    public void setXmlObjectKey(String xmlObjectKey) { this.xmlObjectKey = xmlObjectKey; }

    public String getPdfObjectKey() { return pdfObjectKey; }
    public void setPdfObjectKey(String pdfObjectKey) { this.pdfObjectKey = pdfObjectKey; }

    public String getFiscalSnapshot() { return fiscalSnapshot; }
    public void setFiscalSnapshot(String fiscalSnapshot) { this.fiscalSnapshot = fiscalSnapshot; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
