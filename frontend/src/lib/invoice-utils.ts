import type {
  InvoiceCreateDraftRequest,
  InvoiceDraftLineForm,
  InvoiceFormValues,
  InvoiceLocalLineSummary,
  InvoiceLocalSummary,
} from './invoice-types';

export function createEmptyInvoiceLine(): InvoiceDraftLineForm {
  return {
    productId: '',
    description: '',
    quantity: 1,
    unitPrice: 0,
    discount: 0,
    productLabel: '',
    satProductCode: '',
    satUnitCode: '',
    objetoImpCode: '',
    currencyCode: '',
  };
}

export function calculateLocalLineSummary(line: InvoiceDraftLineForm): InvoiceLocalLineSummary {
  const quantity = Number.isFinite(line.quantity) ? line.quantity : 0;
  const unitPrice = Number.isFinite(line.unitPrice) ? Number(line.unitPrice) : 0;
  const discount = Number.isFinite(line.discount) ? Number(line.discount) : 0;
  const subtotal = roundCurrency(quantity * unitPrice);
  const netSubtotal = roundCurrency(Math.max(subtotal - discount, 0));
  return { subtotal, netSubtotal };
}

export function calculateLocalInvoiceSummary(lines: InvoiceDraftLineForm[]): InvoiceLocalSummary {
  const subtotal = roundCurrency(lines.reduce((sum, line) => sum + calculateLocalLineSummary(line).subtotal, 0));
  const discount = roundCurrency(lines.reduce((sum, line) => sum + (Number.isFinite(line.discount) ? Number(line.discount) : 0), 0));
  const total = roundCurrency(Math.max(subtotal - discount, 0));

  return {
    subtotal,
    discount,
    transferredTaxTotal: 0,
    withheldTaxTotal: 0,
    total,
  };
}

export function mapInvoiceFormToDraftRequest(values: InvoiceFormValues): InvoiceCreateDraftRequest {
  return {
    clientId: values.clientId,
    invoiceType: values.invoiceType,
    series: normalizeOptional(values.series),
    folio: normalizeOptional(values.folio),
    paymentMethodCode: normalizeOptional(values.paymentMethodCode),
    paymentFormCode: normalizeOptional(values.paymentFormCode),
    usoCfdiCode: normalizeOptional(values.usoCfdiCode),
    currencyCode: normalizeOptional(values.currencyCode),
    lines: values.lines.map((line) => ({
      productId: line.productId,
      description: normalizeOptional(line.description),
      quantity: Number(line.quantity),
      unitPrice: line.unitPrice === undefined ? undefined : Number(line.unitPrice),
      discount: line.discount === undefined ? undefined : Number(line.discount),
    })),
  };
}

export function invoiceFieldPathToFormPath(fieldPath?: string | null): string | undefined {
  if (!fieldPath) {
    return undefined;
  }

  return fieldPath.replace(/\[(\d+)\]/g, '.$1');
}

export function formatInvoiceSeriesFolio(series?: string | null, folio?: string | null): string {
  const safeSeries = series?.trim() ?? '';
  const safeFolio = folio?.trim() ?? '';
  return `${safeSeries}${safeFolio}`.trim() || 'Sin folio';
}

function normalizeOptional(value?: string | null): string | undefined {
  const normalized = value?.trim();
  return normalized ? normalized : undefined;
}

function roundCurrency(value: number): number {
  return Math.round(value * 100) / 100;
}
