package com.contabilidad.parties;

import java.time.Instant;
import java.util.UUID;

public record ContactDto(
    UUID id,
    UUID companyId,
    String partyType,
    UUID partyId,
    String fullName,
    String email,
    String phone,
    String position,
    boolean isPrimary,
    Instant createdAt,
    Instant updatedAt
) {}
