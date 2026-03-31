package com.contabilidad.parties;

public record UpdateSupplierRequest(
    String rfc,
    String legalName,
    String tradeName,
    String email,
    String phone,
    String website,
    String fiscalRegimeCode,
    String defaultFormaPagoCode,
    String nationality,
    String diotOperationType,
    String notes,
    Boolean active
) {}
