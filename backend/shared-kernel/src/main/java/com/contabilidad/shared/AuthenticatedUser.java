package com.contabilidad.shared;

import java.util.UUID;

public record AuthenticatedUser(
    UUID id,
    UUID companyId,
    String email,
    String fullName,
    String role
) {}
