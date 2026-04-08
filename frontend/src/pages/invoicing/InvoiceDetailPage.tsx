import { useMemo, useState } from 'react';
import { Link, useParams } from 'react-router';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ArrowLeft, Download, FileCheck2, ReceiptText, Stamp, XCircle } from 'lucide-react';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';
import StatusBadge from '@/components/ui/StatusBadge';
import ValidationAlertList from '@/components/invoicing/ValidationAlertList';
import { invoiceApi } from '@/lib/invoice-api';
import { formatInvoiceSeriesFolio } from '@/lib/invoice-utils';
import type { InvoiceValidationIssue } from '@/lib/invoice-types';

function formatMoney(amount: number, currencyCode: string) {
  return new Intl.NumberFormat('es-MX', {
    style: 'currency',
    currency: currencyCode || 'MXN',
    minimumFractionDigits: 2,
  }).format(amount ?? 0);
}

function formatDate(value?: string | null) {
  return value ? new Date(value).toLocaleString('es-MX') : '—';
}

function triggerBrowserDownload(blob: Blob, fileName: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = fileName;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(url);
}

export function InvoiceDetailPage() {
  const { id = '' } = useParams<{ id: string }>();
  const queryClient = useQueryClient();
  const [cancelModalOpen, setCancelModalOpen] = useState(false);
  const [cancelReason, setCancelReason] = useState('02');
  const [validationIssues, setValidationIssues] = useState<InvoiceValidationIssue[]>([]);

  const { data: invoice, isLoading, error } = useQuery({
    queryKey: ['invoice-detail', id],
    queryFn: () => invoiceApi.getInvoiceDetail(id),
    enabled: !!id,
  });

  const validateMutation = useMutation({
    mutationFn: () => invoiceApi.validatePersistedDraft(id),
    onSuccess: async (result) => {
      setValidationIssues(result.issues);
      await queryClient.invalidateQueries({ queryKey: ['invoice-detail', id] });
    },
  });

  const stampMutation = useMutation({
    mutationFn: () => invoiceApi.stampInvoice(id),
    onSuccess: async () => {
      setValidationIssues([]);
      await queryClient.invalidateQueries({ queryKey: ['invoice-detail', id] });
      await queryClient.invalidateQueries({ queryKey: ['invoices'] });
    },
  });

  const cancelMutation = useMutation({
    mutationFn: () => invoiceApi.cancelInvoice(id, cancelReason),
    onSuccess: async () => {
      setCancelModalOpen(false);
      await queryClient.invalidateQueries({ queryKey: ['invoice-detail', id] });
      await queryClient.invalidateQueries({ queryKey: ['invoices'] });
    },
  });

  const xmlDownloadMutation = useMutation({
    mutationFn: () => invoiceApi.downloadXml(id),
    onSuccess: (result) => triggerBrowserDownload(result.blob, result.fileName ?? `factura-${id}.xml`),
  });

  const pdfDownloadMutation = useMutation({
    mutationFn: () => invoiceApi.downloadPdf(id),
    onSuccess: (result) => triggerBrowserDownload(result.blob, result.fileName ?? `factura-${id}.pdf`),
  });

  const canStamp = invoice?.status === 'DRAFT' || invoice?.status === 'VALIDATED';

  const generalIssues = useMemo(() => {
    const issues = [...validationIssues];
    if (validateMutation.error) {
      issues.push({ code: 'VALIDATE_ERROR', message: 'No se pudo validar la factura.' });
    }
    if (stampMutation.error) {
      issues.push({ code: 'STAMP_ERROR', message: 'No se pudo timbrar la factura.' });
    }
    if (cancelMutation.error) {
      issues.push({ code: 'CANCEL_ERROR', message: 'No se pudo cancelar la factura.' });
    }
    return issues;
  }, [cancelMutation.error, stampMutation.error, validateMutation.error, validationIssues]);

  if (isLoading) {
    return <div className="py-12 text-center text-gray-500">Cargando factura...</div>;
  }

  if (error || !invoice) {
    return (
      <div className="py-12 text-center text-red-600">
        Error al cargar la factura.{' '}
        <Link to="/facturacion" className="underline">
          Volver a facturación
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <Link to="/facturacion" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700">
          <ArrowLeft className="h-4 w-4" />
          Volver a facturación
        </Link>
      </div>

      <div className="flex flex-col gap-4 rounded-lg border border-gray-200 bg-white p-6 xl:flex-row xl:items-start xl:justify-between">
        <div>
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-2xl font-bold text-gray-900">
              Factura {formatInvoiceSeriesFolio(invoice.series, invoice.folio)}
            </h1>
            <StatusBadge status={invoice.status} />
          </div>
          <dl className="mt-4 grid grid-cols-1 gap-3 text-sm md:grid-cols-2 xl:grid-cols-3">
            <div>
              <dt className="text-gray-500">UUID</dt>
              <dd className="font-medium text-gray-900">{invoice.pacUuid ?? 'Sin timbrar'}</dd>
            </div>
            <div>
              <dt className="text-gray-500">Fecha de emisión</dt>
              <dd className="font-medium text-gray-900">{formatDate(invoice.issuedAt)}</dd>
            </div>
            <div>
              <dt className="text-gray-500">Fecha de certificación</dt>
              <dd className="font-medium text-gray-900">{formatDate(invoice.certifiedAt)}</dd>
            </div>
            <div>
              <dt className="text-gray-500">Moneda</dt>
              <dd className="font-medium text-gray-900">{invoice.currencyCode}</dd>
            </div>
            <div>
              <dt className="text-gray-500">Método de pago</dt>
              <dd className="font-medium text-gray-900">{invoice.paymentMethodCode}</dd>
            </div>
            <div>
              <dt className="text-gray-500">Forma de pago</dt>
              <dd className="font-medium text-gray-900">{invoice.paymentFormCode ?? '—'}</dd>
            </div>
          </dl>
        </div>

        <div className="flex flex-wrap gap-2">
          <Button variant="secondary" size="sm" onClick={() => pdfDownloadMutation.mutate()} disabled={pdfDownloadMutation.isPending}>
            <Download className="h-4 w-4" />
            {pdfDownloadMutation.isPending ? 'Descargando PDF...' : 'Descargar PDF'}
          </Button>
          <Button variant="secondary" size="sm" onClick={() => xmlDownloadMutation.mutate()} disabled={xmlDownloadMutation.isPending}>
            <Download className="h-4 w-4" />
            {xmlDownloadMutation.isPending ? 'Descargando XML...' : 'Descargar XML'}
          </Button>
          {canStamp && (
            <>
              <Button variant="secondary" size="sm" onClick={() => validateMutation.mutate()} disabled={validateMutation.isPending || stampMutation.isPending}>
                <FileCheck2 className="h-4 w-4" />
                {validateMutation.isPending ? 'Validando...' : 'Validar'}
              </Button>
              <Button size="sm" onClick={() => stampMutation.mutate()} disabled={stampMutation.isPending || validateMutation.isPending}>
                <Stamp className="h-4 w-4" />
                {stampMutation.isPending ? 'Timbrando...' : 'Timbrar'}
              </Button>
            </>
          )}
          {invoice.status === 'STAMPED' && (
            <Button variant="danger" size="sm" onClick={() => setCancelModalOpen(true)}>
              <XCircle className="h-4 w-4" />
              Cancelar
            </Button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Emisor</h2>
          <dl className="space-y-2 text-sm">
            <div className="flex justify-between gap-4">
              <dt className="text-gray-500">Razón social</dt>
              <dd className="text-right font-medium text-gray-900">{invoice.issuerName}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-gray-500">RFC</dt>
              <dd className="text-right font-medium text-gray-900">{invoice.issuerRfc}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-gray-500">Régimen fiscal</dt>
              <dd className="text-right font-medium text-gray-900">{invoice.issuerRegimeCode}</dd>
            </div>
          </dl>
        </section>

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Cliente / Receptor</h2>
          <dl className="space-y-2 text-sm">
            <div className="flex justify-between gap-4">
              <dt className="text-gray-500">Razón social</dt>
              <dd className="text-right font-medium text-gray-900">{invoice.receiverName}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-gray-500">RFC</dt>
              <dd className="text-right font-medium text-gray-900">{invoice.receiverRfc}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-gray-500">Régimen fiscal</dt>
              <dd className="text-right font-medium text-gray-900">{invoice.receiverRegimeCode}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-gray-500">Código postal fiscal</dt>
              <dd className="text-right font-medium text-gray-900">{invoice.receiverPostalCode}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-gray-500">Uso CFDI</dt>
              <dd className="text-right font-medium text-gray-900">{invoice.usoCfdiCode}</dd>
            </div>
          </dl>
        </section>
      </div>

      <ValidationAlertList issues={generalIssues} />

      <section className="rounded-lg border border-gray-200 bg-white p-6">
        <div className="mb-4 flex items-center gap-2">
          <ReceiptText className="h-5 w-5 text-primary-600" />
          <h2 className="text-lg font-semibold text-gray-900">Conceptos</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-200 bg-gray-50">
                <th className="px-4 py-2 text-left font-semibold text-gray-500">Descripción</th>
                <th className="px-4 py-2 text-left font-semibold text-gray-500">SAT</th>
                <th className="px-4 py-2 text-right font-semibold text-gray-500">Cantidad</th>
                <th className="px-4 py-2 text-right font-semibold text-gray-500">P. unitario</th>
                <th className="px-4 py-2 text-right font-semibold text-gray-500">Descuento</th>
                <th className="px-4 py-2 text-right font-semibold text-gray-500">Importe</th>
                <th className="px-4 py-2 text-right font-semibold text-gray-500">Total línea</th>
              </tr>
            </thead>
            <tbody>
              {invoice.lines.map((line) => (
                <tr key={line.id ?? `${line.lineNumber}-${line.description}`} className="border-b border-gray-100 last:border-b-0">
                  <td className="px-4 py-3 text-gray-900">
                    <p className="font-medium">{line.description}</p>
                    {line.objetoImpCode && <p className="text-xs text-gray-500">Objeto imp.: {line.objetoImpCode}</p>}
                  </td>
                  <td className="px-4 py-3 text-gray-600">{line.satProductCode} · {line.satUnitCode}</td>
                  <td className="px-4 py-3 text-right text-gray-900">{line.quantity}</td>
                  <td className="px-4 py-3 text-right text-gray-900">{formatMoney(line.unitPrice, invoice.currencyCode)}</td>
                  <td className="px-4 py-3 text-right text-gray-900">{formatMoney(line.discount, invoice.currencyCode)}</td>
                  <td className="px-4 py-3 text-right text-gray-900">{formatMoney(line.subtotal, invoice.currencyCode)}</td>
                  <td className="px-4 py-3 text-right font-medium text-gray-900">{formatMoney(line.total, invoice.currencyCode)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="grid grid-cols-1 gap-6 xl:grid-cols-2">
        <div className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Impuestos</h2>
          {invoice.taxLines.length === 0 ? (
            <p className="text-sm text-gray-500">No hay líneas de impuesto registradas.</p>
          ) : (
            <div className="space-y-3 text-sm">
              {invoice.taxLines.map((taxLine) => (
                <div key={taxLine.id ?? `${taxLine.taxCode}-${taxLine.sourceLineId}`} className="rounded-md border border-gray-100 bg-gray-50 px-3 py-2">
                  <div className="flex justify-between gap-3">
                    <span className="font-medium text-gray-900">{taxLine.isTransfer ? 'Traslado' : 'Retención'} {taxLine.taxCode}</span>
                    <span className="text-gray-900">{formatMoney(taxLine.taxAmount, invoice.currencyCode)}</span>
                  </div>
                  <p className="text-xs text-gray-500">Base {formatMoney(taxLine.baseAmount, invoice.currencyCode)} · {taxLine.factorType} {taxLine.rate}</p>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Totales</h2>
          <dl className="space-y-2 text-sm">
            <div className="flex justify-between">
              <dt className="text-gray-600">Subtotal</dt>
              <dd className="font-medium text-gray-900">{formatMoney(invoice.subtotal, invoice.currencyCode)}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-600">Descuento</dt>
              <dd className="font-medium text-gray-900">{formatMoney(invoice.discount, invoice.currencyCode)}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-600">Impuestos trasladados</dt>
              <dd className="font-medium text-gray-900">{formatMoney(invoice.transferredTaxTotal, invoice.currencyCode)}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-600">Impuestos retenidos</dt>
              <dd className="font-medium text-gray-900">{formatMoney(invoice.withheldTaxTotal, invoice.currencyCode)}</dd>
            </div>
            <div className="flex justify-between border-t border-gray-200 pt-3">
              <dt className="font-semibold text-gray-900">Total</dt>
              <dd className="text-lg font-bold text-gray-900">{formatMoney(invoice.total, invoice.currencyCode)}</dd>
            </div>
          </dl>
        </div>
      </section>

      <Modal
        open={cancelModalOpen}
        onClose={() => setCancelModalOpen(false)}
        title="Cancelar factura"
        footer={(
          <>
            <Button variant="secondary" onClick={() => setCancelModalOpen(false)}>
              Volver
            </Button>
            <Button variant="danger" onClick={() => cancelMutation.mutate()} disabled={cancelMutation.isPending}>
              {cancelMutation.isPending ? 'Cancelando...' : 'Confirmar cancelación'}
            </Button>
          </>
        )}
      >
        <p className="mb-4 text-sm text-gray-600">
          Esta acción enviará la solicitud de cancelación usando el flujo real del backend.
        </p>
        <label className="mb-1 block text-sm font-medium text-gray-700">Motivo de cancelación</label>
        <select
          value={cancelReason}
          onChange={(event) => setCancelReason(event.target.value)}
          className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
        >
          <option value="01">01 - Comprobante emitido con errores con relación</option>
          <option value="02">02 - Comprobante emitido con errores sin relación</option>
          <option value="03">03 - No se llevó a cabo la operación</option>
          <option value="04">04 - Operación nominativa relacionada en factura global</option>
        </select>
      </Modal>
    </div>
  );
}
