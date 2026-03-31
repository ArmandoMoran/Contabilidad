package com.contabilidad.expenses;

public record ImportXmlRequest(
    String xmlObjectKey,
    String pdfObjectKey
) {}
