package com.contabilidad.expenses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    Page<Expense> findByCompanyId(UUID companyId, Pageable pageable);

    Page<Expense> findByCompanyIdAndStatus(UUID companyId, String status, Pageable pageable);

    Optional<Expense> findByCompanyIdAndCfdiUuid(UUID companyId, UUID cfdiUuid);

    Optional<Expense> findByCompanyIdAndId(UUID companyId, UUID id);
}
