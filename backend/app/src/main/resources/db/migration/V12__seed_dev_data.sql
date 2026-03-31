-- ============================================================
-- V12: Seed dev/test data — company, user, clients, suppliers,
--      products, invoices, expenses, payments
-- ============================================================

-- ── Dev Company ──
INSERT INTO companies (id, rfc, legal_name, taxpayer_type, fiscal_regime_code, postal_code, active)
VALUES ('11111111-1111-7111-8111-111111111111', 'EMP010101AAA', 'Empresa Demo S.A. de C.V.', 'PERSONA_MORAL', '601', '06600', true);

-- ── Dev User (password: demo1234 — bcrypt) ──
INSERT INTO users (id, company_id, email, password_hash, full_name, role, active)
VALUES ('22222222-2222-7222-8222-222222222222', '11111111-1111-7111-8111-111111111111',
        'admin@demo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Admin Demo', 'admin', true);

-- ── Clients (3) ──
INSERT INTO clients (id, company_id, rfc, legal_name, trade_name, email, phone, fiscal_regime_code, default_uso_cfdi_code, default_forma_pago_code, active)
VALUES
  ('33333333-3333-7333-8333-333333333301', '11111111-1111-7111-8111-111111111111', 'AAA010101AA1', 'Distribuidora Norte S.A. de C.V.', 'DisNorte', 'ventas@disnorte.mx', '5551234001', '601', 'G03', '03', true),
  ('33333333-3333-7333-8333-333333333302', '11111111-1111-7111-8111-111111111111', 'BBB020202BB2', 'Logística Central S. de R.L.', 'LogCentral', 'contacto@logcentral.mx', '5551234002', '601', 'G01', '03', true),
  ('33333333-3333-7333-8333-333333333303', '11111111-1111-7111-8111-111111111111', 'CCC030303CC3', 'Carlos García López', NULL, 'carlos@gmail.com', '5551234003', '626', 'S01', '01', true);

-- ── Suppliers (3) ──
INSERT INTO suppliers (id, company_id, rfc, legal_name, trade_name, email, phone, fiscal_regime_code, default_forma_pago_code, nationality, diot_operation_type, active)
VALUES
  ('44444444-4444-7444-8444-444444444401', '11111111-1111-7111-8111-111111111111', 'SUP010101AA1', 'Proveedora Industrial S.A. de C.V.', 'ProvInd', 'ventas@provind.mx', '5559876001', '601', '03', 'NATIONAL', '85', true),
  ('44444444-4444-7444-8444-444444444402', '11111111-1111-7111-8111-111111111111', 'SUP020202BB2', 'Servicios Digitales Express S.A.', 'ServDigital', 'info@servdigital.mx', '5559876002', '601', '03', 'NATIONAL', '85', true),
  ('44444444-4444-7444-8444-444444444403', '11111111-1111-7111-8111-111111111111', 'SUP030303CC3', 'María Fernanda Ruiz Hernández', NULL, 'maria.ruiz@freelance.mx', '5559876003', '626', '01', 'NATIONAL', '03', true);

-- ── Products (3) ──
INSERT INTO products (id, company_id, internal_code, internal_name, description, sat_product_code, sat_unit_code, unit_price, currency_code, objeto_imp_code, active)
VALUES
  ('55555555-5555-7555-8555-555555555501', '11111111-1111-7111-8111-111111111111', 'SERV-001', 'Consultoría Fiscal', 'Servicio de consultoría fiscal y contable mensual', '80101500', 'E48', 15000.00, 'MXN', '02', true),
  ('55555555-5555-7555-8555-555555555502', '11111111-1111-7111-8111-111111111111', 'SERV-002', 'Desarrollo de Software', 'Desarrollo de software a medida por hora', '81112100', 'E48', 850.00, 'MXN', '02', true),
  ('55555555-5555-7555-8555-555555555503', '11111111-1111-7111-8111-111111111111', 'PROD-001', 'Licencia Software Anual', 'Licencia anual de software contable', '81112000', 'E48', 24000.00, 'MXN', '02', true);

-- ── Product Tax Profiles (IVA 16% transfer for each product) ──
INSERT INTO product_tax_profiles (id, product_id, company_id, tax_code, factor_type, rate, is_transfer, is_withholding, valid_from, active)
VALUES
  ('55555555-5555-7555-8555-aaaaaaaaa501', '55555555-5555-7555-8555-555555555501', '11111111-1111-7111-8111-111111111111', '002', 'Tasa', 0.160000, true, false, '2024-01-01', true),
  ('55555555-5555-7555-8555-aaaaaaaaa502', '55555555-5555-7555-8555-555555555502', '11111111-1111-7111-8111-111111111111', '002', 'Tasa', 0.160000, true, false, '2024-01-01', true),
  ('55555555-5555-7555-8555-aaaaaaaaa503', '55555555-5555-7555-8555-555555555503', '11111111-1111-7111-8111-111111111111', '002', 'Tasa', 0.160000, true, false, '2024-01-01', true);

-- ── Invoices (3 drafts) ──
INSERT INTO invoices (id, company_id, client_id, invoice_type, status, series, folio, currency_code, payment_method_code, payment_form_code, uso_cfdi_code,
                      issuer_rfc, issuer_name, issuer_regime_code,
                      receiver_rfc, receiver_name, receiver_regime_code, receiver_postal_code,
                      subtotal, transferred_tax_total, total)
VALUES
  ('66666666-6666-7666-8666-666666666601', '11111111-1111-7111-8111-111111111111', '33333333-3333-7333-8333-333333333301',
   'I', 'DRAFT', 'A', '001', 'MXN', 'PUE', '03', 'G03',
   'EMP010101AAA', 'Empresa Demo S.A. de C.V.', '601',
   'AAA010101AA1', 'Distribuidora Norte S.A. de C.V.', '601', '06600',
   15000.00, 2400.00, 17400.00),
  ('66666666-6666-7666-8666-666666666602', '11111111-1111-7111-8111-111111111111', '33333333-3333-7333-8333-333333333302',
   'I', 'DRAFT', 'A', '002', 'MXN', 'PPD', '03', 'G01',
   'EMP010101AAA', 'Empresa Demo S.A. de C.V.', '601',
   'BBB020202BB2', 'Logística Central S. de R.L.', '601', '06600',
   34000.00, 5440.00, 39440.00),
  ('66666666-6666-7666-8666-666666666603', '11111111-1111-7111-8111-111111111111', '33333333-3333-7333-8333-333333333303',
   'I', 'DRAFT', 'A', '003', 'MXN', 'PUE', '01', 'S01',
   'EMP010101AAA', 'Empresa Demo S.A. de C.V.', '601',
   'CCC030303CC3', 'Carlos García López', '626', '06600',
   850.00, 136.00, 986.00);

-- ── Invoice Lines ──
INSERT INTO invoice_lines (id, invoice_id, company_id, line_number, product_id, sat_product_code, description, sat_unit_code, quantity, unit_price, discount, subtotal, objeto_imp_code)
VALUES
  ('66666666-6666-7666-8666-aaaaaaaaa601', '66666666-6666-7666-8666-666666666601', '11111111-1111-7111-8111-111111111111', 1, '55555555-5555-7555-8555-555555555501', '80101500', 'Consultoría Fiscal - Enero 2026', 'E48', 1.000000, 15000.000000, 0.00, 15000.00, '02'),
  ('66666666-6666-7666-8666-aaaaaaaaa602', '66666666-6666-7666-8666-666666666602', '11111111-1111-7111-8111-111111111111', 1, '55555555-5555-7555-8555-555555555502', '81112100', 'Desarrollo de Software - 40 hrs', 'E48', 40.000000, 850.000000, 0.00, 34000.00, '02'),
  ('66666666-6666-7666-8666-aaaaaaaaa603', '66666666-6666-7666-8666-666666666603', '11111111-1111-7111-8111-111111111111', 1, '55555555-5555-7555-8555-555555555502', '81112100', 'Hora consultoría técnica', 'E48', 1.000000, 850.000000, 0.00, 850.00, '02');

-- ── Expenses (3) ──
INSERT INTO expenses (id, company_id, supplier_id, expense_type, status, issuer_rfc, issuer_name, currency_code, subtotal, total, category, deductible, notes)
VALUES
  ('77777777-7777-7777-8777-777777777701', '11111111-1111-7111-8111-111111111111', '44444444-4444-7444-8444-444444444401',
   'CFDI_RECEIVED', 'PENDING', 'SUP010101AA1', 'Proveedora Industrial S.A. de C.V.', 'MXN', 8620.69, 10000.00, 'MATERIALES', true, 'Compra de materiales de oficina'),
  ('77777777-7777-7777-8777-777777777702', '11111111-1111-7111-8111-111111111111', '44444444-4444-7444-8444-444444444402',
   'CFDI_RECEIVED', 'PENDING', 'SUP020202BB2', 'Servicios Digitales Express S.A.', 'MXN', 4310.34, 5000.00, 'SOFTWARE', true, 'Hosting y servicios cloud mensual'),
  ('77777777-7777-7777-8777-777777777703', '11111111-1111-7111-8111-111111111111', NULL,
   'MANUAL', 'PENDING', NULL, NULL, 'MXN', 1500.00, 1500.00, 'TRANSPORTE', true, 'Viáticos y gasolina Q1');

-- ── Payments (3) ──
INSERT INTO payments (id, company_id, payment_direction, status, payment_form_code, currency_code, amount, paid_at, operation_number, payer_rfc, payer_name, notes)
VALUES
  ('88888888-8888-7888-8888-888888888801', '11111111-1111-7111-8111-111111111111', 'INBOUND', 'REGISTERED', '03', 'MXN', 17400.00, '2026-01-15T12:00:00Z', 'OP-2026-001', 'AAA010101AA1', 'Distribuidora Norte S.A. de C.V.', 'Cobro factura A-001'),
  ('88888888-8888-7888-8888-888888888802', '11111111-1111-7111-8111-111111111111', 'OUTBOUND', 'REGISTERED', '03', 'MXN', 10000.00, '2026-01-20T12:00:00Z', 'OP-2026-002', NULL, NULL, 'Pago a Proveedora Industrial por materiales'),
  ('88888888-8888-7888-8888-888888888803', '11111111-1111-7111-8111-111111111111', 'INBOUND', 'REGISTERED', '04', 'MXN', 986.00, '2026-02-01T12:00:00Z', 'OP-2026-003', 'CCC030303CC3', 'Carlos García López', 'Cobro factura A-003 con tarjeta');
