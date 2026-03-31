package com.contabilidad.parties;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Page<Client> findByCompanyIdAndDeletedAtIsNull(UUID companyId, Pageable pageable);

    Optional<Client> findByCompanyIdAndRfc(UUID companyId, String rfc);

    boolean existsByCompanyIdAndRfc(UUID companyId, String rfc);

    Optional<Client> findByCompanyIdAndIdAndDeletedAtIsNull(UUID companyId, UUID id);
}
