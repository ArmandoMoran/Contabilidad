package com.contabilidad.expenses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateExpenseRequest(
    UUID supplierId,
    String expenseType,
    @NotBlank String description,
    @NotNull BigDecimal total,
    String currencyCode,
    String category,
    Boolean deductible,
    String accountingAccount,
    String notes
) {}
