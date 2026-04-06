# Deploy con Supabase + Cloud Run + Cloudflare Pages

Esta guia prepara el stack para:

- `Supabase` para PostgreSQL y almacenamiento S3-compatible
- `Cloud Run` para el backend Spring Boot
- `Cloudflare Pages` para el frontend React/Vite

## 1. Supabase

### Base de datos

1. Crea un proyecto en Supabase.
2. En el dashboard, abre `Connect` y copia la cadena de conexion del `Session pooler`.
3. Convierte la cadena a formato JDBC agregando `jdbc:` al inicio y `?sslmode=require` al final.

Ejemplo:

```text
jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require
```

Variables recomendadas para Cloud Run:

```text
DB_URL=jdbc:postgresql://...pooler.supabase.com:5432/postgres?sslmode=require
DB_USER=postgres.<project-ref>
DB_PASSWORD=<tu-password>
DB_POOL_MAX_SIZE=5
DB_POOL_MIN_IDLE=0
```

### Storage S3-compatible

1. En Supabase Storage habilita S3.
2. Genera un par `Access Key ID` y `Secret Access Key` para uso de servidor.
3. Crea el bucket `contabilidad-attachments` o usa otro nombre y ajusta la variable.

Variables recomendadas:

```text
STORAGE_ENDPOINT=https://<project-ref>.storage.supabase.co/storage/v1/s3
STORAGE_REGION=<project-region>
STORAGE_ACCESS_KEY=<access-key-id>
STORAGE_SECRET_KEY=<secret-access-key>
STORAGE_BUCKET_ATTACHMENTS=contabilidad-attachments
```

## 2. Cloud Run

### APIs y repositorio

```bash
gcloud services enable run.googleapis.com cloudbuild.googleapis.com artifactregistry.googleapis.com
gcloud artifacts repositories create contabilidad --repository-format=docker --location=us-central1
```

### Build de la imagen

Ejecuta esto desde la raiz del repo:

```bash
gcloud builds submit --tag us-central1-docker.pkg.dev/PROJECT_ID/contabilidad/contabilidad-api:latest -f backend/Dockerfile .
```

### Deploy del backend

```bash
gcloud run deploy contabilidad-api \
  --image us-central1-docker.pkg.dev/PROJECT_ID/contabilidad/contabilidad-api:latest \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars "DB_URL=jdbc:postgresql://...pooler.supabase.com:5432/postgres?sslmode=require,DB_USER=postgres.<project-ref>,DB_PASSWORD=<db-password>,DB_POOL_MAX_SIZE=5,DB_POOL_MIN_IDLE=0,JWT_SECRET=<jwt-secret>,STORAGE_ENDPOINT=https://<project-ref>.storage.supabase.co/storage/v1/s3,STORAGE_REGION=<project-region>,STORAGE_ACCESS_KEY=<storage-access-key>,STORAGE_SECRET_KEY=<storage-secret-key>,STORAGE_BUCKET_ATTACHMENTS=contabilidad-attachments,APP_CORS_ALLOWED_ORIGIN_PATTERNS=https://<your-pages-project>.pages.dev,https://*.<your-pages-project>.pages.dev,SAT_MODE=simulated,PAC_MODE=simulated"
```

Despues del deploy, guarda la URL del servicio, por ejemplo:

```text
https://contabilidad-api-xxxxx-uc.a.run.app
```

### Variables recomendadas

- `PORT` no necesita configurarse en Cloud Run; Cloud Run la inyecta automaticamente.
- Usa `JWT_SECRET` largo y aleatorio.
- Para datos sensibles, Cloud Run tambien permite cargar secretos desde `Variables & Secrets`.

## 3. Cloudflare Pages

Configura el proyecto con estos valores:

- Root directory: `frontend`
- Build command: `pnpm build`
- Build output directory: `dist`

Variables de entorno de build:

```text
VITE_API_BASE_URL=https://contabilidad-api-xxxxx-uc.a.run.app/api/v1
```

Si luego conectas un dominio propio, agrega ese dominio en `APP_CORS_ALLOWED_ORIGIN_PATTERNS` del backend.

## 4. Verificacion

1. Abre la URL de Cloud Run y prueba `GET /actuator/health`.
2. Abre la URL de Pages y verifica login.
3. Prueba una subida de archivo para confirmar que Supabase Storage funciona.

## 5. Notas del repo

- El frontend ya acepta `VITE_API_BASE_URL`, asi que no depende del proxy de Vite en produccion.
- El backend ya acepta `DB_URL`, lo que facilita usar Supabase con `sslmode=require`.
- El backend ya acepta `APP_CORS_ALLOWED_ORIGIN_PATTERNS`, util para `*.pages.dev` y dominios propios.
- Los adjuntos ahora usan `STORAGE_BUCKET_ATTACHMENTS` en lugar de un bucket hardcodeado.
