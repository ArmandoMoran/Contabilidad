package com.contabilidad.catalogs;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sat_regimen_fiscal")
public class SatRegimenFiscal {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String code;

    private String description;

    @Column(name = "applies_pf")
    private boolean appliesPf;

    @Column(name = "applies_pm")
    private boolean appliesPm;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    private boolean active;

    @Column(name = "sat_release_tag")
    private String satReleaseTag;

    @Column(name = "source_hash")
    private String sourceHash;

    @Column(name = "loaded_at")
    private Instant loadedAt;

    public SatRegimenFiscal() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isAppliesPf() { return appliesPf; }
    public void setAppliesPf(boolean appliesPf) { this.appliesPf = appliesPf; }
    public boolean isAppliesPm() { return appliesPm; }
    public void setAppliesPm(boolean appliesPm) { this.appliesPm = appliesPm; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getSatReleaseTag() { return satReleaseTag; }
    public void setSatReleaseTag(String satReleaseTag) { this.satReleaseTag = satReleaseTag; }
    public String getSourceHash() { return sourceHash; }
    public void setSourceHash(String sourceHash) { this.sourceHash = sourceHash; }
    public Instant getLoadedAt() { return loadedAt; }
    public void setLoadedAt(Instant loadedAt) { this.loadedAt = loadedAt; }
}
