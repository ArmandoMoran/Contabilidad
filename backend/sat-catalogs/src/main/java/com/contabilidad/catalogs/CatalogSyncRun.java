package com.contabilidad.catalogs;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_sync_runs")
public class CatalogSyncRun {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "catalog_name", nullable = false)
    private String catalogName;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "source_hash")
    private String sourceHash;

    @Column(name = "records_loaded")
    private int recordsLoaded;

    @Column(name = "records_updated")
    private int recordsUpdated;

    @Column(name = "records_deactivated")
    private int recordsDeactivated;

    @Column(nullable = false)
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "triggered_by")
    private UUID triggeredBy;

    public CatalogSyncRun() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCatalogName() { return catalogName; }
    public void setCatalogName(String catalogName) { this.catalogName = catalogName; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getSourceHash() { return sourceHash; }
    public void setSourceHash(String sourceHash) { this.sourceHash = sourceHash; }
    public int getRecordsLoaded() { return recordsLoaded; }
    public void setRecordsLoaded(int recordsLoaded) { this.recordsLoaded = recordsLoaded; }
    public int getRecordsUpdated() { return recordsUpdated; }
    public void setRecordsUpdated(int recordsUpdated) { this.recordsUpdated = recordsUpdated; }
    public int getRecordsDeactivated() { return recordsDeactivated; }
    public void setRecordsDeactivated(int recordsDeactivated) { this.recordsDeactivated = recordsDeactivated; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }
    public UUID getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(UUID triggeredBy) { this.triggeredBy = triggeredBy; }
}
