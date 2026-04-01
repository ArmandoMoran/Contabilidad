-- ============================================================
-- V13: Seed dev contacts and addresses for clients
-- ============================================================

-- ── Contacts for Client 1 (Distribuidora Norte) ──
INSERT INTO contacts (id, company_id, party_type, party_id, full_name, email, phone, position, is_primary)
VALUES
  ('cc111111-1111-7111-8111-111111111101', '11111111-1111-7111-8111-111111111111', 'CLIENT', '33333333-3333-7333-8333-333333333301',
   'Roberto Martínez López', 'roberto.martinez@disnorte.mx', '5551001001', 'Director de Compras', true),
  ('cc111111-1111-7111-8111-111111111102', '11111111-1111-7111-8111-111111111111', 'CLIENT', '33333333-3333-7333-8333-333333333301',
   'Laura Sánchez Ruiz', 'laura.sanchez@disnorte.mx', '5551001002', 'Contador', false);

-- ── Contacts for Client 2 (Logística Central) ──
INSERT INTO contacts (id, company_id, party_type, party_id, full_name, email, phone, position, is_primary)
VALUES
  ('cc111111-1111-7111-8111-111111111103', '11111111-1111-7111-8111-111111111111', 'CLIENT', '33333333-3333-7333-8333-333333333302',
   'Ana María Flores Vega', 'ana.flores@logcentral.mx', '5552002001', 'Gerente de Finanzas', true);

-- ── Contact for Client 3 (Carlos García) ──
INSERT INTO contacts (id, company_id, party_type, party_id, full_name, email, phone, position, is_primary)
VALUES
  ('cc111111-1111-7111-8111-111111111104', '11111111-1111-7111-8111-111111111111', 'CLIENT', '33333333-3333-7333-8333-333333333303',
   'Carlos García López', 'carlos@gmail.com', '5551234003', NULL, true);

-- ── Addresses for Client 1 (Distribuidora Norte) ──
INSERT INTO addresses (id, company_id, party_type, party_id, address_type, street1, exterior_number, neighborhood, city, state_code, postal_code, country_code, is_primary, created_by, updated_by)
VALUES
  ('aa111111-1111-7111-8111-111111111101', '11111111-1111-7111-8111-111111111111', 'CLIENT', '33333333-3333-7333-8333-333333333301',
   'FISCAL', 'Av. Revolución', '1500', 'San Ángel', 'Ciudad de México', 'CMX', '01000', 'MEX', true, NULL, NULL),
  ('aa111111-1111-7111-8111-111111111102', '11111111-1111-7111-8111-111111111111', 'CLIENT', '33333333-3333-7333-8333-333333333301',
   'DELIVERY', 'Calzada de Tlalpan', '4200', 'Huipulco', 'Ciudad de México', 'CMX', '14370', 'MEX', false, NULL, NULL);

-- ── Address for Client 2 (Logística Central) ──
INSERT INTO addresses (id, company_id, party_type, party_id, address_type, street1, exterior_number, interior_number, neighborhood, city, state_code, postal_code, country_code, is_primary, created_by, updated_by)
VALUES
  ('aa111111-1111-7111-8111-111111111103', '11111111-1111-7111-8111-111111111111', 'CLIENT', '33333333-3333-7333-8333-333333333302',
   'FISCAL', 'Blvd. Manuel Ávila Camacho', '36', 'Piso 10', 'Lomas de Chapultepec', 'Ciudad de México', 'CMX', '11000', 'MEX', true, NULL, NULL);

-- ── Address for Client 3 (Carlos García) ──
INSERT INTO addresses (id, company_id, party_type, party_id, address_type, street1, exterior_number, neighborhood, city, state_code, postal_code, country_code, is_primary, created_by, updated_by)
VALUES
  ('aa111111-1111-7111-8111-111111111104', '11111111-1111-7111-8111-111111111111', 'CLIENT', '33333333-3333-7333-8333-333333333303',
   'FISCAL', 'Calle Durango', '250', 'Roma Norte', 'Ciudad de México', 'CMX', '06700', 'MEX', true, NULL, NULL);
