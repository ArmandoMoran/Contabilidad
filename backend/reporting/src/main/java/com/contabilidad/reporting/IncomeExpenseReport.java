package com.contabilidad.reporting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record IncomeExpenseReport(
    LocalDate from,
    LocalDate to,
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal netResult,
    List<CategoryBreakdown> incomeByCategory,
    List<CategoryBreakdown> expenseByCategory
) {
    public record CategoryBreakdown(String category, BigDecimal amount) {}
}
