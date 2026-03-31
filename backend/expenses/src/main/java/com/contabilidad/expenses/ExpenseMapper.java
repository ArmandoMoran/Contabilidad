package com.contabilidad.expenses;

import java.util.UUID;

public final class ExpenseMapper {

    private ExpenseMapper() {}

    public static ExpenseDto toDto(Expense e) {
        return new ExpenseDto(
            e.getId(),
            e.getCompanyId(),
            e.getSupplierId(),
            e.getExpenseType(),
            e.getStatus(),
            e.getCfdiUuid(),
            e.getCfdiVersion(),
            e.getIssuerRfc(),
            e.getIssuerName(),
            e.getReceiverRfc(),
            e.getReceiverName(),
            e.getInvoiceType(),
            e.getSeries(),
            e.getFolio(),
            e.getIssuedAt(),
            e.getPaymentMethodCode(),
            e.getPaymentFormCode(),
            e.getUsoCfdiCode(),
            e.getCurrencyCode(),
            e.getExchangeRate(),
            e.getSubtotal(),
            e.getDiscount(),
            e.getTransferredTaxTotal(),
            e.getWithheldTaxTotal(),
            e.getTotal(),
            e.getCategory(),
            e.isDeductible(),
            e.getAccountingAccount(),
            e.getNotes(),
            e.getSatValidationStatus(),
            e.getSatValidatedAt(),
            e.getXmlObjectKey(),
            e.getPdfObjectKey(),
            e.getCreatedAt(),
            e.getUpdatedAt()
        );
    }

    public static ExpenseLineDto toLineDto(ExpenseLine l) {
        return new ExpenseLineDto(
            l.getId(),
            l.getExpenseId(),
            l.getLineNumber(),
            l.getSatProductCode(),
            l.getDescription(),
            l.getSatUnitCode(),
            l.getQuantity(),
            l.getUnitPrice(),
            l.getDiscount(),
            l.getSubtotal(),
            l.getObjetoImpCode()
        );
    }

    public static Expense toEntity(UUID companyId, CreateExpenseRequest r) {
        Expense e = new Expense();
        e.setCompanyId(companyId);
        e.setSupplierId(r.supplierId());
        if (r.expenseType() != null) {
            e.setExpenseType(r.expenseType());
        }
        e.setTotal(r.total());
        e.setSubtotal(r.total()); // For manual expenses, subtotal = total
        if (r.currencyCode() != null) {
            e.setCurrencyCode(r.currencyCode());
        }
        e.setCategory(r.category());
        if (r.deductible() != null) {
            e.setDeductible(r.deductible());
        }
        e.setAccountingAccount(r.accountingAccount());
        // Map description to notes (manual expenses use description as notes)
        if (r.description() != null && !r.description().isBlank()) {
            e.setNotes(r.description());
        }
        if (r.notes() != null) {
            e.setNotes(r.notes());
        }
        return e;
    }
}
