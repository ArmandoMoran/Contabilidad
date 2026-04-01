package com.contabilidad.declarations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "declaration_lines")
public class DeclarationLine {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "declaration_run_id", nullable = false)
    private UUID declarationRunId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "line_type", nullable = false, length = 40)
    private String lineType;

    @Column(name = "source_type", length = 20)
    private String sourceType;

    @Column(name = "source_id")
    private UUID sourceId;

    @Column(name = "source_line_id")
    private UUID sourceLineId;

    @Column(name = "concept", nullable = false, length = 200)
    private String concept;

    @Column(name = "base_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal baseAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "rate", precision = 12, scale = 6)
    private BigDecimal rate;

    @Column(name = "period_key", nullable = false, length = 7)
    private String periodKey;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DeclarationLine() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDeclarationRunId() { return declarationRunId; }
    public void setDeclarationRunId(UUID declarationRunId) { this.declarationRunId = declarationRunId; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public String getLineType() { return lineType; }
    public void setLineType(String lineType) { this.lineType = lineType; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public UUID getSourceId() { return sourceId; }
    public void setSourceId(UUID sourceId) { this.sourceId = sourceId; }
    public UUID getSourceLineId() { return sourceLineId; }
    public void setSourceLineId(UUID sourceLineId) { this.sourceLineId = sourceLineId; }
    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public String getPeriodKey() { return periodKey; }
    public void setPeriodKey(String periodKey) { this.periodKey = periodKey; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
}
