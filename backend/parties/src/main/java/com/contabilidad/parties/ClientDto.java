package com.contabilidad.parties;

import java.time.Instant;
import java.util.UUID;

public record ClientDto(
    UUID id,
    UUID companyId,
    String rfc,
    String legalName,
    String tradeName,
    String email,
    String phone,
    String website,
    String fiscalRegimeCode,
    String defaultUsoCfdiCode,
    String defaultFormaPagoCode,
    String defaultMetodoPagoCode,
    String defaultPostalCode,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
