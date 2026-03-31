-- ============================================================
-- V11: Attachments, outbox, SAT recovery
-- ============================================================

-- ── Attachments ──
CREATE TABLE attachments (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id      uuid         NOT NULL REFERENCES companies(id),
    entity_type     varchar(60)  NOT NULL,
    entity_id       uuid         NOT NULL,
    file_name       varchar(255) NOT NULL,
    content_type    varchar(100) NOT NULL,
    file_size       bigint       NOT NULL,
    object_key      varchar(500) NOT NULL,
    checksum_sha256 varchar(64),
    uploaded_by     uuid,
    created_at      timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_attachments_entity ON attachments(entity_type, entity_id);

-- ── Outbox (for async events) ──
CREATE TABLE outbox_events (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id      uuid,
    aggregate_type  varchar(60)  NOT NULL,
    aggregate_id    uuid         NOT NULL,
    event_type      varchar(80)  NOT NULL,
    payload         jsonb        NOT NULL,
    status          varchar(20)  NOT NULL DEFAULT 'PENDING',
    retry_count     int          NOT NULL DEFAULT 0,
    max_retries     int          NOT NULL DEFAULT 5,
    next_retry_at   timestamptz,
    processed_at    timestamptz,
    error_message   text,
    created_at      timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_outbox_pending ON outbox_events(status, next_retry_at) WHERE status IN ('PENDING','RETRYING');

-- ── SAT Recovery Runs ──
CREATE TABLE sat_recovery_runs (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    company_id      uuid         NOT NULL REFERENCES companies(id),
    recovery_type   varchar(30)  NOT NULL CHECK (recovery_type IN ('METADATA','XML','BOTH')),
    date_from       date         NOT NULL,
    date_to         date         NOT NULL,
    direction       varchar(10)  NOT NULL CHECK (direction IN ('ISSUED','RECEIVED')),
    status          varchar(20)  NOT NULL DEFAULT 'RUNNING',
    total_found     int          NOT NULL DEFAULT 0,
    total_downloaded int         NOT NULL DEFAULT 0,
    total_new       int          NOT NULL DEFAULT 0,
    error_message   text,
    started_at      timestamptz  NOT NULL DEFAULT now(),
    finished_at     timestamptz,
    triggered_by    uuid
);
CREATE INDEX idx_sat_recovery_company ON sat_recovery_runs(company_id, started_at DESC);

-- ── SAT Recovery Items ──
CREATE TABLE sat_recovery_items (
    id              uuid PRIMARY KEY DEFAULT uuidv7(),
    recovery_run_id uuid         NOT NULL REFERENCES sat_recovery_runs(id) ON DELETE CASCADE,
    company_id      uuid         NOT NULL REFERENCES companies(id),
    cfdi_uuid       uuid         NOT NULL,
    issuer_rfc      varchar(13),
    receiver_rfc    varchar(13),
    amount          numeric(18,2),
    issued_at       timestamptz,
    cfdi_type       varchar(1),
    status          varchar(20)  NOT NULL DEFAULT 'FOUND',
    xml_object_key  varchar(500),
    matched_entity_type varchar(20),
    matched_entity_id   uuid,
    created_at      timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX idx_sat_recovery_items_run ON sat_recovery_items(recovery_run_id);
CREATE INDEX idx_sat_recovery_items_uuid ON sat_recovery_items(company_id, cfdi_uuid);

-- ─── Seed SAT catalog data ───

-- Régimen Fiscal (starter)
INSERT INTO sat_regimen_fiscal (id, code, description, applies_pf, applies_pm) VALUES
    (uuidv7(), '601', 'General de Ley Personas Morales', false, true),
    (uuidv7(), '603', 'Personas Morales con Fines no Lucrativos', false, true),
    (uuidv7(), '605', 'Sueldos y Salarios e Ingresos Asimilados a Salarios', true, false),
    (uuidv7(), '606', 'Arrendamiento', true, false),
    (uuidv7(), '608', 'Demás ingresos', true, false),
    (uuidv7(), '610', 'Residentes en el Extranjero sin Establecimiento Permanente en México', true, true),
    (uuidv7(), '611', 'Ingresos por Dividendos (socios y accionistas)', true, false),
    (uuidv7(), '612', 'Personas Físicas con Actividades Empresariales y Profesionales', true, false),
    (uuidv7(), '614', 'Ingresos por intereses', true, false),
    (uuidv7(), '615', 'Régimen de los ingresos por obtención de premios', true, false),
    (uuidv7(), '616', 'Sin obligaciones fiscales', true, false),
    (uuidv7(), '620', 'Sociedades Cooperativas de Producción que optan por diferir sus ingresos', false, true),
    (uuidv7(), '621', 'Incorporación Fiscal', true, false),
    (uuidv7(), '622', 'Actividades Agrícolas, Ganaderas, Silvícolas y Pesqueras', true, true),
    (uuidv7(), '623', 'Opcional para Grupos de Sociedades', false, true),
    (uuidv7(), '624', 'Coordinados', false, true),
    (uuidv7(), '625', 'Régimen de las Actividades Empresariales con ingresos a través de Plataformas Tecnológicas', true, false),
    (uuidv7(), '626', 'Régimen Simplificado de Confianza', true, true);

-- Forma de Pago (starter)
INSERT INTO sat_forma_pago (id, code, description, banked) VALUES
    (uuidv7(), '01', 'Efectivo', false),
    (uuidv7(), '02', 'Cheque nominativo', true),
    (uuidv7(), '03', 'Transferencia electrónica de fondos', true),
    (uuidv7(), '04', 'Tarjeta de crédito', true),
    (uuidv7(), '05', 'Monedero electrónico', true),
    (uuidv7(), '06', 'Dinero electrónico', true),
    (uuidv7(), '08', 'Vales de despensa', false),
    (uuidv7(), '12', 'Dación en pago', false),
    (uuidv7(), '13', 'Pago por subrogación', false),
    (uuidv7(), '14', 'Pago por consignación', false),
    (uuidv7(), '15', 'Condonación', false),
    (uuidv7(), '17', 'Compensación', false),
    (uuidv7(), '23', 'Novación', false),
    (uuidv7(), '24', 'Confusión', false),
    (uuidv7(), '25', 'Remisión de deuda', false),
    (uuidv7(), '26', 'Prescripción o caducidad', false),
    (uuidv7(), '27', 'A satisfacción del acreedor', false),
    (uuidv7(), '28', 'Tarjeta de débito', true),
    (uuidv7(), '29', 'Tarjeta de servicios', true),
    (uuidv7(), '30', 'Aplicación de anticipos', false),
    (uuidv7(), '31', 'Intermediario pagos', true),
    (uuidv7(), '99', 'Por definir', false);

-- Método de Pago
INSERT INTO sat_metodo_pago (id, code, description) VALUES
    (uuidv7(), 'PUE', 'Pago en una sola exhibición'),
    (uuidv7(), 'PPD', 'Pago en parcialidades o diferido');

-- Uso CFDI (starter)
INSERT INTO sat_uso_cfdi (id, code, description, applies_pf, applies_pm) VALUES
    (uuidv7(), 'G01', 'Adquisición de mercancías', true, true),
    (uuidv7(), 'G02', 'Devoluciones, descuentos o bonificaciones', true, true),
    (uuidv7(), 'G03', 'Gastos en general', true, true),
    (uuidv7(), 'I01', 'Construcciones', true, true),
    (uuidv7(), 'I02', 'Mobiliario y equipo de oficina por inversiones', true, true),
    (uuidv7(), 'I03', 'Equipo de transporte', true, true),
    (uuidv7(), 'I04', 'Equipo de computo y accesorios', true, true),
    (uuidv7(), 'I05', 'Dados, troqueles, moldes, matrices y herramental', true, true),
    (uuidv7(), 'I06', 'Comunicaciones telefónicas', true, true),
    (uuidv7(), 'I07', 'Comunicaciones satelitales', true, true),
    (uuidv7(), 'I08', 'Otra maquinaria y equipo', true, true),
    (uuidv7(), 'D01', 'Honorarios médicos, dentales y gastos hospitalarios', true, false),
    (uuidv7(), 'D02', 'Gastos médicos por incapacidad o discapacidad', true, false),
    (uuidv7(), 'D03', 'Gastos funerales', true, false),
    (uuidv7(), 'D04', 'Donativos', true, false),
    (uuidv7(), 'D05', 'Intereses reales efectivamente pagados por créditos hipotecarios (casa habitación)', true, false),
    (uuidv7(), 'D06', 'Aportaciones voluntarias al SAR', true, false),
    (uuidv7(), 'D07', 'Primas por seguros de gastos médicos', true, false),
    (uuidv7(), 'D08', 'Gastos de transportación escolar obligatoria', true, false),
    (uuidv7(), 'D09', 'Depósitos en cuentas para el ahorro, primas que tengan como base planes de pensiones', true, false),
    (uuidv7(), 'D10', 'Pagos por servicios educativos (colegiaturas)', true, false),
    (uuidv7(), 'S01', 'Sin efectos fiscales', true, true),
    (uuidv7(), 'CP01', 'Pagos', true, true),
    (uuidv7(), 'CN01', 'Nómina', true, false);

-- Tipo Comprobante
INSERT INTO sat_tipo_comprobante (id, code, description) VALUES
    (uuidv7(), 'I', 'Ingreso'),
    (uuidv7(), 'E', 'Egreso'),
    (uuidv7(), 'T', 'Traslado'),
    (uuidv7(), 'N', 'Nómina'),
    (uuidv7(), 'P', 'Pago');

-- Impuesto
INSERT INTO sat_impuesto (id, code, description) VALUES
    (uuidv7(), '001', 'ISR'),
    (uuidv7(), '002', 'IVA'),
    (uuidv7(), '003', 'IEPS');

-- Tipo Factor
INSERT INTO sat_tipo_factor (id, code, description) VALUES
    (uuidv7(), 'Tasa', 'Tasa'),
    (uuidv7(), 'Cuota', 'Cuota'),
    (uuidv7(), 'Exento', 'Exento');

-- Objeto de Impuesto
INSERT INTO sat_objeto_imp (id, code, description) VALUES
    (uuidv7(), '01', 'No objeto de impuesto'),
    (uuidv7(), '02', 'Sí objeto de impuesto'),
    (uuidv7(), '03', 'Sí objeto del impuesto y no obligado al desglose'),
    (uuidv7(), '04', 'Sí objeto del impuesto y no causa impuesto');

-- Motivo de Cancelación
INSERT INTO sat_motivo_cancelacion (id, code, description, requires_replacement) VALUES
    (uuidv7(), '01', 'Comprobantes emitidos con errores con relación', true),
    (uuidv7(), '02', 'Comprobantes emitidos con errores sin relación', false),
    (uuidv7(), '03', 'No se llevó a cabo la operación', false),
    (uuidv7(), '04', 'Operación nominativa relacionada en una factura global', false);

-- Moneda (starter)
INSERT INTO sat_moneda (id, code, description, decimals) VALUES
    (uuidv7(), 'MXN', 'Peso Mexicano', 2),
    (uuidv7(), 'USD', 'Dólar americano', 2),
    (uuidv7(), 'EUR', 'Euro', 2),
    (uuidv7(), 'XXX', 'Los códigos asignados para transacciones en que intervenga ninguna moneda', 0);

-- Tipo de Relación (starter)
INSERT INTO sat_tipo_relacion (id, code, description) VALUES
    (uuidv7(), '01', 'Nota de crédito de los documentos relacionados'),
    (uuidv7(), '02', 'Nota de débito de los documentos relacionados'),
    (uuidv7(), '03', 'Devolución de mercancía sobre facturas o traslados previos'),
    (uuidv7(), '04', 'Sustitución de los CFDI previos'),
    (uuidv7(), '05', 'Traslados de mercancías facturados previamente'),
    (uuidv7(), '06', 'Factura generada por los traslados previos'),
    (uuidv7(), '07', 'CFDI por aplicación de anticipo');
