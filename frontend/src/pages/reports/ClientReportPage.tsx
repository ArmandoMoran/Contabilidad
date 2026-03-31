import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { PageResponse } from '@/lib/types';
import DataGrid from '@/components/ui/DataGrid';

interface ClientReportRow {
  id: string;
  clientName: string;
  rfc: string;
  totalInvoiced: number;
  pendingBalance: number;
  lastInvoiceDate: string | null;
}

export function ClientReportPage() {
  const { data, isLoading } = useQuery({
    queryKey: ['reports', 'clients'],
    queryFn: () => api.get<PageResponse<ClientReportRow>>('/reports/clients?page=0&size=50'),
  });

  const columns = [
    { key: 'clientName', header: 'Cliente' },
    { key: 'rfc', header: 'RFC' },
    {
      key: 'totalInvoiced',
      header: 'Total facturado',
      render: (row: ClientReportRow) => `$${row.totalInvoiced.toFixed(2)}`,
    },
    {
      key: 'pendingBalance',
      header: 'Saldo pendiente',
      render: (row: ClientReportRow) => (
        <span className={row.pendingBalance > 0 ? 'text-red-600 font-medium' : 'text-gray-700'}>
          ${row.pendingBalance.toFixed(2)}
        </span>
      ),
    },
    {
      key: 'lastInvoiceDate',
      header: 'Última factura',
      render: (row: ClientReportRow) =>
        row.lastInvoiceDate ? new Date(row.lastInvoiceDate).toLocaleDateString('es-MX') : '—',
    },
  ];

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Reporte por cliente</h1>
      <DataGrid columns={columns} data={data?.items ?? []} isLoading={isLoading} emptyMessage="Sin datos de clientes" />
    </div>
  );
}
