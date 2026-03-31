package com.contabilidad.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCompanyRequest(
    @NotBlank @Size(max = 13) String rfc,
    @NotBlank String legalName,
    @NotBlank String taxpayerType,
    @NotBlank @Size(max = 3) String fiscalRegimeCode,
    String taxZoneProfile,
    @Size(max = 5) String postalCode,
    String logoUrl
) {}
