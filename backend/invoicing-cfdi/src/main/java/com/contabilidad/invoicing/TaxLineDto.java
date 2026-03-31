package com.contabilidad.invoicing;

import java.math.BigDecimal;
import java.util.UUID;

public record TaxLineDto(
    UUID id,
    String sourceType,
    UUID sourceId,
    UUID sourceLineId,
    String taxCode,
    String factorType,
    BigDecimal rate,
    BigDecimal baseAmount,
    BigDecimal taxAmount,
    boolean isTransfer,
    boolean isWithholding,
    String periodKey
) {}
