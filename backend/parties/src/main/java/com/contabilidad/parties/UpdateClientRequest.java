package com.contabilidad.parties;

public record UpdateClientRequest(
    String rfc,
    String legalName,
    String tradeName,
    String email,
    String phone,
    String website,
    String fiscalRegimeCode,
    String defaultUsoCfdiCode,
    String defaultFormaPagoCode,
    String defaultMetodoPagoCode,
    String defaultPostalCode,
    String notes,
    Boolean active
) {}
