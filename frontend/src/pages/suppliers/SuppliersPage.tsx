import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { Search, Pencil } from 'lucide-react';
import { api } from '@/lib/api';
import type { Supplier, PageResponse } from '@/lib/types';
import PageToolbar from '@/components/ui/PageToolbar';
import DataGrid from '@/components/ui/DataGrid';
import Button from '@/components/ui/Button';
import Pagination from '@/components/ui/Pagination';

export function SuppliersPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['suppliers', page, search],
    queryFn: () =>
      api.get<PageResponse<Supplier>>(
        `/suppliers?page=${page - 1}&size=10${search ? `&search=${encodeURIComponent(search)}` : ''}`,
      ),
  });

  const columns = [
    {
      key: 'legalName',
      header: 'Nombre legal',
      render: (row: Supplier) => (
        <Link to={`/proveedores/${row.id}`} className="font-medium text-primary-600 hover:underline">
          {row.legalName}
        </Link>
      ),
    },
    { key: 'rfc', header: 'RFC' },
    { key: 'email', header: 'Email' },
    { key: 'nationality', header: 'Nacionalidad' },
    {
      key: 'actions',
      header: 'Acciones',
      render: (row: Supplier) => (
        <button
          type="button"
          onClick={(e) => {
            e.stopPropagation();
            navigate(`/proveedores/${row.id}`);
          }}
          className="text-gray-400 hover:text-primary-600 transition-colors"
        >
          <Pencil className="h-4 w-4" />
        </button>
      ),
    },
  ];

  return (
    <div>
      <PageToolbar
        title="Proveedores"
        actions={
          <Link to="/proveedores/nuevo">
            <Button>Nuevo proveedor</Button>
          </Link>
        }
      >
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Buscar por nombre o RFC..."
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(1);
            }}
            className="rounded-md border border-gray-200 bg-white py-1.5 pl-9 pr-3 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>
      </PageToolbar>

      <DataGrid columns={columns} data={data?.items ?? []} isLoading={isLoading} emptyMessage="No se encontraron proveedores" />
      {data && data.totalPages > 1 && (
        <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
      )}
    </div>
  );
}
