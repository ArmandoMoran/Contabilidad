package com.contabilidad.products;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProductTaxProfileDto(
    UUID id,
    UUID productId,
    String taxCode,
    String factorType,
    BigDecimal rate,
    boolean isTransfer,
    boolean isWithholding,
    LocalDate validFrom,
    LocalDate validTo,
    boolean active
) {}
