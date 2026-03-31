package com.contabilidad.integration.pac;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.pac.mode", havingValue = "simulated", matchIfMissing = true)
public class SimulatedPacClient implements PacClient {

    @Override
    public StampResult stamp(String xmlContent) {
        return new StampResult(
            UUID.randomUUID(),
            "SIMULATED_SAT_SEAL_" + System.currentTimeMillis(),
            "SIMULATED_CFDI_SEAL_" + System.currentTimeMillis(),
            "30001000000500003416",
            Instant.now(),
            xmlContent
        );
    }

    @Override
    public CancelResult cancel(UUID cfdiUuid, String issuerRfc, String reasonCode, UUID replacementUuid) {
        return new CancelResult(
            "<Acuse>" + cfdiUuid + "</Acuse>",
            "Cancelado sin aceptación",
            Instant.now()
        );
    }
}
