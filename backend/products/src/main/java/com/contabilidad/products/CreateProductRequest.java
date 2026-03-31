package com.contabilidad.products;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
    String internalCode,
    @NotBlank String internalName,
    String description,
    @NotBlank String satProductCode,
    @NotBlank String satUnitCode,
    BigDecimal unitPrice,
    String currencyCode,
    String objetoImpCode,
    String cuentaPredial,
    @Valid List<CreateTaxProfileRequest> taxProfiles
) {}
