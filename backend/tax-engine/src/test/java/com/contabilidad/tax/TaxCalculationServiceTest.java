package com.contabilidad.tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaxCalculationServiceTest {

    private TaxCalculationService service;

    @BeforeEach
    void setUp() {
        service = new TaxCalculationService();
    }

    @Test
    void calculateLineTaxes_iva16Percent() {
        TaxRule rule = buildRule("002", "Tasa", new BigDecimal("0.160000"), true, false);
        BigDecimal base = new BigDecimal("1000.00");

        List<TaxCalculationResult> results = service.calculateLineTaxes(base, List.of(rule));

        assertThat(results).hasSize(1);
        TaxCalculationResult r = results.getFirst();
        assertThat(r.taxCode()).isEqualTo("002");
        assertThat(r.baseAmount()).isEqualByComparingTo(base);
        assertThat(r.taxAmount()).isEqualByComparingTo(new BigDecimal("160.00"));
        assertThat(r.isTransfer()).isTrue();
        assertThat(r.isWithholding()).isFalse();
    }

    @Test
    void calculateLineTaxes_exentoReturnsZeroTax() {
        TaxRule rule = buildRule("002", "Exento", BigDecimal.ZERO, true, false);
        BigDecimal base = new BigDecimal("5000.00");

        List<TaxCalculationResult> results = service.calculateLineTaxes(base, List.of(rule));

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().taxAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(results.getFirst().factorType()).isEqualTo("Exento");
    }

    @Test
    void calculateLineTaxes_ivaFronterizo8Percent() {
        TaxRule rule = buildRule("002", "Tasa", new BigDecimal("0.080000"), true, false);
        BigDecimal base = new BigDecimal("1000.00");

        List<TaxCalculationResult> results = service.calculateLineTaxes(base, List.of(rule));

        assertThat(results.getFirst().taxAmount()).isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    void calculateLineTaxes_withholdingRule() {
        TaxRule rule = buildRule("001", "Tasa", new BigDecimal("0.100000"), false, true);
        BigDecimal base = new BigDecimal("2000.00");

        List<TaxCalculationResult> results = service.calculateLineTaxes(base, List.of(rule));

        assertThat(results).hasSize(1);
        TaxCalculationResult r = results.getFirst();
        assertThat(r.taxAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(r.isTransfer()).isFalse();
        assertThat(r.isWithholding()).isTrue();
    }

    @Test
    void calculateLineTaxes_multipleRules() {
        TaxRule ivaTransfer = buildRule("002", "Tasa", new BigDecimal("0.160000"), true, false);
        TaxRule isrRetention = buildRule("001", "Tasa", new BigDecimal("0.100000"), false, true);
        BigDecimal base = new BigDecimal("1500.00");

        List<TaxCalculationResult> results = service.calculateLineTaxes(base, List.of(ivaTransfer, isrRetention));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).taxAmount()).isEqualByComparingTo(new BigDecimal("240.00"));
        assertThat(results.get(1).taxAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    void calculateLineTaxes_emptyRulesReturnsEmpty() {
        List<TaxCalculationResult> results = service.calculateLineTaxes(new BigDecimal("1000.00"), List.of());
        assertThat(results).isEmpty();
    }

    @Test
    void calculateLineTaxes_roundsHalfUp() {
        TaxRule rule = buildRule("002", "Tasa", new BigDecimal("0.160000"), true, false);
        BigDecimal base = new BigDecimal("33.33");

        List<TaxCalculationResult> results = service.calculateLineTaxes(base, List.of(rule));

        // 33.33 * 0.16 = 5.3328, rounded HALF_UP to 5.33
        assertThat(results.getFirst().taxAmount()).isEqualByComparingTo(new BigDecimal("5.33"));
    }

    @Test
    void calculateLineTaxes_zeroRate() {
        TaxRule rule = buildRule("002", "Tasa", BigDecimal.ZERO, true, false);
        BigDecimal base = new BigDecimal("1000.00");

        List<TaxCalculationResult> results = service.calculateLineTaxes(base, List.of(rule));

        assertThat(results.getFirst().taxAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private TaxRule buildRule(String taxCode, String factorType, BigDecimal rate, boolean transfer, boolean withholding) {
        TaxRule rule = new TaxRule();
        rule.setId(UUID.randomUUID());
        rule.setRuleName("test-rule");
        rule.setTaxCode(taxCode);
        rule.setFactorType(factorType);
        rule.setRate(rate);
        rule.setTransfer(transfer);
        rule.setWithholding(withholding);
        rule.setValidFrom(LocalDate.of(2024, 1, 1));
        return rule;
    }
}