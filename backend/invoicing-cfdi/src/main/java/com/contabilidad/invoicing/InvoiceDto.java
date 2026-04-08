package com.contabilidad.invoicing;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InvoiceDto(
    UUID id,
    UUID companyId,
    UUID clientId,
    String invoiceType,
    String status,
    String series,
    String folio,
    Instant issuedAt,
    String currencyCode,
    BigDecimal subtotal,
    BigDecimal transferredTaxTotal,
    BigDecimal withheldTaxTotal,
    BigDecimal total,
    UUID pacUuid,
    String receiverRfc,
    String receiverName
) {}
