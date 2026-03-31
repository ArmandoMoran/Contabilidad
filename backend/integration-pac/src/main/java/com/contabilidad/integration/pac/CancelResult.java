package com.contabilidad.integration.pac;

import java.time.Instant;

public record CancelResult(
    String acuse,
    String status,
    Instant cancelDate
) {}
