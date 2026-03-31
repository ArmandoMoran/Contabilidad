package com.contabilidad.shared;

import java.net.URI;
import java.time.Instant;
import java.util.List;

public record ProblemDetail(
    URI type,
    String title,
    int status,
    String detail,
    String code,
    String traceId,
    Instant timestamp,
    List<FieldError> fieldErrors
) {
    public record FieldError(String field, String message, Object rejectedValue) {}

    public static ProblemDetail of(int status, String code, String detail, String traceId) {
        return new ProblemDetail(
            URI.create("about:blank"), code, status, detail, code, traceId, Instant.now(), List.of()
        );
    }

    public static ProblemDetail withFieldErrors(int status, String code, String detail,
                                                 String traceId, List<FieldError> fieldErrors) {
        return new ProblemDetail(
            URI.create("about:blank"), code, status, detail, code, traceId, Instant.now(), fieldErrors
        );
    }
}
