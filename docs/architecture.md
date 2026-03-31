# Arquitectura del Sistema

## Visión General

Contabilidad es un **modular monolith** SaaS-ready para contabilidad y facturación electrónica en México.

```
┌─────────────────────────────────────────────────────────────────┐
│                    React 19 SPA (Español)                       │
│  Vite 8 · TypeScript · TanStack Query · React Hook Form · Zod  │
└──────────────────────────────┬──────────────────────────────────┘
                               │ REST JSON
┌──────────────────────────────┴──────────────────────────────────┐
│                   Spring Boot 4.0.x API                         │
│                      Java 25 · Maven                            │
│                                                                 │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐    │
│  │identity-access│ │company-profile│ │   sat-catalogs      │    │
│  └──────────────┘ └──────────────┘ └──────────────────────┘    │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐    │
│  │   parties     │ │  products    │ │  invoicing-cfdi      │    │
│  └──────────────┘ └──────────────┘ └──────────────────────┘    │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐    │
│  │  expenses     │ │  payments    │ │    tax-engine        │    │
│  └──────────────┘ └──────────────┘ └──────────────────────┘    │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐    │
│  │ declarations  │ │  reporting   │ │   attachments        │    │
│  └──────────────┘ └──────────────┘ └──────────────────────┘    │
│  ┌──────────────┐ ┌────────────────┐ ┌────────────────────┐    │
│  │    audit      │ │integration-sat │ │ integration-pac    │    │
│  └──────────────┘ └────────────────┘ └────────────────────┘    │
│                         shared-kernel                           │
└─────────────────────────────┬───────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────┴─────┐     ┌────────┴────────┐    ┌──────┴──────┐
│PostgreSQL 18│     │  S3/MinIO       │    │ PAC/SAT     │
│  Flyway     │     │XML·PDF·Acuses   │    │ Adapters    │
└─────────────┘     └─────────────────┘    └─────────────┘
```

## Principios de Diseño

1. **Multi-empresa desde día 1** — Toda tabla transaccional lleva `company_id`
2. **Fiscal separado** — Defaults de maestro ≠ valores transaccionales
3. **Catálogos versionados** — SAT catalogs con `valid_from/valid_to`, nunca constantes
4. **PAC agnostic** — Abstracción completa vía `PacClient` interface
5. **Outbox pattern** — Procesos asíncronos vía tabla `outbox_events`
6. **Auditoría completa** — Toda acción fiscal relevante en `audit_log`
7. **Soft delete** — En catálogos maestros; documentos fiscales inmutables

## Módulos

| Módulo | Responsabilidad |
|--------|----------------|
| `shared-kernel` | Base entities, validators, DTOs comunes |
| `identity-access` | JWT auth, roles, permisos, security config |
| `company-profile` | Perfil de empresa, configuración fiscal |
| `sat-catalogs` | Catálogos SAT versionados, sync, validación |
| `parties` | Clientes + proveedores + contactos + direcciones |
| `products` | Productos con código SAT y perfil fiscal |
| `invoicing-cfdi` | CFDI 4.0, líneas, impuestos, timbrado, cancelación |
| `expenses` | Gastos manuales e importación XML |
| `payments` | Cobros, pagos, REP 2.0, aplicaciones |
| `tax-engine` | Reglas fiscales, cálculo de impuestos, obligaciones |
| `declarations` | Papeles de trabajo, declaraciones, DIOT |
| `reporting` | Reportes operativos y fiscales |
| `attachments` | Almacenamiento S3/MinIO |
| `audit` | Bitácora de auditoría |
| `integration-sat` | Validación y recuperación SAT |
| `integration-pac` | Abstracción de PAC |

## Profiles

| Perfil | Uso |
|--------|-----|
| `local` | Desarrollo: PAC/SAT simulados (WireMock), PostgreSQL local |
| `test` | CI/CD: Testcontainers, mocks |
| `staging` | Pre-producción: PAC sandbox |
| `prod` | Producción: PAC real, KMS para certificados |
