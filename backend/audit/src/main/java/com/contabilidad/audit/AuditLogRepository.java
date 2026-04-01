package com.contabilidad.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByCompanyIdOrderByCreatedAtDesc(UUID companyId, Pageable pageable);

    List<AuditLog> findByCompanyIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(UUID companyId, String entityType, UUID entityId);
}
