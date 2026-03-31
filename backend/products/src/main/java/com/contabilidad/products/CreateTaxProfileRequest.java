package com.contabilidad.products;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTaxProfileRequest(
    String taxCode,
    String factorType,
    BigDecimal rate,
    Boolean isTransfer,
    Boolean isWithholding,
    LocalDate validFrom
) {}
