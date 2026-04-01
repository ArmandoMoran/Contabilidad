package com.contabilidad.expenses;

import com.contabilidad.shared.PageResponse;
import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseDto create(@Valid @RequestBody CreateExpenseRequest request) {
        Expense expense = expenseService.create(SecurityContextUtils.currentCompanyId(), request);
        return ExpenseMapper.toDto(expense);
    }

    @PostMapping("/import-xml")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseDto importXml(@Valid @RequestBody ImportXmlRequest request) {
        Expense expense = expenseService.importXml(SecurityContextUtils.currentCompanyId(), request);
        return ExpenseMapper.toDto(expense);
    }

    @GetMapping("/{id}")
    public ExpenseDto get(@PathVariable java.util.UUID id) {
        Expense expense = expenseService.getExpense(SecurityContextUtils.currentCompanyId(), id);
        return ExpenseMapper.toDto(expense);
    }

    @GetMapping
    public PageResponse<ExpenseDto> list(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        var companyId = SecurityContextUtils.currentCompanyId();
        Page<Expense> page;
        if (status != null && !status.isBlank()) {
            page = expenseService.listExpensesByStatus(companyId, status, pageable);
        } else {
            page = expenseService.listExpenses(companyId, pageable);
        }
        return PageResponse.of(
            page.getContent().stream().map(ExpenseMapper::toDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }
}
