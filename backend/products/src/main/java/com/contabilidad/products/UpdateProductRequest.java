package com.contabilidad.products;

import java.math.BigDecimal;

public record UpdateProductRequest(
    String internalCode,
    String internalName,
    String description,
    String satProductCode,
    String satUnitCode,
    BigDecimal unitPrice,
    String currencyCode,
    String objetoImpCode,
    String cuentaPredial,
    Boolean active
) {}
