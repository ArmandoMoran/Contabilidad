package com.contabilidad.audit;

import java.util.UUID;

public record AuditEntry(
    UUID companyId,
    UUID userId,
    String module,
    String action,
    String entityType,
    UUID entityId,
    String beforeHash,
    String afterHash,
    String details,
    String ipAddress,
    String userAgent,
    String traceId
) {}
