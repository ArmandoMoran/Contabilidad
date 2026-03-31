package com.contabilidad.declarations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DeclarationRunDto(
    UUID id,
    UUID companyId,
    UUID templateId,
    String declarationType,
    String periodKey,
    int fiscalYear,
    Integer fiscalMonth,
    String status,
    BigDecimal totalIncome,
    BigDecimal totalDeductions,
    BigDecimal taxBase,
    BigDecimal taxDetermined,
    BigDecimal taxWithheld,
    BigDecimal taxPaidPrevious,
    BigDecimal taxPayable,
    BigDecimal taxInFavor,
    Instant frozenAt,
    String notes,
    Instant createdAt,
    Instant updatedAt
) {}
