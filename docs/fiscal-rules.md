# Reglas Fiscales — Motor de Impuestos

## Principio Fundamental

> Los defaults de maestro son sugerencias editables. La verdad fiscal vive en el backend, en catálogos y reglas versionadas.

## IVA — Reglas de Traslado

| Regla | Tasa | Condición | Notas |
|-------|------|-----------|-------|
| IVA general | 16% | Tasa general para todo México | Default |
| IVA tasa cero | 0% | Actos gravados a tasa 0 | No equivale a exento |
| IVA exento | N/A | Sin traslado causado | Impacta `ObjetoImp` y reportes diferente a 0% |
| IVA fronterizo | 8% | Estímulo fiscal región fronteriza | Solo con `zone_eligibility_rule`, no como tasa global |

## IVA — Reglas de Retención

| Regla | Tasa | Condición | Base legal |
|-------|------|-----------|------------|
| Ret. 2/3 IVA | 10.6667% | PF servicios profesionales/comisiones a PM | RLIVA art. 3 |
| Ret. autotransporte | 4% | Autotransporte terrestre de bienes | RLIVA art. 3 |
| Ret. histórica | 6% | Art. 1-A fr. IV (derogado) | `inactive_by_default` |
| Ret. fronteriza 2/3 | 5.3333% | 2/3 de 8% cuando aplique fronterizo | Requiere validación fiscal |

## ISR — Reglas de Retención

| Regla | Tasa | Condición | Notas |
|-------|------|-----------|-------|
| ISR honorarios/arrendamiento | 10% | PF por honorarios o arrendamiento | Solo combinaciones válidas |
| ISR RESICO PF | 1.25% | RESICO PF cuando proceda | Bloquear fuera de perfiles autorizados |

## Zona Fronteriza (IVA 8%)

Modelado con `zone_eligibility_rules`:
- Se evalúa por código postal, municipio y estado
- Solo aplica si el contribuyente, la operación y la calificación regional coinciden
- NO es una tasa global alternativa

## Cancelación CFDI

Desde 1 de enero de 2022:
- Motivo obligatorio (claves `01`, `02`, `03`, `04`)
- `01`: Requiere UUID de CFDI sustituto
- Aceptación del receptor cuando aplique
- Supuestos de cancelación sin aceptación según SAT

## REP 2.0

- Si factura origen es `PPD`, el sistema DEBE emitir CFDI tipo `P` con complemento de pagos
- No sustituir con simple cambio de estatus de cobro
- Módulo separado de Retenciones e Información de Pagos 2.0

## Validaciones Bloqueantes

1. RFC sintáctico (12 PM, 13 PF, genéricos permitidos)
2. Combinación `uso_cfdi` × `régimen_fiscal` del receptor
3. `forma_pago` × `método_pago` válida
4. Importes no negativos
5. Total = subtotal - descuento + traslados - retenciones
6. `PPD` exige flujo REP
7. Cancelación `01` exige UUID sustituto
8. Documentos timbrados no editables
9. Datos mínimos receptor: RFC, nombre, régimen fiscal, CP fiscal

## Sign-off Requerido

Todas las reglas de combinación SAT, retenciones y obligaciones deben pasar **sign-off de fiscalista/contador mexicano** antes de activarse en producción.
