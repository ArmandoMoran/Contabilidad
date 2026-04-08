import { useDeferredValue, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search, UserPlus, X } from 'lucide-react';
import { invoiceApi } from '@/lib/invoice-api';
import type { InvoiceClientOption } from '@/lib/invoice-types';

interface ClientSearchSelectProps {
  value: InvoiceClientOption | null;
  onSelect: (client: InvoiceClientOption) => void;
  onClear: () => void;
  error?: string;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 pl-9 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

export default function ClientSearchSelect({ value, onSelect, onClear, error }: ClientSearchSelectProps) {
  const [search, setSearch] = useState('');
  const [open, setOpen] = useState(false);
  const deferredSearch = useDeferredValue(search);

  const { data, isLoading } = useQuery({
    queryKey: ['invoice-client-search', deferredSearch],
    queryFn: () => invoiceApi.searchClients(deferredSearch),
  });

  const options = data?.items ?? [];

  return (
    <div className="space-y-2">
      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-3 h-4 w-4 text-gray-400" />
        <input
          type="text"
          value={search}
          onFocus={() => setOpen(true)}
          onBlur={() => window.setTimeout(() => setOpen(false), 120)}
          onChange={(event) => {
            setSearch(event.target.value);
            setOpen(true);
          }}
          className={inputClass}
          placeholder={value ? 'Buscar otro cliente...' : 'Busca por nombre legal, RFC, nombre comercial o email'}
          aria-label="Buscar cliente"
        />
        {value && (
          <button
            type="button"
            onClick={onClear}
            className="absolute right-3 top-3 text-gray-400 transition-colors hover:text-gray-600"
            aria-label="Limpiar cliente"
          >
            <X className="h-4 w-4" />
          </button>
        )}
      </div>

      {value && (
        <div className="rounded-md border border-primary-100 bg-primary-50 px-3 py-2 text-sm text-primary-900">
          <p className="font-medium">{value.legalName}</p>
          <p className="text-primary-800/80">{value.rfc}{value.tradeName ? ` · ${value.tradeName}` : ''}</p>
          {value.email && <p className="text-primary-800/80">{value.email}</p>}
        </div>
      )}

      {open && (
        <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
          <div className="max-h-72 overflow-y-auto">
            {isLoading ? (
              <p className="px-3 py-3 text-sm text-gray-500">Buscando clientes...</p>
            ) : options.length === 0 ? (
              <p className="px-3 py-3 text-sm text-gray-500">No hay coincidencias para esta búsqueda.</p>
            ) : (
              options.map((client) => (
                <button
                  key={client.id}
                  type="button"
                  onMouseDown={(event) => {
                    event.preventDefault();
                    onSelect(client);
                    setSearch('');
                    setOpen(false);
                  }}
                  className="block w-full border-b border-gray-100 px-3 py-3 text-left last:border-b-0 hover:bg-gray-50"
                >
                  <p className="text-sm font-medium text-gray-900">{client.legalName}</p>
                  <p className="text-xs text-gray-500">
                    {client.rfc}
                    {client.tradeName ? ` · ${client.tradeName}` : ''}
                    {client.email ? ` · ${client.email}` : ''}
                  </p>
                </button>
              ))
            )}
          </div>
          <div className="flex items-center justify-between border-t border-gray-200 px-3 py-2 text-sm">
            <span className="text-gray-500">¿No existe el cliente?</span>
            <a
              href="/clientes/nuevo"
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1 font-medium text-primary-600 hover:underline"
            >
              <UserPlus className="h-4 w-4" />
              Nuevo cliente
            </a>
          </div>
        </div>
      )}

      {error && <p className="text-sm text-red-600">{error}</p>}
    </div>
  );
}
