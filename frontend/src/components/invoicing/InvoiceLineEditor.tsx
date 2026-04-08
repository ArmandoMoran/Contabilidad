import { Trash2 } from 'lucide-react';
import ProductSearchSelect from './ProductSearchSelect';
import type { InvoiceDraftLineForm, InvoiceLocalLineSummary, InvoiceProductOption } from '@/lib/invoice-types';

interface InvoiceLineEditorProps {
  index: number;
  line: InvoiceDraftLineForm;
  localSummary: InvoiceLocalLineSummary;
  selectedProduct: InvoiceProductOption | null;
  errors?: Partial<Record<'productId' | 'description' | 'quantity' | 'unitPrice' | 'discount', string>>;
  onSelectProduct: (product: InvoiceProductOption) => void;
  onClearProduct: () => void;
  onChange: (field: keyof InvoiceDraftLineForm, value: string | number) => void;
  onRemove: () => void;
  canRemove: boolean;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'mb-1 block text-sm font-medium text-gray-700';

function formatMoney(amount: number, currencyCode = 'MXN') {
  return new Intl.NumberFormat('es-MX', {
    style: 'currency',
    currency: currencyCode || 'MXN',
    minimumFractionDigits: 2,
  }).format(amount ?? 0);
}

export default function InvoiceLineEditor({
  index,
  line,
  localSummary,
  selectedProduct,
  errors,
  onSelectProduct,
  onClearProduct,
  onChange,
  onRemove,
  canRemove,
}: InvoiceLineEditorProps) {
  const currencyCode = line.currencyCode || selectedProduct?.currencyCode || 'MXN';

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-5">
      <div className="mb-4 flex items-center justify-between">
        <h3 className="text-sm font-semibold uppercase tracking-wide text-gray-500">Concepto {index + 1}</h3>
        <button
          type="button"
          onClick={onRemove}
          disabled={!canRemove}
          className="text-gray-400 transition-colors hover:text-red-600 disabled:cursor-not-allowed disabled:opacity-40"
          aria-label={`Eliminar concepto ${index + 1}`}
        >
          <Trash2 className="h-4 w-4" />
        </button>
      </div>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-12">
        <div className="xl:col-span-4">
          <label className={labelClass}>Producto o servicio</label>
          <ProductSearchSelect
            value={selectedProduct}
            onSelect={onSelectProduct}
            onClear={onClearProduct}
            error={errors?.productId}
          />
        </div>

        <div className="xl:col-span-4">
          <label htmlFor={`invoice-line-description-${index}`} className={labelClass}>Descripción</label>
          <textarea
            id={`invoice-line-description-${index}`}
            value={line.description ?? ''}
            onChange={(event) => onChange('description', event.target.value)}
            rows={3}
            className={inputClass}
          />
          {errors?.description && <p className="mt-1 text-sm text-red-600">{errors.description}</p>}
        </div>

        <div className="xl:col-span-4">
          <div className="rounded-md border border-gray-200 bg-gray-50 p-4 text-sm">
            <p className="font-medium text-gray-900">Detalle de línea</p>
            <dl className="mt-3 space-y-2">
              <div className="flex justify-between gap-3">
                <dt className="text-gray-600">Importe</dt>
                <dd className="font-medium text-gray-900">{formatMoney(localSummary.subtotal, currencyCode)}</dd>
              </div>
              <div className="flex justify-between gap-3">
                <dt className="text-gray-600">Neto estimado</dt>
                <dd className="font-medium text-gray-900">{formatMoney(localSummary.netSubtotal, currencyCode)}</dd>
              </div>
              {line.satProductCode && (
                <div className="flex justify-between gap-3">
                  <dt className="text-gray-600">SAT</dt>
                  <dd className="text-right text-gray-700">
                    {line.satProductCode} · {line.satUnitCode || 'Sin unidad'}
                  </dd>
                </div>
              )}
              {line.objetoImpCode && (
                <div className="flex justify-between gap-3">
                  <dt className="text-gray-600">Objeto imp.</dt>
                  <dd className="text-gray-700">{line.objetoImpCode}</dd>
                </div>
              )}
            </dl>
          </div>
        </div>

        <div className="xl:col-span-2">
          <label htmlFor={`invoice-line-quantity-${index}`} className={labelClass}>Cantidad</label>
          <input
            id={`invoice-line-quantity-${index}`}
            type="number"
            min="0"
            step="0.01"
            value={line.quantity}
            onChange={(event) => onChange('quantity', Number(event.target.value))}
            className={inputClass}
          />
          {errors?.quantity && <p className="mt-1 text-sm text-red-600">{errors.quantity}</p>}
        </div>

        <div className="xl:col-span-2">
          <label htmlFor={`invoice-line-unit-price-${index}`} className={labelClass}>Precio unitario</label>
          <input
            id={`invoice-line-unit-price-${index}`}
            type="number"
            min="0"
            step="0.01"
            value={line.unitPrice ?? 0}
            onChange={(event) => onChange('unitPrice', Number(event.target.value))}
            className={inputClass}
          />
          {errors?.unitPrice && <p className="mt-1 text-sm text-red-600">{errors.unitPrice}</p>}
        </div>

        <div className="xl:col-span-2">
          <label htmlFor={`invoice-line-discount-${index}`} className={labelClass}>Descuento</label>
          <input
            id={`invoice-line-discount-${index}`}
            type="number"
            min="0"
            step="0.01"
            value={line.discount ?? 0}
            onChange={(event) => onChange('discount', Number(event.target.value))}
            className={inputClass}
          />
          {errors?.discount && <p className="mt-1 text-sm text-red-600">{errors.discount}</p>}
        </div>
      </div>
    </div>
  );
}
