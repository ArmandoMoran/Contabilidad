package com.contabilidad.invoicing;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineRequest(
    @NotNull UUID productId,
    String description,
    @NotNull BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal discount
) {}
