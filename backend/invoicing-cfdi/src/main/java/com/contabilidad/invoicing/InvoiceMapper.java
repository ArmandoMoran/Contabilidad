package com.contabilidad.invoicing;

import java.util.UUID;

public final class InvoiceMapper {

    private InvoiceMapper() {}

    public static InvoiceDto toDto(Invoice inv) {
        return new InvoiceDto(
            inv.getId(),
            inv.getCompanyId(),
            inv.getClientId(),
            inv.getInvoiceType(),
            inv.getStatus(),
            inv.getSeries(),
            inv.getFolio(),
            inv.getCfdiVersion(),
            inv.getIssuedAt(),
            inv.getCertifiedAt(),
            inv.getCancelledAt(),
            inv.getCurrencyCode(),
            inv.getExchangeRate(),
            inv.getPaymentMethodCode(),
            inv.getPaymentFormCode(),
            inv.getUsoCfdiCode(),
            inv.getExportCode(),
            inv.getIssuerRfc(),
            inv.getIssuerName(),
            inv.getIssuerRegimeCode(),
            inv.getReceiverRfc(),
            inv.getReceiverName(),
            inv.getReceiverRegimeCode(),
            inv.getReceiverPostalCode(),
            inv.getSubtotal(),
            inv.getDiscount(),
            inv.getTransferredTaxTotal(),
            inv.getWithheldTaxTotal(),
            inv.getTotal(),
            inv.getPacUuid(),
            inv.getPacCertNumber(),
            inv.getSatCertNumber(),
            inv.getPacStatus(),
            inv.getCancelReasonCode(),
            inv.getCancelReplacementUuid(),
            inv.getXmlObjectKey(),
            inv.getPdfObjectKey(),
            inv.getIdempotencyKey(),
            inv.getCreatedAt(),
            inv.getUpdatedAt()
        );
    }

    public static InvoiceLineDto toLineDto(InvoiceLine l) {
        return new InvoiceLineDto(
            l.getId(),
            l.getInvoiceId(),
            l.getLineNumber(),
            l.getProductId(),
            l.getSatProductCode(),
            l.getDescription(),
            l.getSatUnitCode(),
            l.getUnitName(),
            l.getQuantity(),
            l.getUnitPrice(),
            l.getDiscount(),
            l.getSubtotal(),
            l.getObjetoImpCode()
        );
    }

    public static TaxLineDto toTaxLineDto(TaxLine t) {
        return new TaxLineDto(
            t.getId(),
            t.getSourceType(),
            t.getSourceId(),
            t.getSourceLineId(),
            t.getTaxCode(),
            t.getFactorType(),
            t.getRate(),
            t.getBaseAmount(),
            t.getTaxAmount(),
            t.isTransfer(),
            t.isWithholding(),
            t.getPeriodKey()
        );
    }

    public static Invoice toEntity(UUID companyId, CreateInvoiceDraftRequest r) {
        Invoice inv = new Invoice();
        inv.setCompanyId(companyId);
        inv.setClientId(r.clientId());
        inv.setInvoiceType(r.invoiceType());
        inv.setPaymentMethodCode(r.paymentMethodCode());
        inv.setPaymentFormCode(r.paymentFormCode());
        inv.setUsoCfdiCode(r.usoCfdiCode());
        if (r.currencyCode() != null) {
            inv.setCurrencyCode(r.currencyCode());
        }
        // Set placeholder issuer/receiver to satisfy NOT NULL constraints;
        // these will be filled from company/client data when stamping
        inv.setIssuerRfc("PENDING");
        inv.setIssuerName("PENDING");
        inv.setIssuerRegimeCode("601");
        inv.setReceiverRfc("PENDING");
        inv.setReceiverName("PENDING");
        inv.setReceiverRegimeCode("601");
        inv.setReceiverPostalCode("00000");
        return inv;
    }

    public static InvoiceLine toLineEntity(UUID companyId, UUID invoiceId, int lineNumber, InvoiceLineRequest r) {
        InvoiceLine line = new InvoiceLine();
        line.setCompanyId(companyId);
        line.setInvoiceId(invoiceId);
        line.setLineNumber(lineNumber);
        line.setProductId(r.productId());
        line.setDescription(r.description());
        line.setQuantity(r.quantity());
        line.setUnitPrice(r.unitPrice());
        line.setSatProductCode(r.satProductCode());
        line.setSatUnitCode(r.satUnitCode());
        line.setSubtotal(r.quantity().multiply(r.unitPrice()));
        return line;
    }
}
