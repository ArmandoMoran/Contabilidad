package com.contabilidad.invoicing;

import java.util.List;

public record ValidationResult(
    List<InvoiceValidationIssue> issues,
    boolean valid,
    InvoicePreviewDto preview
) {
    public static ValidationResult ok(InvoicePreviewDto preview) {
        return new ValidationResult(List.of(), true, preview);
    }

    public static ValidationResult withIssues(List<InvoiceValidationIssue> issues, InvoicePreviewDto preview) {
        return new ValidationResult(issues, false, preview);
    }
}
