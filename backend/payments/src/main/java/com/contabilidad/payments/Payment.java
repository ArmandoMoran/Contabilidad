package com.contabilidad.payments;

import com.contabilidad.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "payment_direction", nullable = false, length = 10)
    private String paymentDirection;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "REGISTERED";

    @Column(name = "payment_form_code", nullable = false, length = 2)
    private String paymentFormCode;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "MXN";

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 6)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    @Column(name = "operation_number", length = 100)
    private String operationNumber;

    @Column(name = "payer_rfc", length = 13)
    private String payerRfc;

    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "payer_bank_rfc", length = 13)
    private String payerBankRfc;

    @Column(name = "payer_account", length = 50)
    private String payerAccount;

    @Column(name = "payee_rfc", length = 13)
    private String payeeRfc;

    @Column(name = "payee_name")
    private String payeeName;

    @Column(name = "payee_bank_rfc", length = 13)
    private String payeeBankRfc;

    @Column(name = "payee_account", length = 50)
    private String payeeAccount;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "rep_invoice_id")
    private UUID repInvoiceId;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    public Payment() {}

    // -- Getters & Setters --

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public String getPaymentDirection() { return paymentDirection; }
    public void setPaymentDirection(String paymentDirection) { this.paymentDirection = paymentDirection; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentFormCode() { return paymentFormCode; }
    public void setPaymentFormCode(String paymentFormCode) { this.paymentFormCode = paymentFormCode; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }

    public String getOperationNumber() { return operationNumber; }
    public void setOperationNumber(String operationNumber) { this.operationNumber = operationNumber; }

    public String getPayerRfc() { return payerRfc; }
    public void setPayerRfc(String payerRfc) { this.payerRfc = payerRfc; }

    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }

    public String getPayerBankRfc() { return payerBankRfc; }
    public void setPayerBankRfc(String payerBankRfc) { this.payerBankRfc = payerBankRfc; }

    public String getPayerAccount() { return payerAccount; }
    public void setPayerAccount(String payerAccount) { this.payerAccount = payerAccount; }

    public String getPayeeRfc() { return payeeRfc; }
    public void setPayeeRfc(String payeeRfc) { this.payeeRfc = payeeRfc; }

    public String getPayeeName() { return payeeName; }
    public void setPayeeName(String payeeName) { this.payeeName = payeeName; }

    public String getPayeeBankRfc() { return payeeBankRfc; }
    public void setPayeeBankRfc(String payeeBankRfc) { this.payeeBankRfc = payeeBankRfc; }

    public String getPayeeAccount() { return payeeAccount; }
    public void setPayeeAccount(String payeeAccount) { this.payeeAccount = payeeAccount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public UUID getRepInvoiceId() { return repInvoiceId; }
    public void setRepInvoiceId(UUID repInvoiceId) { this.repInvoiceId = repInvoiceId; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
