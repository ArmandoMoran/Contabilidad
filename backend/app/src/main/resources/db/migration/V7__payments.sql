-- ============================================================
-- V7: Payments — cobros, pagos, applications, REP 2.0
-- ============================================================

CREATE TABLE payments (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id          uuid         NOT NULL REFERENCES companies(id),
    payment_direction   varchar(10)  NOT NULL CHECK (payment_direction IN ('INBOUND','OUTBOUND')),
    status              varchar(30)  NOT NULL DEFAULT 'REGISTERED',
    payment_form_code   varchar(2)   NOT NULL,
    currency_code       varchar(3)   NOT NULL DEFAULT 'MXN',
    exchange_rate       numeric(18,6) NOT NULL DEFAULT 1,
    amount              numeric(18,2) NOT NULL,
    paid_at             timestamptz  NOT NULL,
    operation_number    varchar(100),
    payer_rfc           varchar(13),
    payer_name          varchar(255),
    payer_bank_rfc      varchar(13),
    payer_account       varchar(50),
    payee_rfc           varchar(13),
    payee_name          varchar(255),
    payee_bank_rfc      varchar(13),
    payee_account       varchar(50),
    notes               text,
    -- REP reference (if payment complement was generated)
    rep_invoice_id      uuid         REFERENCES invoices(id),
    idempotency_key     varchar(100),
    created_at          timestamptz  NOT NULL DEFAULT now(),
    created_by          uuid,
    updated_at          timestamptz  NOT NULL DEFAULT now(),
    updated_by          uuid,
    row_version         bigint       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX idx_payments_idempotency ON payments(company_id, idempotency_key) WHERE idempotency_key IS NOT NULL;
CREATE INDEX idx_payments_company_date       ON payments(company_id, paid_at DESC);

-- ── Payment Applications ──
CREATE TABLE payment_applications (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    payment_id          uuid         NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    company_id          uuid         NOT NULL REFERENCES companies(id),
    document_type       varchar(20)  NOT NULL CHECK (document_type IN ('INVOICE','EXPENSE')),
    document_id         uuid         NOT NULL,
    document_uuid       uuid,
    document_series     varchar(25),
    document_folio      varchar(40),
    installment_number  int          NOT NULL DEFAULT 1,
    previous_balance    numeric(18,2) NOT NULL,
    amount_paid         numeric(18,2) NOT NULL,
    remaining_balance   numeric(18,2) NOT NULL,
    currency_code       varchar(3)   NOT NULL DEFAULT 'MXN',
    exchange_rate       numeric(18,6) NOT NULL DEFAULT 1,
    payment_method_code varchar(3)   NOT NULL DEFAULT 'PPD',
    created_at          timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_payment_applications_payment  ON payment_applications(payment_id);
CREATE INDEX idx_payment_applications_document ON payment_applications(document_type, document_id);
