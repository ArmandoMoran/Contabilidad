package com.contabilidad.invoicing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaxLineRepository extends JpaRepository<TaxLine, UUID> {

    List<TaxLine> findBySourceTypeAndSourceId(String sourceType, UUID sourceId);

    List<TaxLine> findByCompanyIdAndPeriodKeyAndTaxCode(UUID companyId, String periodKey, String taxCode);
}
