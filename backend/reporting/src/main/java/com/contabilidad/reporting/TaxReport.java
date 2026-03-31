package com.contabilidad.reporting;

import java.math.BigDecimal;
import java.util.List;

public record TaxReport(
    String periodKey,
    BigDecimal totalTransferredIva,
    BigDecimal totalWithheldIva,
    BigDecimal totalWithheldIsr,
    BigDecimal netIvaPayable,
    List<TaxLineItem> lines
) {
    public record TaxLineItem(
        String taxCode,
        String factorType,
        BigDecimal rate,
        BigDecimal baseAmount,
        BigDecimal taxAmount,
        boolean isTransfer,
        boolean isWithholding
    ) {}
}
