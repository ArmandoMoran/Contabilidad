package com.contabilidad.integration.sat;

import java.math.BigDecimal;
import java.util.UUID;

public interface SatValidationClient {

    SatValidationResult validateCfdi(UUID cfdiUuid, String issuerRfc, String receiverRfc, BigDecimal total);
}
