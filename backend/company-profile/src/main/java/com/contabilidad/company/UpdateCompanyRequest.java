package com.contabilidad.company;

import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
    String legalName,
    String taxpayerType,
    @Size(max = 3) String fiscalRegimeCode,
    String taxZoneProfile,
    @Size(max = 5) String postalCode,
    String logoUrl,
    Boolean active
) {}
