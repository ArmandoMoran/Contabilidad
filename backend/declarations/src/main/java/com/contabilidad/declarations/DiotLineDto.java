package com.contabilidad.declarations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DiotLineDto(
    UUID id,
    UUID declarationRunId,
    UUID supplierId,
    String supplierRfc,
    String supplierName,
    String nationality,
    String thirdPartyType,
    BigDecimal iva16Paid,
    BigDecimal iva16Accrued,
    BigDecimal iva8Paid,
    BigDecimal iva8Accrued,
    BigDecimal iva0,
    BigDecimal ivaExempt,
    BigDecimal ivaWithheld,
    BigDecimal isrWithheld,
    String periodKey,
    Instant createdAt
) {}
