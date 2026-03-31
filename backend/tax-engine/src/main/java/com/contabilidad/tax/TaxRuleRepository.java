package com.contabilidad.tax;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaxRuleRepository extends JpaRepository<TaxRule, UUID> {

    List<TaxRule> findByTaxCodeAndIsTransferAndIsWithholdingAndActiveTrue(
        String taxCode, boolean isTransfer, boolean isWithholding);

    List<TaxRule> findByActiveTrue();
}
