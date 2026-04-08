import type { InvoiceLocalSummary, InvoicePreview } from '@/lib/invoice-types';

interface InvoiceSummaryCardProps {
  localSummary: InvoiceLocalSummary;
  validatedPreview?: InvoicePreview | null;
}

function formatMoney(amount: number, currencyCode = 'MXN') {
  return new Intl.NumberFormat('es-MX', {
    style: 'currency',
    currency: currencyCode || 'MXN',
    minimumFractionDigits: 2,
  }).format(amount ?? 0);
}

export default function InvoiceSummaryCard({ localSummary, validatedPreview }: InvoiceSummaryCardProps) {
  const summary = validatedPreview ?? localSummary;
  const currencyCode = validatedPreview?.currencyCode ?? 'MXN';

  return (
    <section className="rounded-lg border border-gray-200 bg-white p-6">
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-gray-900">Resumen</h2>
        <span className={`rounded-full px-2.5 py-1 text-xs font-medium ${validatedPreview ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'}`}>
          {validatedPreview ? 'Totales validados' : 'Vista previa local'}
        </span>
      </div>

      <dl className="space-y-2 text-sm">
        <div className="flex justify-between">
          <dt className="text-gray-600">Subtotal</dt>
          <dd className="font-medium text-gray-900">{formatMoney(summary.subtotal, currencyCode)}</dd>
        </div>
        <div className="flex justify-between">
          <dt className="text-gray-600">Descuento</dt>
          <dd className="font-medium text-gray-900">{formatMoney(summary.discount, currencyCode)}</dd>
        </div>
        <div className="flex justify-between">
          <dt className="text-gray-600">Impuestos trasladados</dt>
          <dd className="font-medium text-gray-900">{formatMoney(summary.transferredTaxTotal, currencyCode)}</dd>
        </div>
        <div className="flex justify-between">
          <dt className="text-gray-600">Impuestos retenidos</dt>
          <dd className="font-medium text-gray-900">{formatMoney(summary.withheldTaxTotal, currencyCode)}</dd>
        </div>
        <div className="flex justify-between border-t border-gray-200 pt-3">
          <dt className="font-semibold text-gray-900">Total</dt>
          <dd className="text-lg font-bold text-gray-900">{formatMoney(summary.total, currencyCode)}</dd>
        </div>
      </dl>

      {!validatedPreview && (
        <p className="mt-4 text-xs text-gray-500">
          Los impuestos y totales fiscales definitivos se confirman con la validación del backend antes de guardar o timbrar.
        </p>
      )}
    </section>
  );
}
