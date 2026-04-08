package com.contabilidad.shared;

import java.util.List;

public class BusinessValidationException extends RuntimeException {

    private final List<Violation> violations;

    public BusinessValidationException(List<Violation> violations) {
        super("Business validation failed");
        this.violations = List.copyOf(violations);
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public record Violation(String field, String message, Object rejectedValue) {}
}
