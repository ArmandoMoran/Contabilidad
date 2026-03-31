package com.contabilidad.audit;

import com.contabilidad.shared.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public PageResponse<AuditLog> list(
            @RequestHeader("X-Company-Id") UUID companyId,
            Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
        return PageResponse.of(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public List<AuditLog> byEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
}
