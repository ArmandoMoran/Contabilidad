package com.contabilidad.integration.sat;

public record SatValidationResult(
    String status,
    boolean cancellable,
    String cancellationStatus
) {}
