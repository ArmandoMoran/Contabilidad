package com.contabilidad.invoicing;

import java.util.List;

public record ValidationResult(
    List<String> errors,
    boolean valid
) {
    public static ValidationResult ok() {
        return new ValidationResult(List.of(), true);
    }

    public static ValidationResult withErrors(List<String> errors) {
        return new ValidationResult(errors, false);
    }
}
