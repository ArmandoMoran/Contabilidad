package com.contabilidad.payments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreatePaymentRequest(
    @NotBlank String paymentDirection,
    @NotBlank String paymentFormCode,
    String currencyCode,
    @NotNull BigDecimal amount,
    @NotNull Instant paidAt,
    String operationNumber,
    String payerRfc,
    String payerName,
    String payerBankRfc,
    String payerAccount,
    String payeeRfc,
    String payeeName,
    String payeeBankRfc,
    String payeeAccount,
    String notes
) {}
