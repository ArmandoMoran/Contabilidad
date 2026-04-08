package com.contabilidad.invoicing;

import java.math.BigDecimal;
import java.util.List;

public record InvoicePreviewDto(
    String series,
    String folio,
    String currencyCode,
    String paymentMethodCode,
    String paymentFormCode,
    String usoCfdiCode,
    String issuerRfc,
    String issuerName,
    String issuerRegimeCode,
    String receiverRfc,
    String receiverName,
    String receiverRegimeCode,
    String receiverPostalCode,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal transferredTaxTotal,
    BigDecimal withheldTaxTotal,
    BigDecimal total,
    List<InvoiceLineDto> lines,
    List<TaxLineDto> taxLines
) {}
