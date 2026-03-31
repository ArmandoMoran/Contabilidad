package com.contabilidad.tax;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ObligationRuleRepository extends JpaRepository<ObligationRule, UUID> {

    List<ObligationRule> findByActiveTrue();

    List<ObligationRule> findByAppliesToRegimeCodeAndActiveTrue(String regimeCode);
}
