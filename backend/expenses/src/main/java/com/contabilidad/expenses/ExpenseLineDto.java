package com.contabilidad.expenses;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseLineDto(
    UUID id,
    UUID expenseId,
    int lineNumber,
    String satProductCode,
    String description,
    String satUnitCode,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal discount,
    BigDecimal subtotal,
    String objetoImpCode
) {}
