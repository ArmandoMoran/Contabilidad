import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { Search } from 'lucide-react';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';
import PageToolbar from '@/components/ui/PageToolbar';
import Button from '@/components/ui/Button';
import DataGrid from '@/components/ui/DataGrid';
import Pagination from '@/components/ui/Pagination';
import StatusBadge from '@/components/ui/StatusBadge';
import { invoiceApi } from '@/lib/invoice-api';
import type { InvoiceSummary } from '@/lib/invoice-types';
import { formatInvoiceSeriesFolio } from '@/lib/invoice-utils';

function formatMoney(amount: number, currencyCode: string) {
  return new Intl.NumberFormat('es-MX', {
    style: 'currency',
    currency: currencyCode || 'MXN',
    minimumFractionDigits: 2,
  }).format(amount ?? 0);
}

function formatDate(value?: string) {
  if (!value) {
    return 'Sin fecha';
  }
  return format(new Date(value), 'dd MMM yyyy, HH:mm', { locale: es });
}

export function InvoicesPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('ALL');

  const { data, isLoading } = useQuery({
    queryKey: ['invoices', page, search, status],
    queryFn: () =>
      invoiceApi.listInvoices({
        page: page - 1,
        size: 10,
        search,
        status,
      }),
  });

  const columns = [
    {
      key: 'folio',
      header: 'Serie / folio',
      render: (invoice: InvoiceSummary) => (
        <Link to={`/facturacion/${invoice.id}`} className="font-medium text-primary-600 hover:underline">
          {formatInvoiceSeriesFolio(invoice.series, invoice.folio)}
        </Link>
      ),
    },
    { key: 'receiverName', header: 'Receptor' },
    {
      key: 'status',
      header: 'Estatus',
      render: (invoice: InvoiceSummary) => <StatusBadge status={invoice.status} />,
    },
    {
      key: 'pacUuid',
      header: 'UUID',
      render: (invoice: InvoiceSummary) => invoice.pacUuid ?? 'Sin timbrar',
    },
    {
      key: 'issuedAt',
      header: 'Fecha',
      render: (invoice: InvoiceSummary) => formatDate(invoice.issuedAt),
    },
    {
      key: 'total',
      header: 'Total',
      render: (invoice: InvoiceSummary) => formatMoney(invoice.total, invoice.currencyCode),
    },
  ];

  return (
    <div>
      <PageToolbar
        title="Facturación"
        actions={(
          <Link to="/facturacion/nueva">
            <Button>Nueva factura</Button>
          </Link>
        )}
      >
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Buscar por receptor, folio, RFC o UUID"
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              setPage(1);
            }}
            className="rounded-md border border-gray-200 bg-white py-1.5 pl-9 pr-3 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>

        <select
          value={status}
          onChange={(event) => {
            setStatus(event.target.value);
            setPage(1);
          }}
          className="rounded-md border border-gray-200 bg-white px-3 py-1.5 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
          aria-label="Filtrar por estatus"
        >
          <option value="ALL">Todos los estatus</option>
          <option value="DRAFT">Borrador</option>
          <option value="VALIDATED">Validada</option>
          <option value="STAMPED">Timbrada</option>
          <option value="CANCELLED">Cancelada</option>
        </select>
      </PageToolbar>

      <DataGrid
        columns={columns}
        data={data?.items ?? []}
        isLoading={isLoading}
        emptyMessage="No se encontraron facturas"
        onRowClick={(invoice) => navigate(`/facturacion/${invoice.id}`)}
      />

      {data && data.totalPages > 1 && (
        <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
      )}
    </div>
  );
}
