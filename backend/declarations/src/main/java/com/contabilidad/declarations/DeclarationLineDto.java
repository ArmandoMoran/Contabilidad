package com.contabilidad.declarations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DeclarationLineDto(
    UUID id,
    UUID declarationRunId,
    String lineType,
    String sourceType,
    UUID sourceId,
    UUID sourceLineId,
    String concept,
    BigDecimal baseAmount,
    BigDecimal taxAmount,
    BigDecimal rate,
    String periodKey,
    String notes,
    Instant createdAt
) {}
