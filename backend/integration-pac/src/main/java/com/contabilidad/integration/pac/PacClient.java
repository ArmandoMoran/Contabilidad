package com.contabilidad.integration.pac;

import java.util.UUID;

public interface PacClient {

    StampResult stamp(String xmlContent);

    CancelResult cancel(UUID cfdiUuid, String issuerRfc, String reasonCode, UUID replacementUuid);
}
