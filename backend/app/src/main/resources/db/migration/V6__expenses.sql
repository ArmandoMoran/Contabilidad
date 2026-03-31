-- ============================================================
-- V6: Expenses — CFDI received, manual expenses
-- ============================================================

CREATE TABLE expenses (
    id                      uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id              uuid         NOT NULL REFERENCES companies(id),
    supplier_id             uuid         REFERENCES suppliers(id),
    expense_type            varchar(20)  NOT NULL DEFAULT 'CFDI_RECEIVED',
    status                  varchar(30)  NOT NULL DEFAULT 'PENDING',
    -- Source CFDI data (if from XML)
    cfdi_uuid               uuid,
    cfdi_version            varchar(10),
    issuer_rfc              varchar(13),
    issuer_name             varchar(255),
    receiver_rfc            varchar(13),
    receiver_name           varchar(255),
    invoice_type            varchar(1),
    series                  varchar(25),
    folio                   varchar(40),
    issued_at               timestamptz,
    payment_method_code     varchar(3),
    payment_form_code       varchar(2),
    uso_cfdi_code           varchar(4),
    currency_code           varchar(3)   NOT NULL DEFAULT 'MXN',
    exchange_rate           numeric(18,6) NOT NULL DEFAULT 1,
    -- Totals
    subtotal                numeric(18,2) NOT NULL DEFAULT 0,
    discount                numeric(18,2) NOT NULL DEFAULT 0,
    transferred_tax_total   numeric(18,2) NOT NULL DEFAULT 0,
    withheld_tax_total      numeric(18,2) NOT NULL DEFAULT 0,
    total                   numeric(18,2) NOT NULL DEFAULT 0,
    -- Classification
    category                varchar(60),
    deductible              boolean      NOT NULL DEFAULT true,
    accounting_account      varchar(30),
    notes                   text,
    -- SAT Validation
    sat_validation_status   varchar(30),
    sat_validated_at        timestamptz,
    -- Storage
    xml_object_key          varchar(500),
    pdf_object_key          varchar(500),
    fiscal_snapshot         jsonb        NOT NULL DEFAULT '{}'::jsonb,
    -- Idempotency
    idempotency_key         varchar(100),
    -- Audit
    created_at              timestamptz  NOT NULL DEFAULT now(),
    created_by              uuid,
    updated_at              timestamptz  NOT NULL DEFAULT now(),
    updated_by              uuid,
    row_version             bigint       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX idx_expenses_cfdi_uuid    ON expenses(company_id, cfdi_uuid) WHERE cfdi_uuid IS NOT NULL;
CREATE UNIQUE INDEX idx_expenses_idempotency  ON expenses(company_id, idempotency_key) WHERE idempotency_key IS NOT NULL;
CREATE INDEX idx_expenses_supplier            ON expenses(company_id, supplier_id, issued_at DESC);
CREATE INDEX idx_expenses_status              ON expenses(company_id, status, issued_at DESC);

-- ── Expense Lines ──
CREATE TABLE expense_lines (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    expense_id          uuid         NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    company_id          uuid         NOT NULL REFERENCES companies(id),
    line_number         int          NOT NULL,
    sat_product_code    varchar(8),
    description         varchar(1000) NOT NULL,
    sat_unit_code       varchar(10),
    quantity            numeric(18,6) NOT NULL DEFAULT 1,
    unit_price          numeric(18,6) NOT NULL,
    discount            numeric(18,2) NOT NULL DEFAULT 0,
    subtotal            numeric(18,2) NOT NULL,
    objeto_imp_code     varchar(2),
    created_at          timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_expense_lines ON expense_lines(expense_id, line_number);
