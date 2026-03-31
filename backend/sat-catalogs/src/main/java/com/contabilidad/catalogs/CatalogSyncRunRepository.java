package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CatalogSyncRunRepository extends JpaRepository<CatalogSyncRun, UUID> {
}
