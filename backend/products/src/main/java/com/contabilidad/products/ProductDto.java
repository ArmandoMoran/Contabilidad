package com.contabilidad.products;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductDto(
    UUID id,
    UUID companyId,
    String internalCode,
    String internalName,
    String description,
    String satProductCode,
    String satUnitCode,
    BigDecimal unitPrice,
    String currencyCode,
    String objetoImpCode,
    String cuentaPredial,
    boolean active,
    Instant createdAt,
    Instant updatedAt,
    List<ProductTaxProfileDto> taxProfiles
) {}
