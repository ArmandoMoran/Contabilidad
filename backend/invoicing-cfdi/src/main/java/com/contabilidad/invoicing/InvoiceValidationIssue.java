package com.contabilidad.invoicing;

public record InvoiceValidationIssue(
    String fieldPath,
    String message,
    String code
) {}
