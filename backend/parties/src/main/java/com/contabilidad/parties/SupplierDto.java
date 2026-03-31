package com.contabilidad.parties;

import java.time.Instant;
import java.util.UUID;

public record SupplierDto(
    UUID id,
    UUID companyId,
    String rfc,
    String legalName,
    String tradeName,
    String email,
    String phone,
    String website,
    String fiscalRegimeCode,
    String defaultFormaPagoCode,
    String nationality,
    String diotOperationType,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
