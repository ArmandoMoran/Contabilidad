-- ============================================================
-- V2: SAT Catalogs — all versioned catalog tables
-- ============================================================

-- ── Régimen Fiscal ──
CREATE TABLE sat_regimen_fiscal (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(3)   NOT NULL,
    description     varchar(255) NOT NULL,
    applies_pf      boolean      NOT NULL DEFAULT false,
    applies_pm      boolean      NOT NULL DEFAULT false,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    source_hash     varchar(64),
    loaded_at       timestamptz  NOT NULL DEFAULT now(),
    superseded_by   uuid REFERENCES sat_regimen_fiscal(id)
);
CREATE INDEX idx_sat_regimen_code ON sat_regimen_fiscal(code, valid_from, valid_to);

-- ── Uso CFDI ──
CREATE TABLE sat_uso_cfdi (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(4)   NOT NULL,
    description     varchar(255) NOT NULL,
    applies_pf      boolean      NOT NULL DEFAULT false,
    applies_pm      boolean      NOT NULL DEFAULT false,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    source_hash     varchar(64),
    loaded_at       timestamptz  NOT NULL DEFAULT now(),
    superseded_by   uuid REFERENCES sat_uso_cfdi(id)
);
CREATE INDEX idx_sat_uso_cfdi_code ON sat_uso_cfdi(code, valid_from, valid_to);

-- ── Uso CFDI × Régimen (validation matrix) ──
CREATE TABLE sat_uso_cfdi_regimen (
    id                   uuid PRIMARY KEY DEFAULT uuidv7(),
    uso_cfdi_code        varchar(4) NOT NULL,
    regimen_fiscal_code  varchar(3) NOT NULL,
    valid_from           date,
    valid_to             date,
    active               boolean NOT NULL DEFAULT true,
    sat_release_tag      varchar(30),
    loaded_at            timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX idx_sat_uso_cfdi_regimen ON sat_uso_cfdi_regimen(uso_cfdi_code, regimen_fiscal_code);

-- ── Forma de Pago ──
CREATE TABLE sat_forma_pago (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(2)   NOT NULL,
    description     varchar(255) NOT NULL,
    banked          boolean      NOT NULL DEFAULT false,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    source_hash     varchar(64),
    loaded_at       timestamptz  NOT NULL DEFAULT now(),
    superseded_by   uuid REFERENCES sat_forma_pago(id)
);
CREATE INDEX idx_sat_forma_pago_code ON sat_forma_pago(code);

-- ── Método de Pago ──
CREATE TABLE sat_metodo_pago (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(3)   NOT NULL,
    description     varchar(100) NOT NULL,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Moneda ──
CREATE TABLE sat_moneda (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(3)   NOT NULL,
    description     varchar(100) NOT NULL,
    decimals        int          NOT NULL DEFAULT 2,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Impuesto ──
CREATE TABLE sat_impuesto (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(3)   NOT NULL,
    description     varchar(100) NOT NULL,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Tipo Factor ──
CREATE TABLE sat_tipo_factor (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(10)  NOT NULL,
    description     varchar(100) NOT NULL,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Tasa o Cuota ──
CREATE TABLE sat_tasa_o_cuota (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    impuesto_code   varchar(3)   NOT NULL,
    tipo_factor_code varchar(10) NOT NULL,
    rate            numeric(12,6) NOT NULL,
    transfer        boolean      NOT NULL DEFAULT true,
    withholding     boolean      NOT NULL DEFAULT false,
    min_value       numeric(12,6),
    max_value       numeric(12,6),
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_sat_tasa_impuesto ON sat_tasa_o_cuota(impuesto_code, tipo_factor_code, transfer, withholding);

-- ── Clave Producto/Servicio ──
CREATE TABLE sat_clave_prod_serv (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(8)   NOT NULL,
    description     varchar(500) NOT NULL,
    includes_iva_transfer  boolean DEFAULT false,
    includes_ieps_transfer boolean DEFAULT false,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    source_hash     varchar(64),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_sat_prod_serv_code ON sat_clave_prod_serv(code);

-- ── Clave Unidad ──
CREATE TABLE sat_clave_unidad (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(10)  NOT NULL,
    name            varchar(255) NOT NULL,
    description     varchar(500),
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_sat_unidad_code ON sat_clave_unidad(code);

-- ── Código Postal ──
CREATE TABLE sat_codigo_postal (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(5)   NOT NULL,
    state_code      varchar(3),
    municipality_code varchar(5),
    locality_code   varchar(4),
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_sat_cp_code ON sat_codigo_postal(code);

-- ── Estado ──
CREATE TABLE sat_estado (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(3)   NOT NULL,
    country_code    varchar(3)   NOT NULL DEFAULT 'MEX',
    description     varchar(100) NOT NULL,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Municipio ──
CREATE TABLE sat_municipio (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(5)   NOT NULL,
    state_code      varchar(3)   NOT NULL,
    description     varchar(200) NOT NULL,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Localidad ──
CREATE TABLE sat_localidad (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(4)   NOT NULL,
    state_code      varchar(3)   NOT NULL,
    description     varchar(200) NOT NULL,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── País ──
CREATE TABLE sat_pais (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(3)   NOT NULL,
    description     varchar(200) NOT NULL,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Objeto de Impuesto ──
CREATE TABLE sat_objeto_imp (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(2)   NOT NULL,
    description     varchar(200) NOT NULL,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Tipo de Comprobante ──
CREATE TABLE sat_tipo_comprobante (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(1)   NOT NULL,
    description     varchar(100) NOT NULL,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Motivo de Cancelación ──
CREATE TABLE sat_motivo_cancelacion (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(2)   NOT NULL,
    description     varchar(255) NOT NULL,
    requires_replacement boolean NOT NULL DEFAULT false,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Tipo de Relación ──
CREATE TABLE sat_tipo_relacion (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    code            varchar(2)   NOT NULL,
    description     varchar(200) NOT NULL,
    valid_from      date,
    valid_to        date,
    active          boolean      NOT NULL DEFAULT true,
    sat_release_tag varchar(30),
    loaded_at       timestamptz  NOT NULL DEFAULT now()
);

-- ── Catalog Sync Runs ──
CREATE TABLE catalog_sync_runs (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    catalog_name    varchar(60)  NOT NULL,
    source_url      varchar(500),
    source_hash     varchar(64),
    records_loaded  int          NOT NULL DEFAULT 0,
    records_updated int          NOT NULL DEFAULT 0,
    records_deactivated int      NOT NULL DEFAULT 0,
    status          varchar(20)  NOT NULL DEFAULT 'RUNNING',
    error_message   text,
    started_at      timestamptz  NOT NULL DEFAULT now(),
    finished_at     timestamptz,
    triggered_by    uuid
);
