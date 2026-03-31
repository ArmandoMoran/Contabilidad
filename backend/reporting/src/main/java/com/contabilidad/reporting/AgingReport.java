package com.contabilidad.reporting;

import java.math.BigDecimal;
import java.util.List;

public record AgingReport(
    String type,
    BigDecimal totalOutstanding,
    List<AgingBucket> buckets
) {}
