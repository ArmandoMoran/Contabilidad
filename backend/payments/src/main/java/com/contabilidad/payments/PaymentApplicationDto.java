package com.contabilidad.payments;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentApplicationDto(
    UUID id,
    UUID paymentId,
    String documentType,
    UUID documentId,
    UUID documentUuid,
    String documentSeries,
    String documentFolio,
    int installmentNumber,
    BigDecimal previousBalance,
    BigDecimal amountPaid,
    BigDecimal remainingBalance,
    String currencyCode,
    BigDecimal exchangeRate,
    String paymentMethodCode,
    Instant createdAt
) {}
