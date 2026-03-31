package com.contabilidad.reporting;

import java.math.BigDecimal;
import java.util.UUID;

public record PartySummary(
    UUID partyId,
    String rfc,
    String legalName,
    int invoiceCount,
    BigDecimal totalAmount,
    BigDecimal totalTax,
    BigDecimal totalPaid,
    BigDecimal totalOutstanding
) {}
