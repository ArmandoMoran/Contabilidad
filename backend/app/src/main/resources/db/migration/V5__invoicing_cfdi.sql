-- ============================================================
-- V5: Invoicing — CFDI 4.0, lines, tax lines, related docs
-- ============================================================

CREATE TABLE invoices (
    id                      uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id              uuid         NOT NULL REFERENCES companies(id),
    client_id               uuid         REFERENCES clients(id),
    invoice_type            varchar(1)   NOT NULL,
    status                  varchar(30)  NOT NULL DEFAULT 'DRAFT',
    series                  varchar(25),
    folio                   varchar(40),
    cfdi_version            varchar(10)  NOT NULL DEFAULT '4.0',
    issued_at               timestamptz,
    certified_at            timestamptz,
    cancelled_at            timestamptz,
    currency_code           varchar(3)   NOT NULL DEFAULT 'MXN',
    exchange_rate           numeric(18,6) NOT NULL DEFAULT 1,
    payment_method_code     varchar(3)   NOT NULL,
    payment_form_code       varchar(2),
    uso_cfdi_code           varchar(4)   NOT NULL,
    export_code             varchar(2)   NOT NULL DEFAULT '01',
    global_periodicity      varchar(5),
    global_month            varchar(2),
    global_year             varchar(4),
    -- Issuer snapshot
    issuer_rfc              varchar(13)  NOT NULL,
    issuer_name             varchar(255) NOT NULL,
    issuer_regime_code      varchar(3)   NOT NULL,
    -- Receiver snapshot
    receiver_rfc            varchar(13)  NOT NULL,
    receiver_name           varchar(255) NOT NULL,
    receiver_regime_code    varchar(3)   NOT NULL,
    receiver_postal_code    varchar(5)   NOT NULL,
    -- Totals
    subtotal                numeric(18,2) NOT NULL DEFAULT 0,
    discount                numeric(18,2) NOT NULL DEFAULT 0,
    transferred_tax_total   numeric(18,2) NOT NULL DEFAULT 0,
    withheld_tax_total      numeric(18,2) NOT NULL DEFAULT 0,
    total                   numeric(18,2) NOT NULL DEFAULT 0,
    -- PAC / Stamp
    pac_uuid                uuid,
    pac_cert_number         varchar(30),
    sat_cert_number         varchar(30),
    pac_seal                text,
    sat_seal                text,
    stamp_date              timestamptz,
    original_chain          text,
    pac_status              varchar(30),
    -- Cancellation
    cancel_reason_code      varchar(2),
    cancel_replacement_uuid uuid,
    cancel_acuse            text,
    -- Storage
    xml_object_key          varchar(500),
    pdf_object_key          varchar(500),
    acuse_object_key        varchar(500),
    -- Fiscal snapshot
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
CREATE UNIQUE INDEX idx_invoices_series_folio ON invoices(company_id, series, folio) WHERE series IS NOT NULL AND folio IS NOT NULL;
CREATE UNIQUE INDEX idx_invoices_pac_uuid     ON invoices(company_id, pac_uuid)       WHERE pac_uuid IS NOT NULL;
CREATE UNIQUE INDEX idx_invoices_idempotency  ON invoices(company_id, idempotency_key) WHERE idempotency_key IS NOT NULL;
CREATE INDEX idx_invoices_status              ON invoices(company_id, status, issued_at DESC);
CREATE INDEX idx_invoices_client              ON invoices(company_id, client_id, issued_at DESC);

-- ── Invoice Lines ──
CREATE TABLE invoice_lines (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    invoice_id          uuid         NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    company_id          uuid         NOT NULL REFERENCES companies(id),
    line_number         int          NOT NULL,
    product_id          uuid         REFERENCES products(id),
    sat_product_code    varchar(8)   NOT NULL,
    description         varchar(1000) NOT NULL,
    sat_unit_code       varchar(10)  NOT NULL,
    unit_name           varchar(100),
    quantity            numeric(18,6) NOT NULL,
    unit_price          numeric(18,6) NOT NULL,
    discount            numeric(18,2) NOT NULL DEFAULT 0,
    subtotal            numeric(18,2) NOT NULL,
    objeto_imp_code     varchar(2)   NOT NULL DEFAULT '02',
    cuenta_predial      varchar(50),
    tax_profile_snapshot jsonb        NOT NULL DEFAULT '[]'::jsonb,
    created_at          timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_invoice_lines ON invoice_lines(invoice_id, line_number);

-- ── Tax Lines (fact table, shared by invoices and expenses) ──
CREATE TABLE tax_lines (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id          uuid         NOT NULL REFERENCES companies(id),
    source_type         varchar(20)  NOT NULL CHECK (source_type IN ('INVOICE','EXPENSE','PAYMENT_COMPLEMENT')),
    source_id           uuid         NOT NULL,
    source_line_id      uuid,
    tax_code            varchar(3)   NOT NULL,
    factor_type         varchar(10)  NOT NULL,
    rate                numeric(12,6) NOT NULL,
    base_amount         numeric(18,2) NOT NULL,
    tax_amount          numeric(18,2) NOT NULL,
    is_transfer         boolean      NOT NULL,
    is_withholding      boolean      NOT NULL,
    period_key          varchar(7)   NOT NULL,
    issued_at           timestamptz,
    payment_at          timestamptz,
    created_at          timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_tax_lines_period   ON tax_lines(company_id, period_key, tax_code, is_transfer, is_withholding);
CREATE INDEX idx_tax_lines_source   ON tax_lines(source_type, source_id);

-- ── Related Documents ──
CREATE TABLE invoice_related_documents (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    invoice_id          uuid         NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    relation_type_code  varchar(2)   NOT NULL,
    related_uuid        uuid         NOT NULL,
    created_at          timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_invoice_related ON invoice_related_documents(invoice_id);
