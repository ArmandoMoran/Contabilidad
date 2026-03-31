-- ============================================================
-- V1: Foundation — extensions, companies, users, roles, audit
-- ============================================================

-- UUID v7 generation function
CREATE OR REPLACE FUNCTION uuidv7() RETURNS uuid AS $$
DECLARE
    v_time  bigint;
    v_bytes bytea;
BEGIN
    v_time := (extract(epoch from clock_timestamp()) * 1000)::bigint;
    v_bytes := decode(
        lpad(to_hex(v_time), 12, '0') ||
        lpad(to_hex((random() * x'0fff'::int)::int | x'7000'::int), 4, '0') ||
        lpad(to_hex((random() * x'3fffffffffffffff'::bigint)::bigint | x'8000000000000000'::bigint), 16, '0'),
        'hex'
    );
    RETURN encode(v_bytes, 'hex')::uuid;
END;
$$ LANGUAGE plpgsql VOLATILE;

-- ── Companies ──
CREATE TABLE companies (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    rfc             varchar(13)  NOT NULL UNIQUE,
    legal_name      varchar(255) NOT NULL,
    taxpayer_type   varchar(20)  NOT NULL CHECK (taxpayer_type IN ('PERSONA_MORAL','PERSONA_FISICA')),
    fiscal_regime_code varchar(3) NOT NULL,
    tax_zone_profile   varchar(20) NOT NULL DEFAULT 'STANDARD',
    postal_code     varchar(5),
    logo_url        varchar(500),
    active          boolean      NOT NULL DEFAULT true,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    created_by      uuid,
    updated_at      timestamptz  NOT NULL DEFAULT now(),
    updated_by      uuid,
    row_version     bigint       NOT NULL DEFAULT 0
);

-- ── Users ──
CREATE TABLE users (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id      uuid         NOT NULL REFERENCES companies(id),
    email           varchar(255) NOT NULL,
    password_hash   varchar(255) NOT NULL,
    full_name       varchar(255) NOT NULL,
    role            varchar(30)  NOT NULL DEFAULT 'readonly',
    active          boolean      NOT NULL DEFAULT true,
    locked          boolean      NOT NULL DEFAULT false,
    failed_attempts int          NOT NULL DEFAULT 0,
    last_login_at   timestamptz,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    created_by      uuid,
    updated_at      timestamptz  NOT NULL DEFAULT now(),
    updated_by      uuid,
    row_version     bigint       NOT NULL DEFAULT 0,
    UNIQUE (company_id, email)
);

-- ── Permissions ──
CREATE TABLE permissions (
    id          uuid PRIMARY KEY DEFAULT uuidv7(),
    code        varchar(80)  NOT NULL UNIQUE,
    description varchar(255)
);

CREATE TABLE role_permissions (
    role       varchar(30) NOT NULL,
    permission_id uuid     NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role, permission_id)
);

-- ── Refresh Tokens ──
CREATE TABLE refresh_tokens (
    id           uuid PRIMARY KEY DEFAULT uuidv7(),
    user_id      uuid         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash   varchar(255) NOT NULL UNIQUE,
    expires_at   timestamptz  NOT NULL,
    revoked      boolean      NOT NULL DEFAULT false,
    created_at   timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- ── Audit Log ──
CREATE TABLE audit_log (
    id          uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id  uuid,
    user_id     uuid,
    module      varchar(60)  NOT NULL,
    action      varchar(60)  NOT NULL,
    entity_type varchar(60),
    entity_id   uuid,
    before_hash varchar(64),
    after_hash  varchar(64),
    details     jsonb,
    ip_address  varchar(45),
    user_agent  varchar(500),
    trace_id    varchar(64),
    created_at  timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_audit_log_company   ON audit_log(company_id, created_at DESC);
CREATE INDEX idx_audit_log_entity    ON audit_log(entity_type, entity_id);

-- ── Seed base permissions ──
INSERT INTO permissions (id, code, description) VALUES
    (uuidv7(), 'clients:read',             'Ver clientes'),
    (uuidv7(), 'clients:write',            'Crear/editar clientes'),
    (uuidv7(), 'suppliers:read',           'Ver proveedores'),
    (uuidv7(), 'suppliers:write',          'Crear/editar proveedores'),
    (uuidv7(), 'products:read',            'Ver productos'),
    (uuidv7(), 'products:write',           'Crear/editar productos'),
    (uuidv7(), 'invoices:read',            'Ver facturas'),
    (uuidv7(), 'invoices:write',           'Crear/editar borradores'),
    (uuidv7(), 'invoices:stamp',           'Timbrar CFDI'),
    (uuidv7(), 'invoices:cancel',          'Cancelar CFDI'),
    (uuidv7(), 'expenses:read',            'Ver gastos'),
    (uuidv7(), 'expenses:write',           'Crear/editar gastos'),
    (uuidv7(), 'expenses:import',          'Importar XML de gastos'),
    (uuidv7(), 'payments:read',            'Ver pagos'),
    (uuidv7(), 'payments:write',           'Registrar pagos'),
    (uuidv7(), 'declarations:read',        'Ver declaraciones'),
    (uuidv7(), 'declarations:close',       'Cerrar declaraciones'),
    (uuidv7(), 'reports:read',             'Ver reportes'),
    (uuidv7(), 'reports:export',           'Exportar reportes'),
    (uuidv7(), 'catalogs:sync',            'Sincronizar catálogos SAT'),
    (uuidv7(), 'certificates:manage',      'Administrar certificados'),
    (uuidv7(), 'users:manage',             'Administrar usuarios'),
    (uuidv7(), 'company:manage',           'Configurar empresa'),
    (uuidv7(), 'audit:read',              'Ver bitácora de auditoría');
