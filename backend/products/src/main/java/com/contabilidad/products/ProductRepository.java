package com.contabilidad.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByCompanyIdAndDeletedAtIsNull(UUID companyId, Pageable pageable);

    Optional<Product> findByCompanyIdAndIdAndDeletedAtIsNull(UUID companyId, UUID id);
}
