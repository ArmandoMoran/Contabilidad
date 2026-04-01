package com.contabilidad.parties;

import java.time.Instant;
import java.util.UUID;

public record AddressDto(
    UUID id,
    UUID companyId,
    String partyType,
    UUID partyId,
    String addressType,
    String street1,
    String street2,
    String exteriorNumber,
    String interiorNumber,
    String neighborhood,
    String city,
    String municipalityCode,
    String stateCode,
    String postalCode,
    String countryCode,
    boolean isPrimary,
    Instant createdAt,
    Instant updatedAt
) {}
