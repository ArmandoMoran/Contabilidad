package com.contabilidad.invoicing;

import com.contabilidad.company.Company;
import com.contabilidad.parties.Client;
import com.contabilidad.tax.TaxCalculationResult;

import java.math.BigDecimal;
import java.util.List;

record InvoiceComputationResult(
    Company company,
    Client client,
    String receiverPostalCode,
    String series,
    String folio,
    String paymentMethodCode,
    String paymentFormCode,
    String usoCfdiCode,
    String currencyCode,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal transferredTaxTotal,
    BigDecimal withheldTaxTotal,
    BigDecimal total,
    List<ComputedInvoiceLine> lines,
    List<InvoiceValidationIssue> issues
) {
    boolean valid() {
        return issues.isEmpty();
    }
}

record ComputedInvoiceLine(
    int lineNumber,
    java.util.UUID productId,
    String description,
    String satProductCode,
    String satUnitCode,
    String unitName,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal discount,
    BigDecimal subtotal,
    String objetoImpCode,
    String currencyCode,
    List<TaxCalculationResult> taxes
) {}
