package com.contabilidad.reporting;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/income-expense")
    public IncomeExpenseReport incomeExpense(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.getIncomeExpenseReport(companyId, from, to);
    }

    @GetMapping("/taxes")
    public TaxReport taxes(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam String periodKey) {
        return reportService.getTaxReport(companyId, periodKey);
    }

    @GetMapping("/client-summary")
    public List<PartySummary> clientSummary(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.getClientSummary(companyId, from, to);
    }

    @GetMapping("/supplier-summary")
    public List<PartySummary> supplierSummary(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.getSupplierSummary(companyId, from, to);
    }

    @GetMapping("/aging")
    public AgingReport aging(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam String type) {
        return reportService.getAgingReport(companyId, type);
    }
}
