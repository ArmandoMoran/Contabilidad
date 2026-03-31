-- ============================================================
-- V9: Declarations — working papers, runs, lines
-- ============================================================

CREATE TABLE declaration_runs (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id          uuid         NOT NULL REFERENCES companies(id),
    template_id         uuid         REFERENCES declaration_templates(id),
    declaration_type    varchar(20)  NOT NULL,
    period_key          varchar(7)   NOT NULL,
    fiscal_year         int          NOT NULL,
    fiscal_month        int,
    status              varchar(30)  NOT NULL DEFAULT 'DRAFT',
    -- Computed totals
    total_income        numeric(18,2) NOT NULL DEFAULT 0,
    total_deductions    numeric(18,2) NOT NULL DEFAULT 0,
    tax_base            numeric(18,2) NOT NULL DEFAULT 0,
    tax_determined      numeric(18,2) NOT NULL DEFAULT 0,
    tax_withheld        numeric(18,2) NOT NULL DEFAULT 0,
    tax_paid_previous   numeric(18,2) NOT NULL DEFAULT 0,
    tax_payable         numeric(18,2) NOT NULL DEFAULT 0,
    tax_in_favor        numeric(18,2) NOT NULL DEFAULT 0,
    -- Snapshot
    snapshot            jsonb        NOT NULL DEFAULT '{}'::jsonb,
    frozen_at           timestamptz,
    frozen_by           uuid,
    notes               text,
    created_at          timestamptz  NOT NULL DEFAULT now(),
    created_by          uuid,
    updated_at          timestamptz  NOT NULL DEFAULT now(),
    updated_by          uuid,
    row_version         bigint       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX idx_declaration_runs_period ON declaration_runs(company_id, declaration_type, period_key);
CREATE INDEX idx_declaration_runs_year        ON declaration_runs(company_id, fiscal_year, declaration_type);

-- ── Declaration Lines (traceability to source documents) ──
CREATE TABLE declaration_lines (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    declaration_run_id  uuid         NOT NULL REFERENCES declaration_runs(id) ON DELETE CASCADE,
    company_id          uuid         NOT NULL REFERENCES companies(id),
    line_type           varchar(40)  NOT NULL,
    source_type         varchar(20),
    source_id           uuid,
    source_line_id      uuid,
    concept             varchar(200) NOT NULL,
    base_amount         numeric(18,2) NOT NULL DEFAULT 0,
    tax_amount          numeric(18,2) NOT NULL DEFAULT 0,
    rate                numeric(12,6),
    period_key          varchar(7)   NOT NULL,
    notes               text,
    created_at          timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_declaration_lines_run ON declaration_lines(declaration_run_id);

-- ── DIOT Lines ──
CREATE TABLE diot_lines (
    id                      uuid PRIMARY KEY DEFAULT uuidv7(),
    declaration_run_id      uuid         NOT NULL REFERENCES declaration_runs(id) ON DELETE CASCADE,
    company_id              uuid         NOT NULL REFERENCES companies(id),
    supplier_id             uuid         REFERENCES suppliers(id),
    supplier_rfc            varchar(13),
    supplier_name           varchar(255),
    nationality             varchar(20)  NOT NULL DEFAULT 'NATIONAL',
    third_party_type        varchar(2)   NOT NULL,
    iva_16_paid             numeric(18,2) NOT NULL DEFAULT 0,
    iva_16_accrued          numeric(18,2) NOT NULL DEFAULT 0,
    iva_8_paid              numeric(18,2) NOT NULL DEFAULT 0,
    iva_8_accrued           numeric(18,2) NOT NULL DEFAULT 0,
    iva_0                   numeric(18,2) NOT NULL DEFAULT 0,
    iva_exempt              numeric(18,2) NOT NULL DEFAULT 0,
    iva_withheld            numeric(18,2) NOT NULL DEFAULT 0,
    isr_withheld            numeric(18,2) NOT NULL DEFAULT 0,
    period_key              varchar(7)   NOT NULL,
    created_at              timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_diot_lines_run ON diot_lines(declaration_run_id);
