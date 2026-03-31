package com.contabilidad.parties;

import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(
    @NotBlank String rfc,
    @NotBlank String legalName,
    String tradeName,
    String email,
    String phone,
    String website,
    @NotBlank String fiscalRegimeCode,
    String defaultUsoCfdiCode,
    String defaultFormaPagoCode,
    String defaultMetodoPagoCode,
    String defaultPostalCode,
    String notes
) {}
