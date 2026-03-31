-- ============================================================
-- V8: Tax Engine — rules, obligations, zone eligibility
-- ============================================================

-- ── Tax Rules (configurable per regime/person/operation) ──
CREATE TABLE tax_rules (
    id                      uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id              uuid,
    rule_name               varchar(100)  NOT NULL,
    tax_code                varchar(3)    NOT NULL,
    factor_type             varchar(10)   NOT NULL DEFAULT 'Tasa',
    rate                    numeric(12,6) NOT NULL,
    is_transfer             boolean       NOT NULL DEFAULT true,
    is_withholding          boolean       NOT NULL DEFAULT false,
    applies_to_taxpayer_type varchar(20),
    applies_to_regime_code  varchar(3),
    applies_to_operation    varchar(60),
    applies_to_region       varchar(20),
    requires_zone_eligibility boolean     NOT NULL DEFAULT false,
    inactive_by_default     boolean       NOT NULL DEFAULT false,
    valid_from              date          NOT NULL DEFAULT '2020-01-01',
    valid_to                date,
    active                  boolean       NOT NULL DEFAULT true,
    notes                   text,
    created_at              timestamptz   NOT NULL DEFAULT now(),
    updated_at              timestamptz   NOT NULL DEFAULT now()
);
CREATE INDEX idx_tax_rules_lookup ON tax_rules(tax_code, is_transfer, is_withholding, active);

-- ── Zone Eligibility (for IVA 8% border region) ──
CREATE TABLE zone_eligibility_rules (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    zone_code           varchar(20)  NOT NULL,
    zone_name           varchar(100) NOT NULL,
    description         text,
    eligible_states     jsonb,
    eligible_municipalities jsonb,
    eligible_postal_codes   jsonb,
    stimulus_type       varchar(40)  NOT NULL,
    iva_rate_override   numeric(12,6),
    isr_rate_override   numeric(12,6),
    valid_from          date         NOT NULL,
    valid_to            date,
    active              boolean      NOT NULL DEFAULT true,
    created_at          timestamptz  NOT NULL DEFAULT now(),
    updated_at          timestamptz  NOT NULL DEFAULT now()
);

-- ── Obligation Rules ──
CREATE TABLE obligation_rules (
    id                      uuid PRIMARY KEY DEFAULT uuidv7(),
    obligation_code         varchar(40)   NOT NULL,
    obligation_name         varchar(200)  NOT NULL,
    frequency               varchar(20)   NOT NULL CHECK (frequency IN ('MONTHLY','BIMONTHLY','QUARTERLY','ANNUAL','ON_DEMAND')),
    tax_type                varchar(10),
    applies_to_taxpayer_type varchar(20),
    applies_to_regime_code  varchar(3),
    applies_to_region       varchar(20),
    formula_key             varchar(60),
    due_day                 int,
    grace_days              int           NOT NULL DEFAULT 0,
    active                  boolean       NOT NULL DEFAULT true,
    notes                   text,
    valid_from              date          NOT NULL DEFAULT '2020-01-01',
    valid_to                date,
    created_at              timestamptz   NOT NULL DEFAULT now(),
    updated_at              timestamptz   NOT NULL DEFAULT now()
);
CREATE INDEX idx_obligation_rules_regime ON obligation_rules(applies_to_regime_code, frequency, active);

-- ── Declaration Templates ──
CREATE TABLE declaration_templates (
    id                  uuid PRIMARY KEY DEFAULT uuidv7(),
    template_code       varchar(40)  NOT NULL,
    template_name       varchar(200) NOT NULL,
    declaration_type    varchar(20)  NOT NULL CHECK (declaration_type IN ('MONTHLY','ANNUAL','DIOT','INFORMATIVE')),
    applies_to_taxpayer_type varchar(20),
    applies_to_regime_code varchar(3),
    sections            jsonb        NOT NULL DEFAULT '[]'::jsonb,
    formula_parameters  jsonb        NOT NULL DEFAULT '{}'::jsonb,
    active              boolean      NOT NULL DEFAULT true,
    valid_from          date         NOT NULL,
    valid_to            date,
    created_at          timestamptz  NOT NULL DEFAULT now(),
    updated_at          timestamptz  NOT NULL DEFAULT now()
);

-- ── Seed starter tax rules (IVA, ISR, retenciones) ──
INSERT INTO tax_rules (id, rule_name, tax_code, factor_type, rate, is_transfer, is_withholding, applies_to_regime_code, applies_to_region, notes) VALUES
    (uuidv7(), 'IVA Traslado 16%',            '002', 'Tasa', 0.160000, true, false, NULL, NULL, 'Tasa general IVA'),
    (uuidv7(), 'IVA Traslado 0%',             '002', 'Tasa', 0.000000, true, false, NULL, NULL, 'Tasa cero IVA — actos gravados a tasa 0'),
    (uuidv7(), 'IVA Exento',                  '002', 'Exento', 0.000000, true, false, NULL, NULL, 'Actos exentos de IVA — sin traslado causado');

INSERT INTO tax_rules (id, rule_name, tax_code, factor_type, rate, is_transfer, is_withholding, applies_to_regime_code, applies_to_region, requires_zone_eligibility, notes) VALUES
    (uuidv7(), 'IVA Traslado 8% Frontera',    '002', 'Tasa', 0.080000, true, false, NULL, 'BORDER', true, 'IVA 8% estímulo fronterizo — solo con zone_eligibility');

INSERT INTO tax_rules (id, rule_name, tax_code, factor_type, rate, is_transfer, is_withholding, applies_to_taxpayer_type, applies_to_operation, notes) VALUES
    (uuidv7(), 'IVA Ret 10.6667%',            '002', 'Tasa', 0.106667, false, true, 'PERSONA_FISICA', 'SERVICIOS_PROFESIONALES', 'Retención 2/3 IVA — PF servicios a PM, RLIVA art 3'),
    (uuidv7(), 'IVA Ret 4% Autotransporte',   '002', 'Tasa', 0.040000, false, true, NULL, 'AUTOTRANSPORTE', 'Retención IVA autotransporte terrestre bienes, RLIVA art 3');

INSERT INTO tax_rules (id, rule_name, tax_code, factor_type, rate, is_transfer, is_withholding, applies_to_taxpayer_type, applies_to_operation, inactive_by_default, notes) VALUES
    (uuidv7(), 'IVA Ret 6% (hist.)',          '002', 'Tasa', 0.060000, false, true, NULL, NULL, true, 'Histórica art 1-A fr IV derogado — inactive_by_default');

INSERT INTO tax_rules (id, rule_name, tax_code, factor_type, rate, is_transfer, is_withholding, applies_to_taxpayer_type, applies_to_operation, notes) VALUES
    (uuidv7(), 'ISR Ret 10% Honorarios/Arrend', '001', 'Tasa', 0.100000, false, true, 'PERSONA_FISICA', 'HONORARIOS_ARRENDAMIENTO', 'Retención ISR PF honorarios/arrendamiento'),
    (uuidv7(), 'ISR Ret 1.25% RESICO',        '001', 'Tasa', 0.012500, false, true, 'PERSONA_FISICA', 'RESICO', 'ISR RESICO PF — bloquear fuera de perfiles autorizados');

-- ── Seed starter obligation rules ──
INSERT INTO obligation_rules (id, obligation_code, obligation_name, frequency, tax_type, applies_to_taxpayer_type, applies_to_regime_code) VALUES
    (uuidv7(), 'ISR_MENSUAL_PM',       'ISR Provisional Mensual PM',         'MONTHLY',  'ISR', 'PERSONA_MORAL',  '601'),
    (uuidv7(), 'IVA_MENSUAL',          'IVA Mensual',                        'MONTHLY',  'IVA', NULL,             NULL),
    (uuidv7(), 'DIOT_MENSUAL',         'DIOT — Operaciones con Terceros',    'MONTHLY',  NULL,  NULL,             NULL),
    (uuidv7(), 'ISR_ANUAL_PM',         'ISR Anual PM',                       'ANNUAL',   'ISR', 'PERSONA_MORAL',  '601'),
    (uuidv7(), 'ISR_ANUAL_PF',         'ISR Anual PF',                       'ANNUAL',   'ISR', 'PERSONA_FISICA', NULL),
    (uuidv7(), 'ISR_MENSUAL_RESICO_PF','ISR Bimestral RESICO PF',           'BIMONTHLY','ISR', 'PERSONA_FISICA', '626'),
    (uuidv7(), 'ISR_MENSUAL_PF_AE',    'ISR Provisional Mensual PF Act. Emp.','MONTHLY', 'ISR', 'PERSONA_FISICA', '612');
