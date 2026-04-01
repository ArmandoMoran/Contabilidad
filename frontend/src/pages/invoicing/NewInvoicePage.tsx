import { useState } from 'react';
import { useNavigate } from 'react-router';
import { useMutation } from '@tanstack/react-query';
import { Plus, Trash2 } from 'lucide-react';
import { api } from '@/lib/api';
import type { Invoice } from '@/lib/types';
import PageToolbar from '@/components/ui/PageToolbar';
import Button from '@/components/ui/Button';

interface LineItem {
  productId: string;
  description: string;
  quantity: number;
  unitPrice: number;
  discount: number;
  taxAmount: number;
  subtotal: number;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'mb-1 block text-sm font-medium text-gray-700';

function emptyLine(): LineItem {
  return { productId: '', description: '', quantity: 1, unitPrice: 0, discount: 0, taxAmount: 0, subtotal: 0 };
}

function calcLine(line: LineItem): LineItem {
  const base = line.quantity * line.unitPrice - line.discount;
  const tax = base * 0.16;
  return { ...line, taxAmount: tax, subtotal: base };
}

export function NewInvoicePage() {
  const navigate = useNavigate();
  const [clientId, setClientId] = useState('');
  const [tipoComprobante, setTipoComprobante] = useState('I');
  const [metodoPago, setMetodoPago] = useState('PUE');
  const [formaPago, setFormaPago] = useState('03');
  const [usoCfdi, setUsoCfdi] = useState('G03');
  const [moneda, setMoneda] = useState('MXN');
  const [lines, setLines] = useState<LineItem[]>([emptyLine()]);
  const [validations, setValidations] = useState<string[]>([]);

  const subtotal = lines.reduce((sum, line) => sum + calcLine(line).subtotal, 0);
  const ivaTraslado = lines.reduce((sum, line) => sum + calcLine(line).taxAmount, 0);
  const ivaRetenido = 0;
  const isrRetenido = 0;
  const total = subtotal + ivaTraslado - ivaRetenido - isrRetenido;

  function updateLine(index: number, updates: Partial<LineItem>) {
    setLines((previous) => previous.map((line, currentIndex) => (currentIndex === index ? { ...line, ...updates } : line)));
  }

  function removeLine(index: number) {
    setLines((previous) => (previous.length === 1 ? previous : previous.filter((_, currentIndex) => currentIndex !== index)));
  }

  const draftMutation = useMutation({
    mutationFn: (payload: object) => api.post<Invoice>('/invoices/drafts', payload),
    onSuccess: (invoice) => navigate(`/facturacion/${invoice.id}`),
  });

  const stampMutation = useMutation({
    mutationFn: async (payload: object) => {
      const draft = await api.post<Invoice>('/invoices/drafts', payload);
      return api.post<Invoice>(`/invoices/${draft.id}/stamp`, {});
    },
    onSuccess: (invoice) => navigate(`/facturacion/${invoice.id}`),
  });

  function buildPayload() {
    return {
      clientId,
      invoiceType: tipoComprobante,
      paymentMethodCode: metodoPago,
      paymentFormCode: formaPago,
      usoCfdiCode: usoCfdi,
      currencyCode: moneda,
      lines: lines.map((line) => ({
        productId: line.productId || undefined,
        description: line.description,
        quantity: line.quantity,
        unitPrice: line.unitPrice,
        discount: line.discount,
        satProductCode: '01010101',
        satUnitCode: 'E48',
      })),
    };
  }

  function validateDraft() {
    const errors: string[] = [];
    if (!clientId.trim()) errors.push('Selecciona un cliente.');
    if (lines.every((line) => !line.description.trim() && !line.productId.trim())) {
      errors.push('Agrega al menos un producto o servicio.');
    }

    lines.forEach((line, index) => {
      if (line.quantity <= 0) errors.push(`Linea ${index + 1}: la cantidad debe ser mayor a 0.`);
      if (line.unitPrice <= 0) errors.push(`Linea ${index + 1}: el precio unitario debe ser mayor a 0.`);
    });

    setValidations(errors);
    return errors;
  }

  return (
    <div>
      <PageToolbar title="Nueva factura" />

      <div className="space-y-6">
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-2 text-lg font-semibold text-gray-900">Emisor</h2>
          <p className="text-sm text-gray-500">Los datos del emisor se toman de la configuracion de tu empresa.</p>
        </section>

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Tu cliente</h2>
          <div>
            <label htmlFor="clientId" className={labelClass}>Buscar cliente</label>
            <input
              id="clientId"
              type="text"
              value={clientId}
              onChange={(event) => setClientId(event.target.value)}
              className={inputClass}
              placeholder="Nombre o RFC del cliente..."
            />
          </div>
        </section>

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Datos de factura</h2>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div>
              <label className={labelClass}>Tipo de comprobante</label>
              <select value={tipoComprobante} onChange={(event) => setTipoComprobante(event.target.value)} className={inputClass}>
                <option value="I">I - Ingreso</option>
                <option value="E">E - Egreso</option>
                <option value="T">T - Traslado</option>
                <option value="P">P - Pago</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Metodo de pago</label>
              <select value={metodoPago} onChange={(event) => setMetodoPago(event.target.value)} className={inputClass}>
                <option value="PUE">PUE - Pago en una sola exhibicion</option>
                <option value="PPD">PPD - Pago en parcialidades</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Forma de pago</label>
              <select value={formaPago} onChange={(event) => setFormaPago(event.target.value)} className={inputClass}>
                <option value="01">01 - Efectivo</option>
                <option value="02">02 - Cheque nominativo</option>
                <option value="03">03 - Transferencia electronica</option>
                <option value="04">04 - Tarjeta de credito</option>
                <option value="28">28 - Tarjeta de debito</option>
                <option value="99">99 - Por definir</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Uso CFDI</label>
              <select value={usoCfdi} onChange={(event) => setUsoCfdi(event.target.value)} className={inputClass}>
                <option value="G01">G01 - Adquisicion de mercancias</option>
                <option value="G03">G03 - Gastos en general</option>
                <option value="S01">S01 - Sin efectos fiscales</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Moneda</label>
              <select value={moneda} onChange={(event) => setMoneda(event.target.value)} className={inputClass}>
                <option value="MXN">MXN - Peso mexicano</option>
                <option value="USD">USD - Dolar americano</option>
                <option value="EUR">EUR - Euro</option>
              </select>
            </div>
          </div>
        </section>

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">Productos o servicios</h2>
            <Button size="sm" variant="secondary" onClick={() => setLines((previous) => [...previous, emptyLine()])}>
              <Plus className="h-4 w-4" /> Agregar linea
            </Button>
          </div>

          <div className="space-y-4">
            {lines.map((line, index) => {
              const calculatedLine = calcLine(line);
              return (
                <div key={index} className="grid grid-cols-12 items-end gap-3 border-b border-gray-100 pb-4">
                  <div className="col-span-3">
                    <label htmlFor={`productId-${index}`} className={labelClass}>Producto</label>
                    <input
                      id={`productId-${index}`}
                      value={line.productId}
                      onChange={(event) => updateLine(index, { productId: event.target.value })}
                      className={inputClass}
                      placeholder="Buscar producto..."
                    />
                  </div>
                  <div className="col-span-3">
                    <label htmlFor={`description-${index}`} className={labelClass}>Descripcion</label>
                    <input
                      id={`description-${index}`}
                      value={line.description}
                      onChange={(event) => updateLine(index, { description: event.target.value })}
                      className={inputClass}
                    />
                  </div>
                  <div className="col-span-1">
                    <label className={labelClass}>Cant.</label>
                    <input
                      type="number"
                      min="0"
                      step="1"
                      value={line.quantity}
                      onChange={(event) => updateLine(index, { quantity: Number(event.target.value) })}
                      className={inputClass}
                    />
                  </div>
                  <div className="col-span-2">
                    <label htmlFor={`unitPrice-${index}`} className={labelClass}>Precio unit.</label>
                    <input
                      id={`unitPrice-${index}`}
                      type="number"
                      min="0"
                      step="0.01"
                      value={line.unitPrice}
                      onChange={(event) => updateLine(index, { unitPrice: Number(event.target.value) })}
                      className={inputClass}
                    />
                  </div>
                  <div className="col-span-1">
                    <label className={labelClass}>Desc.</label>
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={line.discount}
                      onChange={(event) => updateLine(index, { discount: Number(event.target.value) })}
                      className={inputClass}
                    />
                  </div>
                  <div className="col-span-1 text-right">
                    <label className={labelClass}>Subtotal</label>
                    <p className="py-2 text-sm font-medium text-gray-900">${calculatedLine.subtotal.toFixed(2)}</p>
                  </div>
                  <div className="col-span-1 flex justify-end pb-2">
                    <button
                      type="button"
                      onClick={() => removeLine(index)}
                      className="transition-colors hover:text-red-600 text-gray-400"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </section>

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Resumen</h2>
          <dl className="space-y-2 text-sm">
            <div className="flex justify-between">
              <dt className="text-gray-600">Subtotal</dt>
              <dd className="font-medium text-gray-900">${subtotal.toFixed(2)}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-600">IVA trasladado (16%)</dt>
              <dd className="font-medium text-gray-900">${ivaTraslado.toFixed(2)}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-600">IVA retenido</dt>
              <dd className="font-medium text-gray-900">${ivaRetenido.toFixed(2)}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-600">ISR retenido</dt>
              <dd className="font-medium text-gray-900">${isrRetenido.toFixed(2)}</dd>
            </div>
            <div className="flex justify-between border-t border-gray-200 pt-2">
              <dt className="font-semibold text-gray-900">Total</dt>
              <dd className="text-lg font-bold text-gray-900">${total.toFixed(2)}</dd>
            </div>
          </dl>
        </section>

        {validations.length > 0 && (
          <section className="rounded-lg border border-red-200 bg-red-50 p-6">
            <h2 className="mb-2 text-lg font-semibold text-red-800">Validaciones</h2>
            <ul className="list-inside list-disc space-y-1 text-sm text-red-700">
              {validations.map((validation, index) => (
                <li key={index}>{validation}</li>
              ))}
            </ul>
          </section>
        )}

        <div className="flex items-center justify-end gap-3">
          <Button variant="secondary" onClick={() => draftMutation.mutate(buildPayload())} disabled={draftMutation.isPending}>
            {draftMutation.isPending ? 'Guardando...' : 'Guardar borrador'}
          </Button>
          <Button variant="secondary" onClick={() => void validateDraft()}>
            Validar
          </Button>
          <Button
            onClick={() => {
              const errors = validateDraft();
              if (errors.length === 0) {
                stampMutation.mutate(buildPayload());
              }
            }}
            disabled={stampMutation.isPending}
          >
            {stampMutation.isPending ? 'Timbrando...' : 'Timbrar'}
          </Button>
        </div>
      </div>
    </div>
  );
}
