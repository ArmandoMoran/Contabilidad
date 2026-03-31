package com.contabilidad.invoicing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineRequest(
    UUID productId,
    @NotBlank String description,
    @NotNull BigDecimal quantity,
    @NotNull BigDecimal unitPrice,
    @NotBlank String satProductCode,
    @NotBlank String satUnitCode,
    UUID taxProfileId
) {}
