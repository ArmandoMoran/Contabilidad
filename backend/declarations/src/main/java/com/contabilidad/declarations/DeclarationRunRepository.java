package com.contabilidad.declarations;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeclarationRunRepository extends JpaRepository<DeclarationRun, UUID> {

    Optional<DeclarationRun> findByCompanyIdAndDeclarationTypeAndPeriodKey(
        UUID companyId, String declarationType, String periodKey);

    List<DeclarationRun> findByCompanyIdAndFiscalYear(UUID companyId, int fiscalYear);
}
