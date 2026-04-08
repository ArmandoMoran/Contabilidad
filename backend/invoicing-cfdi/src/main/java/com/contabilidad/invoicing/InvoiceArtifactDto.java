package com.contabilidad.invoicing;

public record InvoiceArtifactDto(
    String type,
    String fileName,
    String contentType,
    String downloadUrl
) {}
