import { useState } from 'react';
import { useParams, Link } from 'react-router';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ArrowLeft, Download, XCircle } from 'lucide-react';
import { api } from '@/lib/api';
import type { Invoice } from '@/lib/types';
import StatusBadge from '@/components/ui/StatusBadge';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';

export function InvoiceDetailPage() {
  const { id } = useParams<{ id: string }>();
  const queryClient = useQueryClient();
  const [cancelModalOpen, setCancelModalOpen] = useState(false);
  const [cancelReason, setCancelReason] = useState('02');

  const { data: invoice, isLoading, error } = useQuery({
    queryKey: ['invoices', id],
    queryFn: () => api.get<Invoice>(`/invoices/${id}`),
    enabled: !!id,
  });

  const cancelMutation = useMutation({
    mutationFn: () => api.post(`/invoices/${id}/cancel`, { reason: cancelReason }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['invoices', id] });
      setCancelModalOpen(false);
    },
  });

  if (isLoading) {
    return <div className="py-12 text-center text-gray-500">Cargando...</div>;
  }

  if (error || !invoice) {
    return (
      <div className="py-12 text-center text-red-600">
        Error al cargar la factura. <Link to="/facturacion" className="underline">Volver</Link>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-4">
        <Link to="/facturacion" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700">
          <ArrowLeft className="h-4 w-4" /> Facturación
        </Link>
      </div>

      <div className="mb-6 flex items-center justify-between">
        <div>
          <div className="flex items-center gap-3">
            <h1 className="text-2xl font-bold text-gray-900">
              Factura {invoice.series ?? ''}{invoice.folio ?? ''}
            </h1>
            <StatusBadge status={invoice.status} />
          </div>
          {invoice.pacUuid && (
            <p className="mt-1 text-xs text-gray-500">UUID: {invoice.pacUuid}</p>
          )}
        </div>
        <div className="flex items-center gap-2">
          <Button variant="secondary" size="sm">
            <Download className="h-4 w-4" /> Descargar PDF
          </Button>
          <Button variant="secondary" size="sm">
            <Download className="h-4 w-4" /> Descargar XML
          </Button>
          {invoice.status === 'STAMPED' && (
            <Button variant="danger" size="sm" onClick={() => setCancelModalOpen(true)}>
              <XCircle className="h-4 w-4" /> Cancelar
            </Button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* Emisor */}
        <div className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-3 text-sm font-semibold uppercase text-gray-500">Emisor</h2>
          <p className="text-sm text-gray-500">Datos del emisor según configuración de empresa.</p>
        </div>

        {/* Receptor */}
        <div className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-3 text-sm font-semibold uppercase text-gray-500">Receptor</h2>
          <dl className="space-y-2 text-sm">
            <div className="flex justify-between">
              <dt className="text-gray-500">RFC</dt>
              <dd className="text-gray-900">{invoice.receiverRfc}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-500">Nombre</dt>
              <dd className="text-gray-900">{invoice.receiverName}</dd>
            </div>
          </dl>
        </div>
      </div>

      {/* Lines placeholder */}
      <div className="mt-6 rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Conceptos</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-200 bg-gray-50">
                <th className="px-4 py-2 text-left text-xs font-semibold uppercase text-gray-500">Descripción</th>
                <th className="px-4 py-2 text-right text-xs font-semibold uppercase text-gray-500">Cantidad</th>
                <th className="px-4 py-2 text-right text-xs font-semibold uppercase text-gray-500">Precio unit.</th>
                <th className="px-4 py-2 text-right text-xs font-semibold uppercase text-gray-500">Importe</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colSpan={4} className="px-4 py-8 text-center text-gray-400">
                  Los conceptos se mostrarán cuando la API provea este detalle.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* Totals */}
      <div className="mt-6 rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Totales</h2>
        <dl className="space-y-2 text-sm">
          <div className="flex justify-between">
            <dt className="text-gray-600">Subtotal</dt>
            <dd className="font-medium">${invoice.subtotal.toFixed(2)}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-gray-600">Impuestos trasladados</dt>
            <dd className="font-medium">${invoice.transferredTaxTotal.toFixed(2)}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-gray-600">Impuestos retenidos</dt>
            <dd className="font-medium">${invoice.withheldTaxTotal.toFixed(2)}</dd>
          </div>
          <div className="flex justify-between border-t border-gray-200 pt-2">
            <dt className="font-semibold text-gray-900">Total</dt>
            <dd className="text-lg font-bold text-gray-900">${invoice.total.toFixed(2)} {invoice.currencyCode}</dd>
          </div>
        </dl>
      </div>

      {/* Cancel Modal */}
      <Modal
        open={cancelModalOpen}
        onClose={() => setCancelModalOpen(false)}
        title="Cancelar factura"
        footer={
          <>
            <Button variant="secondary" onClick={() => setCancelModalOpen(false)}>
              No, volver
            </Button>
            <Button variant="danger" onClick={() => cancelMutation.mutate()} disabled={cancelMutation.isPending}>
              {cancelMutation.isPending ? 'Cancelando...' : 'Sí, cancelar factura'}
            </Button>
          </>
        }
      >
        <p className="mb-4 text-sm text-gray-600">
          Esta acción enviará una solicitud de cancelación al SAT. ¿Estás seguro?
        </p>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Motivo de cancelación</label>
          <select
            value={cancelReason}
            onChange={(e) => setCancelReason(e.target.value)}
            className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
          >
            <option value="01">01 - Comprobante emitido con errores con relación</option>
            <option value="02">02 - Comprobante emitido con errores sin relación</option>
            <option value="03">03 - No se llevó a cabo la operación</option>
            <option value="04">04 - Operación nominativa relacionada en la factura global</option>
          </select>
        </div>
        {cancelMutation.error && (
          <p className="mt-3 text-sm text-red-600">Error al cancelar. Intenta de nuevo.</p>
        )}
      </Modal>
    </div>
  );
}
