package com.contabilidad.declarations;

import com.contabilidad.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "declaration_runs")
public class DeclarationRun extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "declaration_type", nullable = false, length = 20)
    private String declarationType;

    @Column(name = "period_key", nullable = false, length = 7)
    private String periodKey;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    @Column(name = "fiscal_month")
    private Integer fiscalMonth;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "DRAFT";

    @Column(name = "total_income", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalIncome = BigDecimal.ZERO;

    @Column(name = "total_deductions", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Column(name = "tax_base", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxBase = BigDecimal.ZERO;

    @Column(name = "tax_determined", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxDetermined = BigDecimal.ZERO;

    @Column(name = "tax_withheld", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxWithheld = BigDecimal.ZERO;

    @Column(name = "tax_paid_previous", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxPaidPrevious = BigDecimal.ZERO;

    @Column(name = "tax_payable", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxPayable = BigDecimal.ZERO;

    @Column(name = "tax_in_favor", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxInFavor = BigDecimal.ZERO;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot", columnDefinition = "jsonb", nullable = false)
    private String snapshot = "{}";

    @Column(name = "frozen_at")
    private Instant frozenAt;

    @Column(name = "frozen_by")
    private UUID frozenBy;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getTemplateId() { return templateId; }
    public void setTemplateId(UUID templateId) { this.templateId = templateId; }
    public String getDeclarationType() { return declarationType; }
    public void setDeclarationType(String declarationType) { this.declarationType = declarationType; }
    public String getPeriodKey() { return periodKey; }
    public void setPeriodKey(String periodKey) { this.periodKey = periodKey; }
    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }
    public Integer getFiscalMonth() { return fiscalMonth; }
    public void setFiscalMonth(Integer fiscalMonth) { this.fiscalMonth = fiscalMonth; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }
    public BigDecimal getTaxBase() { return taxBase; }
    public void setTaxBase(BigDecimal taxBase) { this.taxBase = taxBase; }
    public BigDecimal getTaxDetermined() { return taxDetermined; }
    public void setTaxDetermined(BigDecimal taxDetermined) { this.taxDetermined = taxDetermined; }
    public BigDecimal getTaxWithheld() { return taxWithheld; }
    public void setTaxWithheld(BigDecimal taxWithheld) { this.taxWithheld = taxWithheld; }
    public BigDecimal getTaxPaidPrevious() { return taxPaidPrevious; }
    public void setTaxPaidPrevious(BigDecimal taxPaidPrevious) { this.taxPaidPrevious = taxPaidPrevious; }
    public BigDecimal getTaxPayable() { return taxPayable; }
    public void setTaxPayable(BigDecimal taxPayable) { this.taxPayable = taxPayable; }
    public BigDecimal getTaxInFavor() { return taxInFavor; }
    public void setTaxInFavor(BigDecimal taxInFavor) { this.taxInFavor = taxInFavor; }
    public String getSnapshot() { return snapshot; }
    public void setSnapshot(String snapshot) { this.snapshot = snapshot; }
    public Instant getFrozenAt() { return frozenAt; }
    public void setFrozenAt(Instant frozenAt) { this.frozenAt = frozenAt; }
    public UUID getFrozenBy() { return frozenBy; }
    public void setFrozenBy(UUID frozenBy) { this.frozenBy = frozenBy; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
