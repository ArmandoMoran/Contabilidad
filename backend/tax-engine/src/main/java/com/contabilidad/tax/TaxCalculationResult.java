package com.contabilidad.tax;

import java.math.BigDecimal;

public record TaxCalculationResult(
    String taxCode,
    String factorType,
    BigDecimal rate,
    BigDecimal baseAmount,
    BigDecimal taxAmount,
    boolean isTransfer,
    boolean isWithholding
) {}
