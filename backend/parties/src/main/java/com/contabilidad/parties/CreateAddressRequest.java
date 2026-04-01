package com.contabilidad.parties;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(
    String addressType,
    @NotBlank String street1,
    String street2,
    String exteriorNumber,
    String interiorNumber,
    String neighborhood,
    String city,
    String municipalityCode,
    String stateCode,
    @NotBlank String postalCode,
    String countryCode,
    boolean isPrimary
) {}
