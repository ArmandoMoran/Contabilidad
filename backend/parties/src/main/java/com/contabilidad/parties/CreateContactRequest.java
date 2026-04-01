package com.contabilidad.parties;

import jakarta.validation.constraints.NotBlank;

public record CreateContactRequest(
    @NotBlank String fullName,
    String email,
    String phone,
    String position,
    boolean isPrimary
) {}
