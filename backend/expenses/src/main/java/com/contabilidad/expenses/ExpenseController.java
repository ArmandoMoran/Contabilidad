package com.contabilidad.expenses;

import com.contabilidad.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseDto create(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody CreateExpenseRequest request) {
        Expense expense = expenseService.create(companyId, request);
        return ExpenseMapper.toDto(expense);
    }

    @PostMapping("/import-xml")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseDto importXml(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody ImportXmlRequest request) {
        Expense expense = expenseService.importXml(companyId, request);
        return ExpenseMapper.toDto(expense);
    }

    @GetMapping("/{id}")
    public ExpenseDto get(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        Expense expense = expenseService.getExpense(companyId, id);
        return ExpenseMapper.toDto(expense);
    }

    @GetMapping
    public PageResponse<ExpenseDto> list(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
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
