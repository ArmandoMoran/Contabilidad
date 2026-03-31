package com.contabilidad.company;

import java.time.Instant;
import java.util.UUID;

public record CompanyDto(
    UUID id,
    String rfc,
    String legalName,
    String taxpayerType,
    String fiscalRegimeCode,
    String taxZoneProfile,
    String postalCode,
    String logoUrl,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
