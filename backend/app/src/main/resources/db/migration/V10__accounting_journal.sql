-- ============================================================
-- V10: Accounting — journal, chart of accounts, mappings
-- ============================================================

-- ── Account Catalog (Chart of Accounts) ──
CREATE TABLE account_catalog (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id      uuid         NOT NULL REFERENCES companies(id),
    account_code    varchar(30)  NOT NULL,
    parent_code     varchar(30),
    account_name    varchar(255) NOT NULL,
    account_type    varchar(20)  NOT NULL CHECK (account_type IN ('ASSET','LIABILITY','EQUITY','REVENUE','EXPENSE')),
    nature          varchar(10)  NOT NULL CHECK (nature IN ('DEBIT','CREDIT')),
    level           int          NOT NULL DEFAULT 1,
    sat_group_code  varchar(10),
    active          boolean      NOT NULL DEFAULT true,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    updated_at      timestamptz  NOT NULL DEFAULT now(),
    row_version     bigint       NOT NULL DEFAULT 0,
    UNIQUE (company_id, account_code)
);
CREATE INDEX idx_account_catalog_parent ON account_catalog(company_id, parent_code);

-- ── Journal Entries ──
CREATE TABLE journal_entries (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id      uuid         NOT NULL REFERENCES companies(id),
    entry_number    varchar(30),
    entry_date      date         NOT NULL,
    period_key      varchar(7)   NOT NULL,
    entry_type      varchar(30)  NOT NULL,
    description     varchar(500) NOT NULL,
    source_module   varchar(60),
    source_id       uuid,
    status          varchar(20)  NOT NULL DEFAULT 'POSTED',
    total_debit     numeric(18,2) NOT NULL DEFAULT 0,
    total_credit    numeric(18,2) NOT NULL DEFAULT 0,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    created_by      uuid,
    updated_at      timestamptz  NOT NULL DEFAULT now(),
    row_version     bigint       NOT NULL DEFAULT 0
);
CREATE INDEX idx_journal_entries_period ON journal_entries(company_id, period_key, entry_date);

-- ── Journal Lines ──
CREATE TABLE journal_lines (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    journal_entry_id    uuid         NOT NULL REFERENCES journal_entries(id) ON DELETE CASCADE,
    company_id          uuid         NOT NULL REFERENCES companies(id),
    account_code        varchar(30)  NOT NULL,
    description         varchar(500),
    debit               numeric(18,2) NOT NULL DEFAULT 0,
    credit              numeric(18,2) NOT NULL DEFAULT 0,
    created_at          timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_journal_lines_entry   ON journal_lines(journal_entry_id);
CREATE INDEX idx_journal_lines_account ON journal_lines(company_id, account_code, journal_entry_id);

-- ── Account Mapping Rules (auto-posting) ──
CREATE TABLE account_mapping_rules (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id          uuid         NOT NULL REFERENCES companies(id),
    rule_name           varchar(100) NOT NULL,
    source_module       varchar(60)  NOT NULL,
    source_event        varchar(60)  NOT NULL,
    condition_expression text,
    debit_account_code  varchar(30)  NOT NULL,
    credit_account_code varchar(30)  NOT NULL,
    amount_expression   varchar(100) NOT NULL DEFAULT 'amount',
    priority            int          NOT NULL DEFAULT 100,
    active              boolean      NOT NULL DEFAULT true,
    created_at          timestamptz  NOT NULL DEFAULT now(),
    updated_at          timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_mapping_rules ON account_mapping_rules(company_id, source_module, source_event, active);
