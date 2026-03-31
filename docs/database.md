# Modelo de Base de Datos

## Diagrama ER Conceptual

```
companies ──┬── users
            ├── clients ──── contacts, addresses
            ├── suppliers ── contacts, addresses
            ├── products ─── product_tax_profiles
            ├── invoices ─── invoice_lines ─── tax_lines
            ├── expenses ─── expense_lines ─── tax_lines
            ├── payments ─── payment_applications
            ├── declaration_runs ── declaration_lines, diot_lines
            ├── journal_entries ─── journal_lines
            └── account_catalog ─── account_mapping_rules
```

## Convenciones

- **PK**: UUID v7 (time-ordered) vía función `uuidv7()`
- **Multi-empresa**: Toda tabla transaccional incluye `company_id`
- **Audit columns**: `created_at`, `created_by`, `updated_at`, `updated_by`, `row_version`
- **Soft delete**: `deleted_at`, `deleted_by` en maestros (clients, suppliers, products)
- **Inmutabilidad**: CFDI, cancelaciones, declaraciones cerradas → no tienen soft delete
- **Normalización**: 3NF para core transaccional
- **JSONB**: Solo para snapshots fiscales y payloads externos
- **Índices**: Parciales con `WHERE deleted_at IS NULL`, compuestos para queries frecuentes

## Tablas Principales

### Foundation (V1)
- `companies` — Empresas registradas
- `users` — Usuarios con rol y company
- `permissions` — Permisos granulares
- `role_permissions` — Asignación rol → permisos
- `refresh_tokens` — Tokens de refresco
- `audit_log` — Bitácora de auditoría (append-only)

### SAT Catalogs (V2)
- `sat_regimen_fiscal`, `sat_uso_cfdi`, `sat_forma_pago`, `sat_metodo_pago`
- `sat_moneda`, `sat_impuesto`, `sat_tipo_factor`, `sat_tasa_o_cuota`
- `sat_clave_prod_serv`, `sat_clave_unidad`, `sat_codigo_postal`
- `sat_estado`, `sat_municipio`, `sat_localidad`, `sat_pais`
- `sat_objeto_imp`, `sat_tipo_comprobante`, `sat_motivo_cancelacion`, `sat_tipo_relacion`
- `sat_uso_cfdi_regimen` — Matriz de validación uso CFDI × régimen
- `catalog_sync_runs` — Historial de sincronización

### Parties (V3)
- `clients` — Clientes con defaults fiscales
- `suppliers` — Proveedores con clasificación DIOT
- `addresses` — Direcciones compartidas (polimórfico)
- `contacts` — Contactos compartidos (polimórfico)

### Products (V4)
- `products` — Productos con código SAT y unidad
- `product_tax_profiles` — Perfiles de impuestos versionados por fecha

### Invoicing (V5)
- `invoices` — CFDI 4.0 con snapshot fiscal inmutable
- `invoice_lines` — Líneas de factura
- `tax_lines` — Fact table de impuestos (compartida entre invoices, expenses, payments)
- `invoice_related_documents` — Documentos relacionados

### Expenses (V6)
- `expenses` — Gastos y CFDI recibidos
- `expense_lines` — Líneas de gasto

### Payments (V7)
- `payments` — Cobros y pagos
- `payment_applications` — Aplicación contra documentos

### Tax Engine (V8)
- `tax_rules` — Reglas fiscales configurables
- `zone_eligibility_rules` — Estímulo fronterizo IVA 8%
- `obligation_rules` — Obligaciones por régimen/persona/periodo
- `declaration_templates` — Plantillas de declaración

### Declarations (V9)
- `declaration_runs` — Ejecuciones de declaración con snapshot
- `declaration_lines` — Líneas con trazabilidad a documento fuente
- `diot_lines` — Líneas DIOT por proveedor

### Accounting (V10)
- `account_catalog` — Catálogo de cuentas
- `journal_entries` — Pólizas contables
- `journal_lines` — Movimientos de póliza
- `account_mapping_rules` — Reglas de auto-posteo

### Infrastructure (V11)
- `attachments` — Archivos adjuntos con checksum
- `outbox_events` — Cola de eventos asíncronos
- `sat_recovery_runs` — Ejecuciones de recuperación SAT
- `sat_recovery_items` — Items recuperados del SAT
