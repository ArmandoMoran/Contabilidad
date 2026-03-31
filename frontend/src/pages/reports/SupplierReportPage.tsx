import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { PageResponse } from '@/lib/types';
import DataGrid from '@/components/ui/DataGrid';

interface SupplierReportRow {
  id: string;
  supplierName: string;
  rfc: string;
  totalPurchased: number;
  pendingBalance: number;
  lastExpenseDate: string | null;
}

export function SupplierReportPage() {
  const { data, isLoading } = useQuery({
    queryKey: ['reports', 'suppliers'],
    queryFn: () => api.get<PageResponse<SupplierReportRow>>('/reports/suppliers?page=0&size=50'),
  });

  const columns = [
    { key: 'supplierName', header: 'Proveedor' },
    { key: 'rfc', header: 'RFC' },
    {
      key: 'totalPurchased',
      header: 'Total comprado',
      render: (row: SupplierReportRow) => `$${row.totalPurchased.toFixed(2)}`,
    },
    {
      key: 'pendingBalance',
      header: 'Saldo pendiente',
      render: (row: SupplierReportRow) => (
        <span className={row.pendingBalance > 0 ? 'text-red-600 font-medium' : 'text-gray-700'}>
          ${row.pendingBalance.toFixed(2)}
        </span>
      ),
    },
    {
      key: 'lastExpenseDate',
      header: 'Último gasto',
      render: (row: SupplierReportRow) =>
        row.lastExpenseDate ? new Date(row.lastExpenseDate).toLocaleDateString('es-MX') : '—',
    },
  ];

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Reporte por proveedor</h1>
      <DataGrid columns={columns} data={data?.items ?? []} isLoading={isLoading} emptyMessage="Sin datos de proveedores" />
    </div>
  );
}
