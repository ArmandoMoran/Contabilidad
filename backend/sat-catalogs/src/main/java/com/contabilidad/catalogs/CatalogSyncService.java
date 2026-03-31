package com.contabilidad.catalogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class CatalogSyncService {

    private static final Logger log = LoggerFactory.getLogger(CatalogSyncService.class);

    private final CatalogSyncRunRepository syncRunRepository;

    public CatalogSyncService(CatalogSyncRunRepository syncRunRepository) {
        this.syncRunRepository = syncRunRepository;
    }

    public CatalogSyncRun syncCatalog(String catalogName, UUID triggeredBy) {
        log.info("Starting sync for catalog: {}", catalogName);

        CatalogSyncRun run = new CatalogSyncRun();
        run.setCatalogName(catalogName);
        run.setTriggeredBy(triggeredBy);
        run.setStatus("RUNNING");
        run.setStartedAt(Instant.now());
        syncRunRepository.save(run);

        try {
            // TODO: implement actual sync logic per catalog
            run.setStatus("COMPLETED");
            log.info("Sync completed for catalog: {}", catalogName);
        } catch (Exception e) {
            run.setStatus("FAILED");
            run.setErrorMessage(e.getMessage());
            log.error("Sync failed for catalog: {}", catalogName, e);
        } finally {
            run.setFinishedAt(Instant.now());
            syncRunRepository.save(run);
        }

        return run;
    }
}
