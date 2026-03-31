package com.contabilidad.declarations;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GenerateWorkpapersRequest(
    @Min(2020) int year,
    @Min(1) @Max(12) int month
) {}
