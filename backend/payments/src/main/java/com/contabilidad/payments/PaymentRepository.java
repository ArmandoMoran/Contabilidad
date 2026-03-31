package com.contabilidad.payments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Page<Payment> findByCompanyId(UUID companyId, Pageable pageable);

    Optional<Payment> findByCompanyIdAndId(UUID companyId, UUID id);
}
