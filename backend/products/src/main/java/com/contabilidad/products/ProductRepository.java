package com.contabilidad.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByCompanyIdAndDeletedAtIsNull(UUID companyId, Pageable pageable);

    @Query("""
        select p
        from Product p
        where p.companyId = :companyId
          and p.deletedAt is null
          and (
            :search is null
            or lower(p.internalName) like lower(concat('%', :search, '%'))
            or lower(coalesce(p.internalCode, '')) like lower(concat('%', :search, '%'))
            or lower(coalesce(p.description, '')) like lower(concat('%', :search, '%'))
            or lower(p.satProductCode) like lower(concat('%', :search, '%'))
          )
        """)
    Page<Product> searchByCompanyId(@Param("companyId") UUID companyId, @Param("search") String search, Pageable pageable);

    Optional<Product> findByCompanyIdAndIdAndDeletedAtIsNull(UUID companyId, UUID id);
}
