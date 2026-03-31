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

const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

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

  const subtotal = lines.reduce((s, l) => s + calcLine(l).subtotal, 0);
  const ivaTraslado = lines.reduce((s, l) => s + calcLine(l).taxAmount, 0);
  const ivaRetenido = 0;
  const isrRetenido = 0;
  const total = subtotal + ivaTraslado - ivaRetenido - isrRetenido;

  function updateLine(index: number, updates: Partial<LineItem>) {
    setLines((prev) => prev.map((l, i) => (i === index ? { ...l, ...updates } : l)));
  }

  function removeLine(index: number) {
    setLines((prev) => (prev.length === 1 ? prev : prev.filter((_, i) => i !== index)));
  }

  const draftMutation = useMutation({
    mutationFn: (payload: object) => api.post<Invoice>('/invoices/drafts', payload),
    onSuccess: (inv) => navigate(`/facturacion/${inv.id}`),
  });

  const stampMutation = useMutation({
    mutationFn: (invoiceId: string) => api.post<Invoice>(`/invoices/${invoiceId}/stamp`, {}),
    onSuccess: (inv) => navigate(`/facturacion/${inv.id}`),
  });

  function buildPayload() {
    return {
      clientId,
      invoiceType: tipoComprobante,
      paymentMethodCode: metodoPago,
      paymentFormCode: formaPago,
      usoCfdiCode: usoCfdi,
      currencyCode: moneda,
      lines: lines.map((l) => ({
        productId: l.productId,
        description: l.description,
        quantity: l.quantity,
        unitPrice: l.unitPrice,
        discount: l.discount,
        satProductCode: '01010101',
        satUnitCode: 'E48',
      })),
    };
  }

  function handleValidate() {
    const errs: string[] = [];
    if (!clientId) errs.push('Selecciona un cliente.');
    if (lines.every((l) => !l.description && !l.productId)) errs.push('Agrega al menos un producto o servicio.');
    lines.forEach((l, i) => {
      if (l.quantity <= 0) errs.push(`Línea ${i + 1}: la cantidad debe ser mayor a 0.`);
      if (l.unitPrice <= 0) errs.push(`Línea ${i + 1}: el precio unitario debe ser mayor a 0.`);
    });
    setValidations(errs);
  }

  return (
    <div>
      <PageToolbar title="Nueva factura" />

      <div className="space-y-6">
        {/* Emisor */}
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-2 text-lg font-semibold text-gray-900">Emisor</h2>
          <p className="text-sm text-gray-500">Los datos del emisor se toman de la configuración de tu empresa.</p>
        </section>

        {/* Cliente */}
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Tu cliente</h2>
          <div>
            <label className={labelClass}>Buscar cliente</label>
            <input
              type="text"
              value={clientId}
              onChange={(e) => setClientId(e.target.value)}
              className={inputClass}
              placeholder="Nombre o RFC del cliente..."
            />
          </div>
        </section>

        {/* Datos de factura */}
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Datos de factura</h2>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div>
              <label className={labelClass}>Tipo de comprobante</label>
              <select value={tipoComprobante} onChange={(e) => setTipoComprobante(e.target.value)} className={inputClass}>
                <option value="I">I - Ingreso</option>
                <option value="E">E - Egreso</option>
                <option value="T">T - Traslado</option>
                <option value="P">P - Pago</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Método de pago</label>
              <select value={metodoPago} onChange={(e) => setMetodoPago(e.target.value)} className={inputClass}>
                <option value="PUE">PUE - Pago en una sola exhibición</option>
                <option value="PPD">PPD - Pago en parcialidades</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Forma de pago</label>
              <select value={formaPago} onChange={(e) => setFormaPago(e.target.value)} className={inputClass}>
                <option value="01">01 - Efectivo</option>
                <option value="02">02 - Cheque nominativo</option>
                <option value="03">03 - Transferencia electrónica</option>
                <option value="04">04 - Tarjeta de crédito</option>
                <option value="28">28 - Tarjeta de débito</option>
                <option value="99">99 - Por definir</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Uso CFDI</label>
              <select value={usoCfdi} onChange={(e) => setUsoCfdi(e.target.value)} className={inputClass}>
                <option value="G01">G01 - Adquisición de mercancías</option>
                <option value="G03">G03 - Gastos en general</option>
                <option value="P01">P01 - Por definir</option>
                <option value="S01">S01 - Sin efectos fiscales</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Moneda</label>
              <select value={moneda} onChange={(e) => setMoneda(e.target.value)} className={inputClass}>
                <option value="MXN">MXN - Peso mexicano</option>
                <option value="USD">USD - Dólar americano</option>
                <option value="EUR">EUR - Euro</option>
              </select>
            </div>
          </div>
        </section>

        {/* Line Items */}
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">Productos o servicios</h2>
            <Button size="sm" variant="secondary" onClick={() => setLines((prev) => [...prev, emptyLine()])}>
              <Plus className="h-4 w-4" /> Agregar línea
            </Button>
          </div>

          <div className="space-y-4">
            {lines.map((line, idx) => {
              const calc = calcLine(line);
              return (
                <div key={idx} className="grid grid-cols-12 gap-3 items-end border-b border-gray-100 pb-4">
                  <div className="col-span-3">
                    <label className={labelClass}>Producto</label>
                    <input
                      value={line.productId}
                      onChange={(e) => updateLine(idx, { productId: e.target.value })}
                      className={inputClass}
                      placeholder="Buscar producto..."
                    />
                  </div>
                  <div className="col-span-3">
                    <label className={labelClass}>Descripción</label>
                    <input
                      value={line.description}
                      onChange={(e) => updateLine(idx, { description: e.target.value })}
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
                      onChange={(e) => updateLine(idx, { quantity: Number(e.target.value) })}
                      className={inputClass}
                    />
                  </div>
                  <div className="col-span-2">
                    <label className={labelClass}>Precio unit.</label>
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={line.unitPrice}
                      onChange={(e) => updateLine(idx, { unitPrice: Number(e.target.value) })}
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
                      onChange={(e) => updateLine(idx, { discount: Number(e.target.value) })}
                      className={inputClass}
                    />
                  </div>
                  <div className="col-span-1 text-right">
                    <label className={labelClass}>Subtotal</label>
                    <p className="py-2 text-sm font-medium text-gray-900">${calc.subtotal.toFixed(2)}</p>
                  </div>
                  <div className="col-span-1 flex justify-end pb-2">
                    <button
                      type="button"
                      onClick={() => removeLine(idx)}
                      className="text-gray-400 hover:text-red-600 transition-colors"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </section>

        {/* Summary */}
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

        {/* Validations */}
        {validations.length > 0 && (
          <section className="rounded-lg border border-red-200 bg-red-50 p-6">
            <h2 className="mb-2 text-lg font-semibold text-red-800">Validaciones</h2>
            <ul className="list-inside list-disc space-y-1 text-sm text-red-700">
              {validations.map((v, i) => (
                <li key={i}>{v}</li>
              ))}
            </ul>
          </section>
        )}

        {/* Actions */}
        <div className="flex items-center justify-end gap-3">
          <Button variant="secondary" onClick={() => draftMutation.mutate(buildPayload())} disabled={draftMutation.isPending}>
            {draftMutation.isPending ? 'Guardando...' : 'Guardar borrador'}
          </Button>
          <Button variant="secondary" onClick={handleValidate}>
            Validar
          </Button>
          <Button
            onClick={() => {
              handleValidate();
              if (validations.length === 0) stampMutation.mutate(buildPayload());
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
