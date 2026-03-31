package com.contabilidad.identity;

import java.util.UUID;

public record UserInfo(UUID id, UUID companyId, String email, String fullName, String role) {}
