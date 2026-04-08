package com.contabilidad.invoicing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceDraftRequest(
    @NotNull UUID clientId,
    @NotBlank String invoiceType,
    String series,
    String folio,
    String paymentMethodCode,
    String paymentFormCode,
    String usoCfdiCode,
    String currencyCode,
    @Valid List<InvoiceLineRequest> lines
) {}
