package com.contabilidad.integration.sat;

import java.time.LocalDate;
import java.util.UUID;

public interface SatRecoveryClient {

    void requestMetadataDownload(UUID companyId, String rfc, LocalDate from, LocalDate to, String direction);

    void requestXmlDownload(UUID companyId, String rfc, LocalDate from, LocalDate to, String direction);
}
