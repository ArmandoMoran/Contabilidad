package com.contabilidad.integration.sat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.sat.mode", havingValue = "simulated", matchIfMissing = true)
public class SimulatedSatClient implements SatValidationClient {

    @Override
    public SatValidationResult validateCfdi(UUID cfdiUuid, String issuerRfc, String receiverRfc, BigDecimal total) {
        return new SatValidationResult("Vigente", true, "None");
    }
}
