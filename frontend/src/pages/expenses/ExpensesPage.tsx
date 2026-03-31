import { useState } from 'react';
import { Link } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Expense, PageResponse } from '@/lib/types';
import PageToolbar from '@/components/ui/PageToolbar';
import DataGrid from '@/components/ui/DataGrid';
import Button from '@/components/ui/Button';
import StatusBadge from '@/components/ui/StatusBadge';
import Pagination from '@/components/ui/Pagination';

export function ExpensesPage() {
  const [page, setPage] = useState(1);

  const { data, isLoading } = useQuery({
    queryKey: ['expenses', page],
    queryFn: () => api.get<PageResponse<Expense>>(`/expenses?page=${page - 1}&size=10`),
  });

  const columns = [
    {
      key: 'issuerName',
      header: 'Proveedor',
      render: (row: Expense) => (
        <Link to={`/gastos/${row.id}`} className="font-medium text-primary-600 hover:underline">
          {row.issuerName ?? 'Sin proveedor'}
        </Link>
      ),
    },
    { key: 'issuerRfc', header: 'RFC', render: (row: Expense) => row.issuerRfc ?? '—' },
    { key: 'expenseType', header: 'Tipo' },
    { key: 'category', header: 'Categoría', render: (row: Expense) => row.category ?? '—' },
    { key: 'subtotal', header: 'Subtotal', render: (row: Expense) => `$${row.subtotal.toFixed(2)}` },
    { key: 'total', header: 'Total', render: (row: Expense) => `$${row.total.toFixed(2)}` },
    {
      key: 'status',
      header: 'Estado',
      render: (row: Expense) => <StatusBadge status={row.status} />,
    },
  ];

  return (
    <div>
      <PageToolbar
        title="Gastos"
        actions={
          <div className="flex gap-2">
            <Link to="/gastos/nuevo">
              <Button>Nuevo gasto</Button>
            </Link>
            <Button variant="secondary">Importar XML</Button>
          </div>
        }
      />

      <DataGrid columns={columns} data={data?.items ?? []} isLoading={isLoading} emptyMessage="No se encontraron gastos" />
      {data && data.totalPages > 1 && (
        <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
      )}
    </div>
  );
}
