package com.contabilidad.invoicing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Page<Invoice> findByCompanyIdAndStatus(UUID companyId, String status, Pageable pageable);

    @Query("""
        select i
        from Invoice i
        where i.companyId = :companyId
          and (:status is null or i.status = :status)
          and (
            :search is null
            or lower(coalesce(i.receiverName, '')) like lower(concat('%', :search, '%'))
            or lower(coalesce(i.receiverRfc, '')) like lower(concat('%', :search, '%'))
            or lower(coalesce(i.series, '')) like lower(concat('%', :search, '%'))
            or lower(coalesce(i.folio, '')) like lower(concat('%', :search, '%'))
            or (:uuidSearch is not null and i.pacUuid = :uuidSearch)
          )
        order by coalesce(i.issuedAt, i.createdAt) desc, i.createdAt desc
        """)
    Page<Invoice> searchByCompanyId(
        @Param("companyId") UUID companyId,
        @Param("status") String status,
        @Param("search") String search,
        @Param("uuidSearch") UUID uuidSearch,
        Pageable pageable);

    List<Invoice> findByCompanyId(UUID companyId);

    Optional<Invoice> findByCompanyIdAndId(UUID companyId, UUID id);

    Optional<Invoice> findByCompanyIdAndIdempotencyKey(UUID companyId, String idempotencyKey);

    boolean existsByCompanyIdAndPacUuid(UUID companyId, UUID pacUuid);
}
