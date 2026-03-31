package com.contabilidad.payments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ApplyPaymentRequest(
    @NotBlank String documentType,
    @NotNull UUID documentId,
    UUID documentUuid,
    String documentSeries,
    String documentFolio,
    int installmentNumber,
    @NotNull BigDecimal previousBalance,
    @NotNull BigDecimal amountPaid,
    @NotNull BigDecimal remainingBalance,
    String currencyCode
) {}
