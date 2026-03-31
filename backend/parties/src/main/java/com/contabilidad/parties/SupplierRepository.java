package com.contabilidad.parties;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Page<Supplier> findByCompanyIdAndDeletedAtIsNull(UUID companyId, Pageable pageable);

    Optional<Supplier> findByCompanyIdAndRfc(UUID companyId, String rfc);

    boolean existsByCompanyIdAndRfc(UUID companyId, String rfc);

    Optional<Supplier> findByCompanyIdAndIdAndDeletedAtIsNull(UUID companyId, UUID id);
}
