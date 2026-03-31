package com.contabilidad.invoicing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Page<Invoice> findByCompanyIdAndStatus(UUID companyId, String status, Pageable pageable);

    Optional<Invoice> findByCompanyIdAndId(UUID companyId, UUID id);

    Optional<Invoice> findByCompanyIdAndIdempotencyKey(UUID companyId, String idempotencyKey);

    boolean existsByCompanyIdAndPacUuid(UUID companyId, UUID pacUuid);
}
