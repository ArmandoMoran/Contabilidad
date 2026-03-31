package com.contabilidad.payments;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentDto(
    UUID id,
    UUID companyId,
    String paymentDirection,
    String status,
    String paymentFormCode,
    String currencyCode,
    BigDecimal exchangeRate,
    BigDecimal amount,
    Instant paidAt,
    String operationNumber,
    String payerRfc,
    String payerName,
    String payerBankRfc,
    String payerAccount,
    String payeeRfc,
    String payeeName,
    String payeeBankRfc,
    String payeeAccount,
    String notes,
    UUID repInvoiceId,
    Instant createdAt,
    Instant updatedAt
) {}
