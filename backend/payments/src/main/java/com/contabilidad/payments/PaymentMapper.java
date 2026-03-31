package com.contabilidad.payments;

import java.util.UUID;

public final class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentDto toDto(Payment p) {
        return new PaymentDto(
            p.getId(),
            p.getCompanyId(),
            p.getPaymentDirection(),
            p.getStatus(),
            p.getPaymentFormCode(),
            p.getCurrencyCode(),
            p.getExchangeRate(),
            p.getAmount(),
            p.getPaidAt(),
            p.getOperationNumber(),
            p.getPayerRfc(),
            p.getPayerName(),
            p.getPayerBankRfc(),
            p.getPayerAccount(),
            p.getPayeeRfc(),
            p.getPayeeName(),
            p.getPayeeBankRfc(),
            p.getPayeeAccount(),
            p.getNotes(),
            p.getRepInvoiceId(),
            p.getCreatedAt(),
            p.getUpdatedAt()
        );
    }

    public static PaymentApplicationDto toApplicationDto(PaymentApplication a) {
        return new PaymentApplicationDto(
            a.getId(),
            a.getPaymentId(),
            a.getDocumentType(),
            a.getDocumentId(),
            a.getDocumentUuid(),
            a.getDocumentSeries(),
            a.getDocumentFolio(),
            a.getInstallmentNumber(),
            a.getPreviousBalance(),
            a.getAmountPaid(),
            a.getRemainingBalance(),
            a.getCurrencyCode(),
            a.getExchangeRate(),
            a.getPaymentMethodCode(),
            a.getCreatedAt()
        );
    }

    public static Payment toEntity(UUID companyId, CreatePaymentRequest r) {
        Payment p = new Payment();
        p.setCompanyId(companyId);
        p.setPaymentDirection(r.paymentDirection());
        p.setPaymentFormCode(r.paymentFormCode());
        if (r.currencyCode() != null) {
            p.setCurrencyCode(r.currencyCode());
        }
        p.setAmount(r.amount());
        p.setPaidAt(r.paidAt());
        p.setOperationNumber(r.operationNumber());
        p.setPayerRfc(r.payerRfc());
        p.setPayerName(r.payerName());
        p.setPayerBankRfc(r.payerBankRfc());
        p.setPayerAccount(r.payerAccount());
        p.setPayeeRfc(r.payeeRfc());
        p.setPayeeName(r.payeeName());
        p.setPayeeBankRfc(r.payeeBankRfc());
        p.setPayeeAccount(r.payeeAccount());
        p.setNotes(r.notes());
        return p;
    }

    public static PaymentApplication toApplicationEntity(UUID companyId, UUID paymentId, ApplyPaymentRequest r) {
        PaymentApplication a = new PaymentApplication();
        a.setCompanyId(companyId);
        a.setPaymentId(paymentId);
        a.setDocumentType(r.documentType());
        a.setDocumentId(r.documentId());
        a.setDocumentUuid(r.documentUuid());
        a.setDocumentSeries(r.documentSeries());
        a.setDocumentFolio(r.documentFolio());
        a.setInstallmentNumber(r.installmentNumber());
        a.setPreviousBalance(r.previousBalance());
        a.setAmountPaid(r.amountPaid());
        a.setRemainingBalance(r.remainingBalance());
        if (r.currencyCode() != null) {
            a.setCurrencyCode(r.currencyCode());
        }
        return a;
    }
}
