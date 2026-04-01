package com.contabilidad.declarations;

import com.contabilidad.expenses.Expense;
import com.contabilidad.expenses.ExpenseRepository;
import com.contabilidad.invoicing.Invoice;
import com.contabilidad.invoicing.InvoiceRepository;
import com.contabilidad.payments.PaymentApplication;
import com.contabilidad.payments.PaymentApplicationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeclarationService {

    private final DeclarationRunRepository declarationRunRepository;
    private final DeclarationLineRepository declarationLineRepository;
    private final DiotLineRepository diotLineRepository;
    private final InvoiceRepository invoiceRepository;
    private final ExpenseRepository expenseRepository;
    private final PaymentApplicationRepository paymentApplicationRepository;

    public DeclarationService(
        DeclarationRunRepository declarationRunRepository,
        DeclarationLineRepository declarationLineRepository,
        DiotLineRepository diotLineRepository,
        InvoiceRepository invoiceRepository,
        ExpenseRepository expenseRepository,
        PaymentApplicationRepository paymentApplicationRepository
    ) {
        this.declarationRunRepository = declarationRunRepository;
        this.declarationLineRepository = declarationLineRepository;
        this.diotLineRepository = diotLineRepository;
        this.invoiceRepository = invoiceRepository;
        this.expenseRepository = expenseRepository;
        this.paymentApplicationRepository = paymentApplicationRepository;
    }

    public DeclarationRun generateMonthlyWorkpapers(UUID companyId, GenerateWorkpapersRequest request) {
        String periodKey = String.format("%d-%02d", request.year(), request.month());
        DeclarationRun run = prepareRun(companyId, "MONTHLY", periodKey, request.year(), request.month());

        List<Invoice> invoices = invoiceRepository.findByCompanyId(companyId).stream()
            .filter(this::includeInvoiceInReports)
            .filter(invoice -> periodKey.equals(toPeriodKey(resolveInvoiceDate(invoice))))
            .toList();

        List<Expense> expenses = expenseRepository.findAllByCompanyId(companyId).stream()
            .filter(expense -> periodKey.equals(toPeriodKey(resolveExpenseDate(expense))))
            .toList();

        Map<UUID, BigDecimal> paidByExpenseId = paymentApplicationRepository.findByCompanyId(companyId).stream()
            .filter(application -> "EXPENSE".equalsIgnoreCase(application.getDocumentType()))
            .collect(Collectors.groupingBy(PaymentApplication::getDocumentId, Collectors.reducing(BigDecimal.ZERO, PaymentApplication::getAmountPaid, BigDecimal::add)));

        BigDecimal totalIncome = sum(invoices, Invoice::getTotal);
        BigDecimal totalDeductions = sum(expenses, Expense::getTotal);
        BigDecimal transferredIva = sum(invoices, Invoice::getTransferredTaxTotal);
        BigDecimal creditableIva = sum(expenses, Expense::getTransferredTaxTotal);
        BigDecimal withheldIva = sum(expenses, Expense::getWithheldTaxTotal);

        BigDecimal taxBase = totalIncome.subtract(totalDeductions);
        BigDecimal taxDetermined = transferredIva.subtract(creditableIva);
        BigDecimal netPosition = taxDetermined.subtract(withheldIva);

        run.setTotalIncome(totalIncome);
        run.setTotalDeductions(totalDeductions);
        run.setTaxBase(taxBase.max(BigDecimal.ZERO));
        run.setTaxDetermined(taxDetermined.max(BigDecimal.ZERO));
        run.setTaxWithheld(withheldIva);
        run.setTaxPaidPrevious(BigDecimal.ZERO);
        run.setTaxPayable(netPosition.signum() >= 0 ? netPosition : BigDecimal.ZERO);
        run.setTaxInFavor(netPosition.signum() < 0 ? netPosition.abs() : BigDecimal.ZERO);
        run.setStatus("GENERATED");
        run.setNotes("Papeles mensuales generados con facturas, gastos y aplicaciones de pago del periodo.");
        run.setSnapshot(buildMonthlySnapshot(periodKey, totalIncome, totalDeductions, transferredIva, creditableIva, withheldIva));
        run = declarationRunRepository.save(run);

        declarationLineRepository.deleteByDeclarationRunId(run.getId());
        diotLineRepository.deleteByDeclarationRunId(run.getId());

        persistMonthlyDeclarationLines(run, invoices, expenses);
        persistDiotLines(run, expenses, paidByExpenseId);

        return run;
    }

    public DeclarationRun generateAnnualSummary(UUID companyId, int year) {
        String periodKey = String.valueOf(year);
        DeclarationRun run = prepareRun(companyId, "ANNUAL", periodKey, year, null);

        List<DeclarationRun> monthlyRuns = declarationRunRepository.findByCompanyIdAndFiscalYear(companyId, year).stream()
            .filter(existingRun -> "MONTHLY".equals(existingRun.getDeclarationType()))
            .toList();

        BigDecimal totalIncome;
        BigDecimal totalDeductions;
        BigDecimal taxBase;
        BigDecimal taxDetermined;
        BigDecimal taxWithheld;
        BigDecimal taxPayable;
        BigDecimal taxInFavor;

        if (monthlyRuns.isEmpty()) {
            List<Invoice> invoices = invoiceRepository.findByCompanyId(companyId).stream()
                .filter(this::includeInvoiceInReports)
                .filter(invoice -> toLocalDate(resolveInvoiceDate(invoice)).getYear() == year)
                .toList();
            List<Expense> expenses = expenseRepository.findAllByCompanyId(companyId).stream()
                .filter(expense -> toLocalDate(resolveExpenseDate(expense)).getYear() == year)
                .toList();

            totalIncome = sum(invoices, Invoice::getTotal);
            totalDeductions = sum(expenses, Expense::getTotal);
            taxBase = totalIncome.subtract(totalDeductions).max(BigDecimal.ZERO);
            taxDetermined = sum(invoices, Invoice::getTransferredTaxTotal).subtract(sum(expenses, Expense::getTransferredTaxTotal));
            taxWithheld = sum(expenses, Expense::getWithheldTaxTotal);
            BigDecimal netPosition = taxDetermined.subtract(taxWithheld);
            taxPayable = netPosition.signum() >= 0 ? netPosition : BigDecimal.ZERO;
            taxInFavor = netPosition.signum() < 0 ? netPosition.abs() : BigDecimal.ZERO;
        } else {
            totalIncome = monthlyRuns.stream().map(DeclarationRun::getTotalIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
            totalDeductions = monthlyRuns.stream().map(DeclarationRun::getTotalDeductions).reduce(BigDecimal.ZERO, BigDecimal::add);
            taxBase = monthlyRuns.stream().map(DeclarationRun::getTaxBase).reduce(BigDecimal.ZERO, BigDecimal::add);
            taxDetermined = monthlyRuns.stream().map(DeclarationRun::getTaxDetermined).reduce(BigDecimal.ZERO, BigDecimal::add);
            taxWithheld = monthlyRuns.stream().map(DeclarationRun::getTaxWithheld).reduce(BigDecimal.ZERO, BigDecimal::add);
            taxPayable = monthlyRuns.stream().map(DeclarationRun::getTaxPayable).reduce(BigDecimal.ZERO, BigDecimal::add);
            taxInFavor = monthlyRuns.stream().map(DeclarationRun::getTaxInFavor).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        run.setTotalIncome(totalIncome);
        run.setTotalDeductions(totalDeductions);
        run.setTaxBase(taxBase);
        run.setTaxDetermined(taxDetermined.max(BigDecimal.ZERO));
        run.setTaxWithheld(taxWithheld);
        run.setTaxPaidPrevious(BigDecimal.ZERO);
        run.setTaxPayable(taxPayable);
        run.setTaxInFavor(taxInFavor);
        run.setStatus("GENERATED");
        run.setNotes("Resumen anual consolidado.");
        run.setSnapshot(String.format("{\"periodKey\":\"%s\",\"monthlyRuns\":%d}", periodKey, monthlyRuns.size()));
        run = declarationRunRepository.save(run);
        DeclarationRun savedRun = run;

        declarationLineRepository.deleteByDeclarationRunId(savedRun.getId());
        diotLineRepository.deleteByDeclarationRunId(savedRun.getId());

        monthlyRuns.forEach(monthlyRun -> {
            DeclarationLine line = new DeclarationLine();
            line.setDeclarationRunId(savedRun.getId());
            line.setCompanyId(companyId);
            line.setLineType("MONTHLY_SUMMARY");
            line.setSourceType("MONTHLY_RUN");
            line.setSourceId(monthlyRun.getId());
            line.setConcept("Resumen mensual " + monthlyRun.getPeriodKey());
            line.setBaseAmount(monthlyRun.getTaxBase());
            line.setTaxAmount(monthlyRun.getTaxPayable());
            line.setPeriodKey(periodKey);
            line.setNotes("Ingreso " + monthlyRun.getTotalIncome() + ", deducciones " + monthlyRun.getTotalDeductions());
            declarationLineRepository.save(line);
        });

        return savedRun;
    }

    @Transactional(readOnly = true)
    public DeclarationRun getDeclaration(UUID companyId, UUID id) {
        DeclarationRun run = declarationRunRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Declaration not found: " + id));
        if (!run.getCompanyId().equals(companyId)) {
            throw new EntityNotFoundException("Declaration not found: " + id);
        }
        return run;
    }

    @Transactional(readOnly = true)
    public List<DeclarationRun> listDeclarations(UUID companyId, int fiscalYear) {
        return declarationRunRepository.findByCompanyIdAndFiscalYear(companyId, fiscalYear);
    }

    private DeclarationRun prepareRun(UUID companyId, String declarationType, String periodKey, int fiscalYear, Integer fiscalMonth) {
        DeclarationRun run = declarationRunRepository.findByCompanyIdAndDeclarationTypeAndPeriodKey(companyId, declarationType, periodKey)
            .orElseGet(DeclarationRun::new);
        run.setCompanyId(companyId);
        run.setDeclarationType(declarationType);
        run.setPeriodKey(periodKey);
        run.setFiscalYear(fiscalYear);
        run.setFiscalMonth(fiscalMonth);
        return run;
    }

    private void persistMonthlyDeclarationLines(DeclarationRun run, List<Invoice> invoices, List<Expense> expenses) {
        for (Invoice invoice : invoices) {
            DeclarationLine incomeLine = new DeclarationLine();
            incomeLine.setDeclarationRunId(run.getId());
            incomeLine.setCompanyId(run.getCompanyId());
            incomeLine.setLineType("INCOME");
            incomeLine.setSourceType("INVOICE");
            incomeLine.setSourceId(invoice.getId());
            incomeLine.setConcept(invoice.getReceiverName());
            incomeLine.setBaseAmount(invoice.getSubtotal());
            incomeLine.setTaxAmount(invoice.getTransferredTaxTotal());
            incomeLine.setRate(resolveRate(invoice.getSubtotal(), invoice.getTransferredTaxTotal()));
            incomeLine.setPeriodKey(run.getPeriodKey());
            incomeLine.setNotes(invoice.getSeries() + "-" + invoice.getFolio());
            declarationLineRepository.save(incomeLine);
        }

        for (Expense expense : expenses) {
            DeclarationLine expenseLine = new DeclarationLine();
            expenseLine.setDeclarationRunId(run.getId());
            expenseLine.setCompanyId(run.getCompanyId());
            expenseLine.setLineType("EXPENSE");
            expenseLine.setSourceType("EXPENSE");
            expenseLine.setSourceId(expense.getId());
            expenseLine.setConcept(expense.getIssuerName() != null ? expense.getIssuerName() : "Gasto manual");
            expenseLine.setBaseAmount(expense.getSubtotal());
            expenseLine.setTaxAmount(expense.getTransferredTaxTotal().subtract(expense.getWithheldTaxTotal()));
            expenseLine.setRate(resolveRate(expense.getSubtotal(), expense.getTransferredTaxTotal()));
            expenseLine.setPeriodKey(run.getPeriodKey());
            expenseLine.setNotes(expense.getCategory());
            declarationLineRepository.save(expenseLine);
        }
    }

    private void persistDiotLines(DeclarationRun run, List<Expense> expenses, Map<UUID, BigDecimal> paidByExpenseId) {
        Map<String, List<Expense>> expensesBySupplierKey = expenses.stream()
            .collect(Collectors.groupingBy(expense -> {
                if (expense.getSupplierId() != null) {
                    return expense.getSupplierId().toString();
                }
                return (expense.getIssuerRfc() == null ? "UNKNOWN" : expense.getIssuerRfc()) + "|" + (expense.getIssuerName() == null ? "SIN_NOMBRE" : expense.getIssuerName());
            }, LinkedHashMap::new, Collectors.toList()));

        for (List<Expense> supplierExpenses : expensesBySupplierKey.values()) {
            Expense reference = supplierExpenses.getFirst();
            BigDecimal ivaAccrued = sum(supplierExpenses, Expense::getTransferredTaxTotal);
            BigDecimal ivaPaid = supplierExpenses.stream()
                .map(expense -> calculatePaidTaxPortion(expense, paidByExpenseId.getOrDefault(expense.getId(), BigDecimal.ZERO)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            DiotLine line = new DiotLine();
            line.setDeclarationRunId(run.getId());
            line.setCompanyId(run.getCompanyId());
            line.setSupplierId(reference.getSupplierId());
            line.setSupplierRfc(reference.getIssuerRfc());
            line.setSupplierName(reference.getIssuerName());
            line.setNationality("NATIONAL");
            line.setThirdPartyType("04");
            line.setIva16Accrued(ivaAccrued);
            line.setIva16Paid(ivaPaid);
            line.setIva8Accrued(BigDecimal.ZERO);
            line.setIva8Paid(BigDecimal.ZERO);
            line.setIva0(BigDecimal.ZERO);
            line.setIvaExempt(BigDecimal.ZERO);
            line.setIvaWithheld(sum(supplierExpenses, Expense::getWithheldTaxTotal));
            line.setIsrWithheld(BigDecimal.ZERO);
            line.setPeriodKey(run.getPeriodKey());
            diotLineRepository.save(line);
        }
    }

    private BigDecimal calculatePaidTaxPortion(Expense expense, BigDecimal paidAmount) {
        if (expense.getTotal().signum() <= 0 || paidAmount.signum() <= 0 || expense.getTransferredTaxTotal().signum() <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal ratio = paidAmount.min(expense.getTotal()).divide(expense.getTotal(), 6, RoundingMode.HALF_UP);
        return expense.getTransferredTaxTotal().multiply(ratio).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveRate(BigDecimal base, BigDecimal tax) {
        if (base == null || base.signum() == 0 || tax == null) {
            return null;
        }
        return tax.divide(base, 6, RoundingMode.HALF_UP);
    }

    private boolean includeInvoiceInReports(Invoice invoice) {
        return invoice.getStatus() != null && !invoice.getStatus().startsWith("CANCEL");
    }

    private String buildMonthlySnapshot(
        String periodKey,
        BigDecimal totalIncome,
        BigDecimal totalDeductions,
        BigDecimal transferredIva,
        BigDecimal creditableIva,
        BigDecimal withheldIva
    ) {
        return String.format(
            "{\"periodKey\":\"%s\",\"totalIncome\":%s,\"totalDeductions\":%s,\"transferredIva\":%s,\"creditableIva\":%s,\"withheldIva\":%s}",
            periodKey,
            totalIncome,
            totalDeductions,
            transferredIva,
            creditableIva,
            withheldIva
        );
    }

    private String toPeriodKey(Instant timestamp) {
        LocalDate date = toLocalDate(timestamp);
        return String.format("%d-%02d", date.getYear(), date.getMonthValue());
    }

    private LocalDate toLocalDate(Instant timestamp) {
        return timestamp.atZone(ZoneOffset.UTC).toLocalDate();
    }

    private Instant resolveInvoiceDate(Invoice invoice) {
        return invoice.getIssuedAt() != null ? invoice.getIssuedAt() : invoice.getCreatedAt();
    }

    private Instant resolveExpenseDate(Expense expense) {
        return expense.getIssuedAt() != null ? expense.getIssuedAt() : expense.getCreatedAt();
    }

    private <T> BigDecimal sum(List<T> records, Function<T, BigDecimal> extractor) {
        return records.stream().map(extractor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
