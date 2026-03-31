-- ============================================================
-- V4: Products — catalog with SAT codes and tax profiles
-- ============================================================

CREATE TABLE products (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id          uuid         NOT NULL REFERENCES companies(id),
    internal_code       varchar(50),
    internal_name       varchar(255) NOT NULL,
    description         text,
    sat_product_code    varchar(8)   NOT NULL,
    sat_unit_code       varchar(10)  NOT NULL,
    unit_price          numeric(18,2) NOT NULL DEFAULT 0,
    currency_code       varchar(3)   NOT NULL DEFAULT 'MXN',
    objeto_imp_code     varchar(2)   NOT NULL DEFAULT '02',
    cuenta_predial      varchar(50),
    active              boolean      NOT NULL DEFAULT true,
    deleted_at          timestamptz,
    deleted_by          uuid,
    created_at          timestamptz  NOT NULL DEFAULT now(),
    created_by          uuid,
    updated_at          timestamptz  NOT NULL DEFAULT now(),
    updated_by          uuid,
    row_version         bigint       NOT NULL DEFAULT 0
);
CREATE INDEX idx_products_company ON products(company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_name    ON products(company_id, internal_name) WHERE deleted_at IS NULL;

-- ── Product Tax Profiles (versioned) ──
CREATE TABLE product_tax_profiles (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    product_id          uuid         NOT NULL REFERENCES products(id),
    company_id          uuid         NOT NULL REFERENCES companies(id),
    tax_code            varchar(3)   NOT NULL,
    factor_type         varchar(10)  NOT NULL,
    rate                numeric(12,6) NOT NULL,
    is_transfer         boolean      NOT NULL DEFAULT true,
    is_withholding      boolean      NOT NULL DEFAULT false,
    valid_from          date         NOT NULL,
    valid_to            date,
    active              boolean      NOT NULL DEFAULT true,
    created_at          timestamptz  NOT NULL DEFAULT now(),
    created_by          uuid,
    updated_at          timestamptz  NOT NULL DEFAULT now(),
    updated_by          uuid,
    row_version         bigint       NOT NULL DEFAULT 0
);
CREATE INDEX idx_product_tax_profiles ON product_tax_profiles(product_id, active, valid_from);
