# Contabilidad

SaaS de contabilidad y facturación electrónica para México. Modular monolith multi-empresa con soporte CFDI 4.0, REP 2.0, catálogos SAT versionados, motor fiscal configurable y preparación para contabilidad electrónica.

## Tech Stack

| Capa | Tecnología |
|------|------------|
| Backend | Spring Boot 4.0.x · Java 25 · Maven |
| Frontend | React 19 · Vite 8 · TypeScript · pnpm |
| Base de datos | PostgreSQL 18 · Flyway |
| Almacenamiento | S3/MinIO (XML, PDF, acuses) |
| Infraestructura local | Docker Compose |

## Estructura del monorepo

```
backend/          → Spring Boot modular monolith
frontend/         → React SPA (español)
infra/compose/    → Docker Compose para desarrollo
openapi/          → Especificaciones OpenAPI 3.1
docs/             → Documentación del proyecto
e2e/              → Pruebas end-to-end (Playwright)
```

## Inicio rápido

```bash
# Levantar infraestructura local
docker compose -f infra/compose/docker-compose.yml up -d

# Backend
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Frontend
cd frontend
pnpm install
pnpm dev
```

## Módulos funcionales

- **identity-access** — Autenticación, roles y permisos
- **company-profile** — Perfil de empresa y configuración fiscal
- **sat-catalogs** — Catálogos SAT versionados y sincronización
- **parties** — Clientes y proveedores
- **products** — Productos y servicios con perfil fiscal
- **invoicing-cfdi** — Facturación CFDI 4.0, notas de crédito
- **expenses** — Gastos y CFDI recibidos
- **payments** — Pagos, cobros y REP 2.0
- **tax-engine** — Motor de impuestos y reglas fiscales
- **declarations** — Papeles de trabajo y declaraciones
- **reporting** — Reportes operativos y fiscales
- **attachments** — Almacenamiento de archivos
- **audit** — Bitácora de auditoría
- **integration-sat** — Validación, recuperación y sync SAT
- **integration-pac** — Abstracción de PAC para timbrado/cancelación

## Licencia

Privado — Todos los derechos reservados.
