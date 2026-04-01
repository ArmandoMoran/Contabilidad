package com.contabilidad.audit;

import com.contabilidad.shared.PageResponse;
import com.contabilidad.shared.SecurityContextUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public PageResponse<AuditLog> list(Pageable pageable) {
        var companyId = SecurityContextUtils.currentCompanyId();
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
        return auditLogRepository.findByCompanyIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            SecurityContextUtils.currentCompanyId(),
            entityType,
            entityId
        );
    }
}
