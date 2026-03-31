package com.contabilidad.tax;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaxRuleResolver {

    private final TaxRuleRepository taxRuleRepository;

    public TaxRuleResolver(TaxRuleRepository taxRuleRepository) {
        this.taxRuleRepository = taxRuleRepository;
    }

    public List<TaxRule> resolveRules(String taxpayerType, String regimeCode, String operation,
                                       String region, LocalDate date) {
        return taxRuleRepository.findByActiveTrue().stream()
            .filter(r -> r.getAppliesToTaxpayerType() == null || r.getAppliesToTaxpayerType().equals(taxpayerType))
            .filter(r -> r.getAppliesToRegimeCode() == null || r.getAppliesToRegimeCode().equals(regimeCode))
            .filter(r -> r.getAppliesToOperation() == null || r.getAppliesToOperation().equals(operation))
            .filter(r -> r.getAppliesToRegion() == null || r.getAppliesToRegion().equals(region))
            .filter(r -> !date.isBefore(r.getValidFrom()))
            .filter(r -> r.getValidTo() == null || !date.isAfter(r.getValidTo()))
            .toList();
    }
}
