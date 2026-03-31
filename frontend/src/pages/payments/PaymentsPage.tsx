import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { api } from '@/lib/api';
import type { Payment, PageResponse } from '@/lib/types';
import PageToolbar from '@/components/ui/PageToolbar';
import DataGrid from '@/components/ui/DataGrid';
import Button from '@/components/ui/Button';
import StatusBadge from '@/components/ui/StatusBadge';
import Modal from '@/components/ui/Modal';
import Pagination from '@/components/ui/Pagination';

interface PaymentFormData {
  paymentDirection: string;
  paymentFormCode: string;
  currencyCode: string;
  amount: number;
  paidAt: string;
  relatedDocuments: string;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

export function PaymentsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const { register, handleSubmit, reset, formState: { errors } } = useForm<PaymentFormData>();

  const { data, isLoading } = useQuery({
    queryKey: ['payments', page],
    queryFn: () => api.get<PageResponse<Payment>>(`/payments?page=${page - 1}&size=10`),
  });

  const createMutation = useMutation({
    mutationFn: (formData: PaymentFormData) => api.post<Payment>('/payments', formData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payments'] });
      setModalOpen(false);
      reset();
    },
  });

  const columns = [
    {
      key: 'paymentDirection',
      header: 'Tipo',
      render: (row: Payment) => (
        <span className={`font-medium ${row.paymentDirection === 'INBOUND' ? 'text-green-700' : 'text-red-700'}`}>
          {row.paymentDirection === 'INBOUND' ? 'Cobro' : 'Pago'}
        </span>
      ),
    },
    { key: 'paymentFormCode', header: 'Forma de pago' },
    {
      key: 'amount',
      header: 'Monto',
      render: (row: Payment) => `$${row.amount.toFixed(2)} ${row.currencyCode}`,
    },
    { key: 'paidAt', header: 'Fecha', render: (row: Payment) => new Date(row.paidAt).toLocaleDateString('es-MX') },
    {
      key: 'status',
      header: 'Estado',
      render: (row: Payment) => <StatusBadge status={row.status} />,
    },
    {
      key: 'docs',
      header: 'Documentos aplicados',
      render: () => <span className="text-xs text-gray-400">—</span>,
    },
  ];

  function onSubmit(formData: PaymentFormData) {
    // Convert date string to ISO-8601 instant for backend Instant field
    const payload = {
      ...formData,
      paidAt: formData.paidAt ? new Date(formData.paidAt + 'T12:00:00').toISOString() : undefined,
    };
    createMutation.mutate(payload as unknown as PaymentFormData);
  }

  return (
    <div>
      <PageToolbar
        title="Pagos"
        actions={<Button onClick={() => setModalOpen(true)}>Registrar pago</Button>}
      />

      <DataGrid columns={columns} data={data?.items ?? []} isLoading={isLoading} emptyMessage="No se encontraron pagos" />
      {data && data.totalPages > 1 && (
        <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
      )}

      <Modal
        open={modalOpen}
        onClose={() => { setModalOpen(false); reset(); }}
        title="Registrar pago"
        footer={
          <>
            <Button variant="secondary" onClick={() => { setModalOpen(false); reset(); }}>
              Cancelar
            </Button>
            <Button onClick={handleSubmit(onSubmit)} disabled={createMutation.isPending}>
              {createMutation.isPending ? 'Guardando...' : 'Registrar'}
            </Button>
          </>
        }
      >
        <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
          {createMutation.error && (
            <div className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">
              Error al registrar el pago.
            </div>
          )}
          <div>
            <label className={labelClass}>Tipo *</label>
            <select {...register('paymentDirection', { required: 'Tipo es requerido' })} className={inputClass}>
              <option value="INBOUND">Cobro (entrada)</option>
              <option value="OUTBOUND">Pago (salida)</option>
            </select>
            {errors.paymentDirection && <p className="mt-1 text-xs text-red-600">{errors.paymentDirection.message}</p>}
          </div>
          <div>
            <label className={labelClass}>Forma de pago *</label>
            <select {...register('paymentFormCode', { required: 'Forma de pago es requerida' })} className={inputClass}>
              <option value="01">01 - Efectivo</option>
              <option value="02">02 - Cheque nominativo</option>
              <option value="03">03 - Transferencia electrónica</option>
              <option value="04">04 - Tarjeta de crédito</option>
              <option value="28">28 - Tarjeta de débito</option>
            </select>
            {errors.paymentFormCode && <p className="mt-1 text-xs text-red-600">{errors.paymentFormCode.message}</p>}
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Monto *</label>
              <input
                {...register('amount', { required: 'Monto es requerido', valueAsNumber: true })}
                type="number"
                step="0.01"
                min="0"
                className={inputClass}
              />
              {errors.amount && <p className="mt-1 text-xs text-red-600">{errors.amount.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Moneda</label>
              <select {...register('currencyCode')} className={inputClass} defaultValue="MXN">
                <option value="MXN">MXN</option>
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
              </select>
            </div>
          </div>
          <div>
            <label className={labelClass}>Fecha de pago *</label>
            <input
              {...register('paidAt', { required: 'Fecha es requerida' })}
              type="date"
              className={inputClass}
            />
            {errors.paidAt && <p className="mt-1 text-xs text-red-600">{errors.paidAt.message}</p>}
          </div>
          <div>
            <label className={labelClass}>Documentos relacionados</label>
            <input
              {...register('relatedDocuments')}
              className={inputClass}
              placeholder="UUID de facturas separados por coma"
            />
          </div>
        </form>
      </Modal>
    </div>
  );
}
