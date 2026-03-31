package com.contabilidad.parties;

import jakarta.validation.constraints.NotBlank;

public record CreateSupplierRequest(
    @NotBlank String rfc,
    @NotBlank String legalName,
    String tradeName,
    String email,
    String phone,
    String website,
    @NotBlank String fiscalRegimeCode,
    String defaultFormaPagoCode,
    String nationality,
    String diotOperationType,
    String notes
) {}
