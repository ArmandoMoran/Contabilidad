package com.contabilidad.reporting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ReportService {

    public IncomeExpenseReport getIncomeExpenseReport(UUID companyId, LocalDate from, LocalDate to) {
        // TODO: aggregate from invoices (issued=income) and expenses
        return new IncomeExpenseReport(
            from, to,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            List.of(), List.of()
        );
    }

    public TaxReport getTaxReport(UUID companyId, String periodKey) {
        // TODO: aggregate tax amounts from declaration_lines or invoice/expense taxes
        return new TaxReport(
            periodKey,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            List.of()
        );
    }

    public List<PartySummary> getClientSummary(UUID companyId, LocalDate from, LocalDate to) {
        // TODO: aggregate from issued invoices grouped by client
        return List.of();
    }

    public List<PartySummary> getSupplierSummary(UUID companyId, LocalDate from, LocalDate to) {
        // TODO: aggregate from expenses grouped by supplier
        return List.of();
    }

    public AgingReport getAgingReport(UUID companyId, String type) {
        // TODO: compute aging buckets from outstanding invoices/expenses
        return new AgingReport(type, BigDecimal.ZERO, List.of());
    }
}
