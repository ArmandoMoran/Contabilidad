package com.contabilidad.invoicing;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineDto(
    UUID id,
    UUID invoiceId,
    int lineNumber,
    UUID productId,
    String satProductCode,
    String description,
    String satUnitCode,
    String unitName,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal discount,
    BigDecimal subtotal,
    String objetoImpCode,
    BigDecimal transferredTaxTotal,
    BigDecimal withheldTaxTotal,
    BigDecimal total
) {}
