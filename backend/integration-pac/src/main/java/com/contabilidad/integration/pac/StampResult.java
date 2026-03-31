package com.contabilidad.integration.pac;

import java.time.Instant;
import java.util.UUID;

public record StampResult(
    UUID uuid,
    String satSeal,
    String cfdiSeal,
    String satCertNumber,
    Instant stampDate,
    String stampedXml
) {}
