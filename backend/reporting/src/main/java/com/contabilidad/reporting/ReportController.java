package com.contabilidad.reporting;

import com.contabilidad.shared.SecurityContextUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/income-expense")
    public IncomeExpenseReport incomeExpense(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.getIncomeExpenseReport(SecurityContextUtils.currentCompanyId(), from, to);
    }

    @GetMapping("/taxes")
    public TaxReport taxes(@RequestParam String periodKey) {
        return reportService.getTaxReport(SecurityContextUtils.currentCompanyId(), periodKey);
    }

    @GetMapping("/client-summary")
    public List<PartySummary> clientSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.getClientSummary(SecurityContextUtils.currentCompanyId(), from, to);
    }

    @GetMapping("/supplier-summary")
    public List<PartySummary> supplierSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.getSupplierSummary(SecurityContextUtils.currentCompanyId(), from, to);
    }

    @GetMapping("/aging")
    public AgingReport aging(@RequestParam String type) {
        return reportService.getAgingReport(SecurityContextUtils.currentCompanyId(), type);
    }
}
