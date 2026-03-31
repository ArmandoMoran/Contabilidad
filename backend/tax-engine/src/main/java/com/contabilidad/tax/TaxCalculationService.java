package com.contabilidad.tax;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TaxCalculationService {

    public List<TaxCalculationResult> calculateLineTaxes(BigDecimal baseAmount, List<TaxRule> applicableRules) {
        return applicableRules.stream()
            .map(rule -> {
                BigDecimal taxAmount;
                if ("Exento".equalsIgnoreCase(rule.getFactorType())) {
                    taxAmount = BigDecimal.ZERO;
                } else {
                    taxAmount = baseAmount.multiply(rule.getRate()).setScale(2, RoundingMode.HALF_UP);
                }
                return new TaxCalculationResult(
                    rule.getTaxCode(),
                    rule.getFactorType(),
                    rule.getRate(),
                    baseAmount,
                    taxAmount,
                    rule.isTransfer(),
                    rule.isWithholding()
                );
            })
            .toList();
    }
}
