package com.contabilidad.invoicing;

import com.contabilidad.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invoices")
public class Invoice extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "invoice_type", nullable = false, length = 1)
    private String invoiceType;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "DRAFT";

    @Column(name = "series", length = 25)
    private String series;

    @Column(name = "folio", length = 40)
    private String folio;

    @Column(name = "cfdi_version", nullable = false, length = 10)
    private String cfdiVersion = "4.0";

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "certified_at")
    private Instant certifiedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "MXN";

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 6)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Column(name = "payment_method_code", nullable = false, length = 3)
    private String paymentMethodCode;

    @Column(name = "payment_form_code", length = 2)
    private String paymentFormCode;

    @Column(name = "uso_cfdi_code", nullable = false, length = 4)
    private String usoCfdiCode;

    @Column(name = "export_code", nullable = false, length = 2)
    private String exportCode = "01";

    @Column(name = "global_periodicity", length = 5)
    private String globalPeriodicity;

    @Column(name = "global_month", length = 2)
    private String globalMonth;

    @Column(name = "global_year", length = 4)
    private String globalYear;

    @Column(name = "issuer_rfc", nullable = false, length = 13)
    private String issuerRfc;

    @Column(name = "issuer_name", nullable = false)
    private String issuerName;

    @Column(name = "issuer_regime_code", nullable = false, length = 3)
    private String issuerRegimeCode;

    @Column(name = "receiver_rfc", nullable = false, length = 13)
    private String receiverRfc;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_regime_code", nullable = false, length = 3)
    private String receiverRegimeCode;

    @Column(name = "receiver_postal_code", nullable = false, length = 5)
    private String receiverPostalCode;

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

    @Column(name = "pac_uuid")
    private UUID pacUuid;

    @Column(name = "pac_cert_number", length = 30)
    private String pacCertNumber;

    @Column(name = "sat_cert_number", length = 30)
    private String satCertNumber;

    @Column(name = "pac_seal", columnDefinition = "text")
    private String pacSeal;

    @Column(name = "sat_seal", columnDefinition = "text")
    private String satSeal;

    @Column(name = "stamp_date")
    private Instant stampDate;

    @Column(name = "original_chain", columnDefinition = "text")
    private String originalChain;

    @Column(name = "pac_status", length = 30)
    private String pacStatus;

    @Column(name = "cancel_reason_code", length = 2)
    private String cancelReasonCode;

    @Column(name = "cancel_replacement_uuid")
    private UUID cancelReplacementUuid;

    @Column(name = "cancel_acuse", columnDefinition = "text")
    private String cancelAcuse;

    @Column(name = "xml_object_key", length = 500)
    private String xmlObjectKey;

    @Column(name = "pdf_object_key", length = 500)
    private String pdfObjectKey;

    @Column(name = "acuse_object_key", length = 500)
    private String acuseObjectKey;

    @Column(name = "fiscal_snapshot", columnDefinition = "jsonb", nullable = false)
    private String fiscalSnapshot = "{}";

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    public Invoice() {}

    // -- Getters & Setters --

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public String getCfdiVersion() { return cfdiVersion; }
    public void setCfdiVersion(String cfdiVersion) { this.cfdiVersion = cfdiVersion; }

    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }

    public Instant getCertifiedAt() { return certifiedAt; }
    public void setCertifiedAt(Instant certifiedAt) { this.certifiedAt = certifiedAt; }

    public Instant getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Instant cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public String getPaymentMethodCode() { return paymentMethodCode; }
    public void setPaymentMethodCode(String paymentMethodCode) { this.paymentMethodCode = paymentMethodCode; }

    public String getPaymentFormCode() { return paymentFormCode; }
    public void setPaymentFormCode(String paymentFormCode) { this.paymentFormCode = paymentFormCode; }

    public String getUsoCfdiCode() { return usoCfdiCode; }
    public void setUsoCfdiCode(String usoCfdiCode) { this.usoCfdiCode = usoCfdiCode; }

    public String getExportCode() { return exportCode; }
    public void setExportCode(String exportCode) { this.exportCode = exportCode; }

    public String getGlobalPeriodicity() { return globalPeriodicity; }
    public void setGlobalPeriodicity(String globalPeriodicity) { this.globalPeriodicity = globalPeriodicity; }

    public String getGlobalMonth() { return globalMonth; }
    public void setGlobalMonth(String globalMonth) { this.globalMonth = globalMonth; }

    public String getGlobalYear() { return globalYear; }
    public void setGlobalYear(String globalYear) { this.globalYear = globalYear; }

    public String getIssuerRfc() { return issuerRfc; }
    public void setIssuerRfc(String issuerRfc) { this.issuerRfc = issuerRfc; }

    public String getIssuerName() { return issuerName; }
    public void setIssuerName(String issuerName) { this.issuerName = issuerName; }

    public String getIssuerRegimeCode() { return issuerRegimeCode; }
    public void setIssuerRegimeCode(String issuerRegimeCode) { this.issuerRegimeCode = issuerRegimeCode; }

    public String getReceiverRfc() { return receiverRfc; }
    public void setReceiverRfc(String receiverRfc) { this.receiverRfc = receiverRfc; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverRegimeCode() { return receiverRegimeCode; }
    public void setReceiverRegimeCode(String receiverRegimeCode) { this.receiverRegimeCode = receiverRegimeCode; }

    public String getReceiverPostalCode() { return receiverPostalCode; }
    public void setReceiverPostalCode(String receiverPostalCode) { this.receiverPostalCode = receiverPostalCode; }

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

    public UUID getPacUuid() { return pacUuid; }
    public void setPacUuid(UUID pacUuid) { this.pacUuid = pacUuid; }

    public String getPacCertNumber() { return pacCertNumber; }
    public void setPacCertNumber(String pacCertNumber) { this.pacCertNumber = pacCertNumber; }

    public String getSatCertNumber() { return satCertNumber; }
    public void setSatCertNumber(String satCertNumber) { this.satCertNumber = satCertNumber; }

    public String getPacSeal() { return pacSeal; }
    public void setPacSeal(String pacSeal) { this.pacSeal = pacSeal; }

    public String getSatSeal() { return satSeal; }
    public void setSatSeal(String satSeal) { this.satSeal = satSeal; }

    public Instant getStampDate() { return stampDate; }
    public void setStampDate(Instant stampDate) { this.stampDate = stampDate; }

    public String getOriginalChain() { return originalChain; }
    public void setOriginalChain(String originalChain) { this.originalChain = originalChain; }

    public String getPacStatus() { return pacStatus; }
    public void setPacStatus(String pacStatus) { this.pacStatus = pacStatus; }

    public String getCancelReasonCode() { return cancelReasonCode; }
    public void setCancelReasonCode(String cancelReasonCode) { this.cancelReasonCode = cancelReasonCode; }

    public UUID getCancelReplacementUuid() { return cancelReplacementUuid; }
    public void setCancelReplacementUuid(UUID cancelReplacementUuid) { this.cancelReplacementUuid = cancelReplacementUuid; }

    public String getCancelAcuse() { return cancelAcuse; }
    public void setCancelAcuse(String cancelAcuse) { this.cancelAcuse = cancelAcuse; }

    public String getXmlObjectKey() { return xmlObjectKey; }
    public void setXmlObjectKey(String xmlObjectKey) { this.xmlObjectKey = xmlObjectKey; }

    public String getPdfObjectKey() { return pdfObjectKey; }
    public void setPdfObjectKey(String pdfObjectKey) { this.pdfObjectKey = pdfObjectKey; }

    public String getAcuseObjectKey() { return acuseObjectKey; }
    public void setAcuseObjectKey(String acuseObjectKey) { this.acuseObjectKey = acuseObjectKey; }

    public String getFiscalSnapshot() { return fiscalSnapshot; }
    public void setFiscalSnapshot(String fiscalSnapshot) { this.fiscalSnapshot = fiscalSnapshot; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
