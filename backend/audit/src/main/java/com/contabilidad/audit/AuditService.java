package com.contabilidad.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog log(AuditEntry entry) {
        AuditLog log = new AuditLog();
        log.setCompanyId(entry.companyId());
        log.setUserId(entry.userId());
        log.setModule(entry.module());
        log.setAction(entry.action());
        log.setEntityType(entry.entityType());
        log.setEntityId(entry.entityId());
        log.setBeforeHash(entry.beforeHash());
        log.setAfterHash(entry.afterHash());
        log.setDetails(entry.details());
        log.setIpAddress(entry.ipAddress());
        log.setUserAgent(entry.userAgent());
        log.setTraceId(entry.traceId());
        log.setCreatedAt(Instant.now());
        return auditLogRepository.save(log);
    }
}
