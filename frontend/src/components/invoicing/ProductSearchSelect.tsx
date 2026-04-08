import { useDeferredValue, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { PackageSearch, X } from 'lucide-react';
import { invoiceApi } from '@/lib/invoice-api';
import type { InvoiceProductOption } from '@/lib/invoice-types';

interface ProductSearchSelectProps {
  value: InvoiceProductOption | null;
  onSelect: (product: InvoiceProductOption) => void;
  onClear: () => void;
  error?: string;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 pl-9 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

export default function ProductSearchSelect({ value, onSelect, onClear, error }: ProductSearchSelectProps) {
  const [search, setSearch] = useState('');
  const [open, setOpen] = useState(false);
  const deferredSearch = useDeferredValue(search);

  const { data, isLoading } = useQuery({
    queryKey: ['invoice-product-search', deferredSearch],
    queryFn: () => invoiceApi.searchProducts(deferredSearch),
  });

  const options = data?.items ?? [];

  return (
    <div className="space-y-2">
      <div className="relative">
        <PackageSearch className="pointer-events-none absolute left-3 top-3 h-4 w-4 text-gray-400" />
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
          placeholder={value ? 'Buscar otro producto...' : 'Busca por nombre, clave interna, descripción o código SAT'}
          aria-label="Buscar producto"
        />
        {value && (
          <button
            type="button"
            onClick={onClear}
            className="absolute right-3 top-3 text-gray-400 transition-colors hover:text-gray-600"
            aria-label="Limpiar producto"
          >
            <X className="h-4 w-4" />
          </button>
        )}
      </div>

      {value && (
        <div className="rounded-md border border-gray-200 bg-gray-50 px-3 py-2 text-sm">
          <p className="font-medium text-gray-900">{value.internalName}</p>
          <p className="text-gray-500">
            {value.internalCode ? `${value.internalCode} · ` : ''}
            {value.satProductCode} · {value.satUnitCode}
          </p>
        </div>
      )}

      {open && (
        <div className="max-h-72 overflow-y-auto rounded-lg border border-gray-200 bg-white shadow-sm">
          {isLoading ? (
            <p className="px-3 py-3 text-sm text-gray-500">Buscando productos...</p>
          ) : options.length === 0 ? (
            <p className="px-3 py-3 text-sm text-gray-500">No hay coincidencias para esta búsqueda.</p>
          ) : (
            options.map((product) => (
              <button
                key={product.id}
                type="button"
                onMouseDown={(event) => {
                  event.preventDefault();
                  onSelect(product);
                  setSearch('');
                  setOpen(false);
                }}
                className="block w-full border-b border-gray-100 px-3 py-3 text-left last:border-b-0 hover:bg-gray-50"
              >
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="text-sm font-medium text-gray-900">{product.internalName}</p>
                    <p className="text-xs text-gray-500">
                      {product.internalCode ? `${product.internalCode} · ` : ''}
                      {product.satProductCode} · {product.satUnitCode}
                    </p>
                    {product.description && <p className="mt-1 text-xs text-gray-500">{product.description}</p>}
                  </div>
                  <span className="text-sm font-medium text-gray-900">
                    ${product.unitPrice.toFixed(2)} {product.currencyCode}
                  </span>
                </div>
              </button>
            ))
          )}
        </div>
      )}

      {error && <p className="text-sm text-red-600">{error}</p>}
    </div>
  );
}
