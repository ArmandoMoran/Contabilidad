-- ============================================================
-- V3: Parties — clients, suppliers, contacts, addresses
-- ============================================================

-- ── Clients ──
CREATE TABLE clients (
    id                      uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id              uuid         NOT NULL REFERENCES companies(id),
    rfc                     varchar(13)  NOT NULL,
    legal_name              varchar(255) NOT NULL,
    trade_name              varchar(255),
    email                   varchar(255),
    phone                   varchar(30),
    website                 varchar(255),
    fiscal_regime_code      varchar(3)   NOT NULL,
    default_uso_cfdi_code   varchar(4),
    default_forma_pago_code varchar(2),
    default_metodo_pago_code varchar(3),
    default_postal_code     varchar(5),
    notes                   text,
    active                  boolean      NOT NULL DEFAULT true,
    deleted_at              timestamptz,
    deleted_by              uuid,
    created_at              timestamptz  NOT NULL DEFAULT now(),
    created_by              uuid,
    updated_at              timestamptz  NOT NULL DEFAULT now(),
    updated_by              uuid,
    row_version             bigint       NOT NULL DEFAULT 0,
    UNIQUE (company_id, rfc)
);
CREATE INDEX idx_clients_company_active ON clients(company_id, active) WHERE deleted_at IS NULL;
CREATE INDEX idx_clients_legal_name     ON clients(company_id, legal_name) WHERE deleted_at IS NULL;

-- ── Suppliers ──
CREATE TABLE suppliers (
    id                      uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id              uuid         NOT NULL REFERENCES companies(id),
    rfc                     varchar(13)  NOT NULL,
    legal_name              varchar(255) NOT NULL,
    trade_name              varchar(255),
    email                   varchar(255),
    phone                   varchar(30),
    website                 varchar(255),
    fiscal_regime_code      varchar(3)   NOT NULL,
    default_forma_pago_code varchar(2),
    nationality             varchar(20)  NOT NULL DEFAULT 'NATIONAL',
    diot_operation_type     varchar(20),
    notes                   text,
    active                  boolean      NOT NULL DEFAULT true,
    deleted_at              timestamptz,
    deleted_by              uuid,
    created_at              timestamptz  NOT NULL DEFAULT now(),
    created_by              uuid,
    updated_at              timestamptz  NOT NULL DEFAULT now(),
    updated_by              uuid,
    row_version             bigint       NOT NULL DEFAULT 0,
    UNIQUE (company_id, rfc)
);
CREATE INDEX idx_suppliers_company_active ON suppliers(company_id, active) WHERE deleted_at IS NULL;

-- ── Addresses (shared for clients and suppliers) ──
CREATE TABLE addresses (
    id                uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id        uuid         NOT NULL REFERENCES companies(id),
    party_type        varchar(20)  NOT NULL CHECK (party_type IN ('CLIENT','SUPPLIER','COMPANY')),
    party_id          uuid         NOT NULL,
    address_type      varchar(20)  NOT NULL DEFAULT 'FISCAL',
    street1           varchar(255) NOT NULL,
    street2           varchar(255),
    exterior_number   varchar(30),
    interior_number   varchar(30),
    neighborhood      varchar(100),
    city              varchar(100),
    municipality_code varchar(5),
    state_code        varchar(3),
    postal_code       varchar(5)   NOT NULL,
    country_code      varchar(3)   NOT NULL DEFAULT 'MEX',
    is_primary        boolean      NOT NULL DEFAULT false,
    deleted_at        timestamptz,
    created_at        timestamptz  NOT NULL DEFAULT now(),
    created_by        uuid,
    updated_at        timestamptz  NOT NULL DEFAULT now(),
    updated_by        uuid,
    row_version       bigint       NOT NULL DEFAULT 0
);
CREATE INDEX idx_addresses_party ON addresses(party_type, party_id) WHERE deleted_at IS NULL;

-- ── Contacts ──
CREATE TABLE contacts (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id      uuid         NOT NULL REFERENCES companies(id),
    party_type      varchar(20)  NOT NULL CHECK (party_type IN ('CLIENT','SUPPLIER')),
    party_id        uuid         NOT NULL,
    full_name       varchar(255) NOT NULL,
    email           varchar(255),
    phone           varchar(30),
    position        varchar(100),
    is_primary      boolean      NOT NULL DEFAULT false,
    deleted_at      timestamptz,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    created_by      uuid,
    updated_at      timestamptz  NOT NULL DEFAULT now(),
    updated_by      uuid,
    row_version     bigint       NOT NULL DEFAULT 0
);
CREATE INDEX idx_contacts_party ON contacts(party_type, party_id) WHERE deleted_at IS NULL;
