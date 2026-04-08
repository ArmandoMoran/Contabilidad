package com.contabilidad.identity;

import com.contabilidad.shared.BusinessValidationException;
import com.contabilidad.shared.ProblemDetail;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuth(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.of(401, "AUTHENTICATION_FAILED", ex.getMessage(),
                        UUID.randomUUID().toString()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.of(404, "NOT_FOUND", ex.getMessage(),
                        UUID.randomUUID().toString()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ProblemDetail.FieldError(e.getField(), e.getDefaultMessage(),
                        e.getRejectedValue()))
                .toList();
        log.warn("Validation error: {} field errors", fieldErrors.size());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ProblemDetail.withFieldErrors(422, "VALIDATION_ERROR", "Error de validación",
                        UUID.randomUUID().toString(), fieldErrors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArg(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.of(400, "BAD_REQUEST", ex.getMessage(),
                        UUID.randomUUID().toString()));
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessValidation(BusinessValidationException ex) {
        var fieldErrors = ex.getViolations().stream()
                .map(v -> new ProblemDetail.FieldError(v.field(), v.message(), v.rejectedValue()))
                .toList();
        log.warn("Business validation error: {} field errors", fieldErrors.size());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ProblemDetail.withFieldErrors(422, "BUSINESS_VALIDATION_ERROR", "Error de validación",
                        UUID.randomUUID().toString(), fieldErrors));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalState(IllegalStateException ex) {
        log.warn("Conflict/illegal state: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetail.of(409, "CONFLICT", ex.getMessage(),
                        UUID.randomUUID().toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.of(500, "INTERNAL_ERROR", "Error interno del servidor",
                        UUID.randomUUID().toString()));
    }
}
