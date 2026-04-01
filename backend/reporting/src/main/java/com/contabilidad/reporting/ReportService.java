package com.contabilidad.reporting;

import com.contabilidad.expenses.Expense;
import com.contabilidad.expenses.ExpenseRepository;
import com.contabilidad.invoicing.Invoice;
import com.contabilidad.invoicing.InvoiceRepository;
import com.contabilidad.parties.Client;
import com.contabilidad.parties.ClientRepository;
import com.contabilidad.parties.Supplier;
import com.contabilidad.parties.SupplierRepository;
import com.contabilidad.payments.PaymentApplication;
import com.contabilidad.payments.PaymentApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final InvoiceRepository invoiceRepository;
    private final ExpenseRepository expenseRepository;
    private final PaymentApplicationRepository paymentApplicationRepository;
    private final ClientRepository clientRepository;
    private final SupplierRepository supplierRepository;

    public ReportService(
        InvoiceRepository invoiceRepository,
        ExpenseRepository expenseRepository,
        PaymentApplicationRepository paymentApplicationRepository,
        ClientRepository clientRepository,
        SupplierRepository supplierRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.expenseRepository = expenseRepository;
        this.paymentApplicationRepository = paymentApplicationRepository;
        this.clientRepository = clientRepository;
        this.supplierRepository = supplierRepository;
    }

    public IncomeExpenseReport getIncomeExpenseReport(UUID companyId, LocalDate from, LocalDate to) {
        List<Invoice> invoices = invoiceRepository.findByCompanyId(companyId).stream()
            .filter(this::includeInvoiceInReports)
            .filter(invoice -> isWithinRange(resolveInvoiceDate(invoice), from, to))
            .toList();

        List<Expense> expenses = expenseRepository.findAllByCompanyId(companyId).stream()
            .filter(expense -> isWithinRange(resolveExpenseDate(expense), from, to))
            .toList();

        BigDecimal totalIncome = sumInvoices(invoices, Invoice::getTotal);
        BigDecimal totalExpenses = sumExpenses(expenses, Expense::getTotal);

        return new IncomeExpenseReport(
            from,
            to,
            totalIncome,
            totalExpenses,
            totalIncome.subtract(totalExpenses),
            buildIncomeBreakdown(invoices),
            buildExpenseBreakdown(expenses)
        );
    }

    public TaxReport getTaxReport(UUID companyId, String periodKey) {
        List<Invoice> invoices = invoiceRepository.findByCompanyId(companyId).stream()
            .filter(this::includeInvoiceInReports)
            .filter(invoice -> periodKey.equals(toPeriodKey(resolveInvoiceDate(invoice))))
            .toList();

        List<Expense> expenses = expenseRepository.findAllByCompanyId(companyId).stream()
            .filter(expense -> periodKey.equals(toPeriodKey(resolveExpenseDate(expense))))
            .toList();

        BigDecimal transferredIva = sumInvoices(invoices, Invoice::getTransferredTaxTotal);
        BigDecimal creditableIva = sumExpenses(expenses, Expense::getTransferredTaxTotal);
        BigDecimal withheldIva = sumExpenses(expenses, Expense::getWithheldTaxTotal);
        BigDecimal withheldIsr = BigDecimal.ZERO;
        BigDecimal netIvaPayable = transferredIva.subtract(creditableIva).subtract(withheldIva);

        List<TaxReport.TaxLineItem> lines = new ArrayList<>();
        if (transferredIva.signum() != 0) {
            lines.add(new TaxReport.TaxLineItem("IVA_TRASLADADO", "Tasa", new BigDecimal("0.160000"), sumInvoices(invoices, Invoice::getSubtotal), transferredIva, true, false));
        }
        if (creditableIva.signum() != 0) {
            lines.add(new TaxReport.TaxLineItem("IVA_ACREDITABLE", "Tasa", new BigDecimal("0.160000"), sumExpenses(expenses, Expense::getSubtotal), creditableIva.negate(), true, false));
        }
        if (withheldIva.signum() != 0) {
            lines.add(new TaxReport.TaxLineItem("IVA_RETENIDO", "Tasa", BigDecimal.ZERO, sumExpenses(expenses, Expense::getSubtotal), withheldIva, false, true));
        }

        return new TaxReport(
            periodKey,
            transferredIva,
            withheldIva,
            withheldIsr,
            netIvaPayable,
            lines
        );
    }

    public List<PartySummary> getClientSummary(UUID companyId, LocalDate from, LocalDate to) {
        Map<UUID, Client> clientsById = clientRepository.findAllByCompanyIdAndDeletedAtIsNull(companyId).stream()
            .collect(Collectors.toMap(Client::getId, Function.identity()));

        Map<UUID, BigDecimal> paidByInvoiceId = paymentApplicationRepository.findByCompanyId(companyId).stream()
            .filter(application -> "INVOICE".equalsIgnoreCase(application.getDocumentType()))
            .collect(Collectors.groupingBy(PaymentApplication::getDocumentId, Collectors.reducing(BigDecimal.ZERO, PaymentApplication::getAmountPaid, BigDecimal::add)));

        return invoiceRepository.findByCompanyId(companyId).stream()
            .filter(this::includeInvoiceInReports)
            .filter(invoice -> invoice.getClientId() != null)
            .filter(invoice -> isWithinRange(resolveInvoiceDate(invoice), from, to))
            .collect(Collectors.groupingBy(Invoice::getClientId))
            .entrySet().stream()
            .map(entry -> {
                Client client = clientsById.get(entry.getKey());
                List<Invoice> invoices = entry.getValue();
                BigDecimal totalAmount = sumInvoices(invoices, Invoice::getTotal);
                BigDecimal totalTax = sumInvoices(invoices, invoice -> invoice.getTransferredTaxTotal().subtract(invoice.getWithheldTaxTotal()));
                BigDecimal totalPaid = invoices.stream().map(invoice -> paidByInvoiceId.getOrDefault(invoice.getId(), BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                return new PartySummary(
                    entry.getKey(),
                    client != null ? client.getRfc() : null,
                    client != null ? client.getLegalName() : "Cliente sin catalogo",
                    invoices.size(),
                    totalAmount,
                    totalTax,
                    totalPaid,
                    totalAmount.subtract(totalPaid).max(BigDecimal.ZERO)
                );
            })
            .sorted(Comparator.comparing(PartySummary::legalName, Comparator.nullsLast(String::compareToIgnoreCase)))
            .toList();
    }

    public List<PartySummary> getSupplierSummary(UUID companyId, LocalDate from, LocalDate to) {
        Map<UUID, Supplier> suppliersById = supplierRepository.findAllByCompanyIdAndDeletedAtIsNull(companyId).stream()
            .collect(Collectors.toMap(Supplier::getId, Function.identity()));

        Map<UUID, BigDecimal> paidByExpenseId = paymentApplicationRepository.findByCompanyId(companyId).stream()
            .filter(application -> "EXPENSE".equalsIgnoreCase(application.getDocumentType()))
            .collect(Collectors.groupingBy(PaymentApplication::getDocumentId, Collectors.reducing(BigDecimal.ZERO, PaymentApplication::getAmountPaid, BigDecimal::add)));

        return expenseRepository.findAllByCompanyId(companyId).stream()
            .filter(expense -> expense.getSupplierId() != null)
            .filter(expense -> isWithinRange(resolveExpenseDate(expense), from, to))
            .collect(Collectors.groupingBy(Expense::getSupplierId))
            .entrySet().stream()
            .map(entry -> {
                Supplier supplier = suppliersById.get(entry.getKey());
                List<Expense> expenses = entry.getValue();
                BigDecimal totalAmount = sumExpenses(expenses, Expense::getTotal);
                BigDecimal totalTax = sumExpenses(expenses, expense -> expense.getTransferredTaxTotal().subtract(expense.getWithheldTaxTotal()));
                BigDecimal totalPaid = expenses.stream().map(expense -> paidByExpenseId.getOrDefault(expense.getId(), BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                return new PartySummary(
                    entry.getKey(),
                    supplier != null ? supplier.getRfc() : null,
                    supplier != null ? supplier.getLegalName() : "Proveedor sin catalogo",
                    expenses.size(),
                    totalAmount,
                    totalTax,
                    totalPaid,
                    totalAmount.subtract(totalPaid).max(BigDecimal.ZERO)
                );
            })
            .sorted(Comparator.comparing(PartySummary::legalName, Comparator.nullsLast(String::compareToIgnoreCase)))
            .toList();
    }

    public AgingReport getAgingReport(UUID companyId, String type) {
        Map<UUID, BigDecimal> paidByDocumentId = paymentApplicationRepository.findByCompanyId(companyId).stream()
            .filter(application -> matchesAgingDocumentType(type, application.getDocumentType()))
            .collect(Collectors.groupingBy(PaymentApplication::getDocumentId, Collectors.reducing(BigDecimal.ZERO, PaymentApplication::getAmountPaid, BigDecimal::add)));

        List<AgingItem> items = "PAYABLES".equals(normalizeType(type))
            ? expenseRepository.findAllByCompanyId(companyId).stream()
                .map(expense -> toAgingItem(expense.getId(), resolveExpenseDate(expense), expense.getTotal(), paidByDocumentId.getOrDefault(expense.getId(), BigDecimal.ZERO)))
                .filter(item -> item.amount().signum() > 0)
                .toList()
            : invoiceRepository.findByCompanyId(companyId).stream()
                .filter(this::includeInvoiceInReports)
                .map(invoice -> toAgingItem(invoice.getId(), resolveInvoiceDate(invoice), invoice.getTotal(), paidByDocumentId.getOrDefault(invoice.getId(), BigDecimal.ZERO)))
                .filter(item -> item.amount().signum() > 0)
                .toList();

        List<AgingBucket> buckets = List.of(
            buildBucket("0-30 dias", 0, 30, items),
            buildBucket("31-60 dias", 31, 60, items),
            buildBucket("61-90 dias", 61, 90, items),
            buildBucket("91+ dias", 91, Integer.MAX_VALUE, items)
        );

        BigDecimal totalOutstanding = items.stream()
            .map(AgingItem::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AgingReport(normalizeType(type), totalOutstanding, buckets);
    }

    private boolean includeInvoiceInReports(Invoice invoice) {
        return invoice.getStatus() != null && !invoice.getStatus().startsWith("CANCEL");
    }

    private List<IncomeExpenseReport.CategoryBreakdown> buildIncomeBreakdown(List<Invoice> invoices) {
        return buildBreakdown(invoices, invoice -> switch (invoice.getInvoiceType()) {
            case "E" -> "Notas de credito";
            case "P" -> "Complementos de pago";
            default -> "Facturacion";
        }, Invoice::getTotal);
    }

    private List<IncomeExpenseReport.CategoryBreakdown> buildExpenseBreakdown(List<Expense> expenses) {
        return buildBreakdown(expenses, expense -> expense.getCategory() == null || expense.getCategory().isBlank() ? "SIN_CATEGORIA" : expense.getCategory(), Expense::getTotal);
    }

    private <T> List<IncomeExpenseReport.CategoryBreakdown> buildBreakdown(List<T> records, Function<T, String> categoryExtractor, Function<T, BigDecimal> amountExtractor) {
        Map<String, BigDecimal> byCategory = new LinkedHashMap<>();
        records.forEach(record -> byCategory.merge(categoryExtractor.apply(record), amountExtractor.apply(record), BigDecimal::add));
        return byCategory.entrySet().stream()
            .map(entry -> new IncomeExpenseReport.CategoryBreakdown(entry.getKey(), entry.getValue()))
            .toList();
    }

    private AgingBucket buildBucket(String label, int minDays, int maxDays, List<AgingItem> items) {
        List<AgingItem> matchingItems = items.stream()
            .filter(item -> item.days() >= minDays && item.days() <= maxDays)
            .toList();
        BigDecimal amount = matchingItems.stream().map(AgingItem::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AgingBucket(label, minDays, maxDays == Integer.MAX_VALUE ? 9999 : maxDays, matchingItems.size(), amount);
    }

    private AgingItem toAgingItem(UUID documentId, Instant documentDate, BigDecimal total, BigDecimal paid) {
        BigDecimal outstanding = total.subtract(paid).max(BigDecimal.ZERO);
        long days = Math.max(0, ChronoUnit.DAYS.between(toLocalDate(documentDate), LocalDate.now(ZoneOffset.UTC)));
        return new AgingItem(documentId, (int) days, outstanding);
    }

    private boolean matchesAgingDocumentType(String requestedType, String documentType) {
        String normalizedType = normalizeType(requestedType);
        return ("PAYABLES".equals(normalizedType) && "EXPENSE".equalsIgnoreCase(documentType))
            || ("RECEIVABLES".equals(normalizedType) && "INVOICE".equalsIgnoreCase(documentType));
    }

    private String normalizeType(String type) {
        if (type == null) {
            return "RECEIVABLES";
        }
        String normalized = type.trim().toUpperCase();
        if (normalized.equals("PAYABLES") || normalized.equals("AP") || normalized.equals("SUPPLIERS")) {
            return "PAYABLES";
        }
        return "RECEIVABLES";
    }

    private boolean isWithinRange(Instant timestamp, LocalDate from, LocalDate to) {
        LocalDate localDate = toLocalDate(timestamp);
        return !localDate.isBefore(from) && !localDate.isAfter(to);
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

    private BigDecimal sumInvoices(List<Invoice> invoices, Function<Invoice, BigDecimal> extractor) {
        return invoices.stream().map(extractor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumExpenses(List<Expense> expenses, Function<Expense, BigDecimal> extractor) {
        return expenses.stream().map(extractor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private record AgingItem(UUID documentId, int days, BigDecimal amount) {}
}
