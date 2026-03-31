package com.contabilidad.payments;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_applications")
public class PaymentApplication {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "document_type", nullable = false, length = 20)
    private String documentType;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "document_uuid")
    private UUID documentUuid;

    @Column(name = "document_series", length = 25)
    private String documentSeries;

    @Column(name = "document_folio", length = 40)
    private String documentFolio;

    @Column(name = "installment_number", nullable = false)
    private int installmentNumber = 1;

    @Column(name = "previous_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal previousBalance;

    @Column(name = "amount_paid", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "remaining_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal remainingBalance;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "MXN";

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 6)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Column(name = "payment_method_code", nullable = false, length = 3)
    private String paymentMethodCode = "PPD";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public PaymentApplication() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    // -- Getters & Setters --

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPaymentId() { return paymentId; }
    public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }

    public UUID getDocumentUuid() { return documentUuid; }
    public void setDocumentUuid(UUID documentUuid) { this.documentUuid = documentUuid; }

    public String getDocumentSeries() { return documentSeries; }
    public void setDocumentSeries(String documentSeries) { this.documentSeries = documentSeries; }

    public String getDocumentFolio() { return documentFolio; }
    public void setDocumentFolio(String documentFolio) { this.documentFolio = documentFolio; }

    public int getInstallmentNumber() { return installmentNumber; }
    public void setInstallmentNumber(int installmentNumber) { this.installmentNumber = installmentNumber; }

    public BigDecimal getPreviousBalance() { return previousBalance; }
    public void setPreviousBalance(BigDecimal previousBalance) { this.previousBalance = previousBalance; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public String getPaymentMethodCode() { return paymentMethodCode; }
    public void setPaymentMethodCode(String paymentMethodCode) { this.paymentMethodCode = paymentMethodCode; }

    public Instant getCreatedAt() { return createdAt; }
}
