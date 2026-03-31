package com.contabilidad.reporting;

import java.math.BigDecimal;

public record AgingBucket(
    String label,
    int minDays,
    int maxDays,
    int count,
    BigDecimal amount
) {}
