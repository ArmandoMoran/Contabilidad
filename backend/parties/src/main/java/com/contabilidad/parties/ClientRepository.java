package com.contabilidad.parties;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Page<Client> findByCompanyIdAndDeletedAtIsNull(UUID companyId, Pageable pageable);

    @Query("""
        select c
        from Client c
        where c.companyId = :companyId
          and c.deletedAt is null
          and (
            :search is null
            or lower(c.legalName) like lower(concat('%', :search, '%'))
            or lower(coalesce(c.tradeName, '')) like lower(concat('%', :search, '%'))
            or lower(c.rfc) like lower(concat('%', :search, '%'))
            or lower(coalesce(c.email, '')) like lower(concat('%', :search, '%'))
          )
        """)
    Page<Client> searchByCompanyId(@Param("companyId") UUID companyId, @Param("search") String search, Pageable pageable);

    List<Client> findAllByCompanyIdAndDeletedAtIsNull(UUID companyId);

    Optional<Client> findByCompanyIdAndRfc(UUID companyId, String rfc);

    boolean existsByCompanyIdAndRfc(UUID companyId, String rfc);

    Optional<Client> findByCompanyIdAndIdAndDeletedAtIsNull(UUID companyId, UUID id);
}
