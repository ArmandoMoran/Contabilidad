import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { RefreshCw, CheckCircle, AlertCircle } from 'lucide-react';
import { api } from '@/lib/api';
import Button from '@/components/ui/Button';

interface CatalogItem {
  code: string;
  name: string;
  lastSync: string | null;
  count: number;
}

const catalogs: CatalogItem[] = [
  { code: 'c_FormaPago', name: 'Formas de pago', lastSync: '2026-03-15', count: 24 },
  { code: 'c_MetodoPago', name: 'Métodos de pago', lastSync: '2026-03-15', count: 2 },
  { code: 'c_Moneda', name: 'Monedas', lastSync: '2026-03-15', count: 178 },
  { code: 'c_RegimenFiscal', name: 'Regímenes fiscales', lastSync: '2026-03-15', count: 19 },
  { code: 'c_UsoCFDI', name: 'Usos de CFDI', lastSync: '2026-03-15', count: 22 },
  { code: 'c_ClaveProdServ', name: 'Productos y servicios', lastSync: '2026-03-10', count: 52987 },
  { code: 'c_ClaveUnidad', name: 'Unidades de medida', lastSync: '2026-03-10', count: 2354 },
  { code: 'c_CodigoPostal', name: 'Códigos postales', lastSync: '2026-03-10', count: 65000 },
  { code: 'c_ObjetoImp', name: 'Objeto de impuesto', lastSync: '2026-03-15', count: 3 },
];

export function CatalogSettingsPage() {
  const [syncingCatalog, setSyncingCatalog] = useState<string | null>(null);

  const syncMutation = useMutation({
    mutationFn: (catalogCode: string) => {
      setSyncingCatalog(catalogCode);
      return api.post(`/sat-catalogs/sync`, { catalog: catalogCode });
    },
    onSettled: () => setSyncingCatalog(null),
  });

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Catálogos del SAT</h1>

      <div className="rounded-lg border border-gray-200 bg-white">
        <div className="border-b border-gray-200 px-6 py-4">
          <p className="text-sm text-gray-500">
            Los catálogos del SAT se sincronizan automáticamente. Puedes forzar una sincronización manual si es necesario.
          </p>
        </div>

        <div className="divide-y divide-gray-100">
          {catalogs.map((cat) => (
            <div key={cat.code} className="flex items-center justify-between px-6 py-4">
              <div className="flex items-center gap-3">
                {cat.lastSync ? (
                  <CheckCircle className="h-5 w-5 text-green-500" />
                ) : (
                  <AlertCircle className="h-5 w-5 text-yellow-500" />
                )}
                <div>
                  <p className="text-sm font-medium text-gray-900">{cat.name}</p>
                  <p className="text-xs text-gray-500">
                    {cat.code} · {cat.count.toLocaleString('es-MX')} registros
                  </p>
                </div>
              </div>

              <div className="flex items-center gap-4">
                <span className="text-xs text-gray-400">
                  {cat.lastSync
                    ? `Última sincronización: ${new Date(cat.lastSync).toLocaleDateString('es-MX')}`
                    : 'Sin sincronizar'}
                </span>
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={syncingCatalog === cat.code}
                  onClick={() => syncMutation.mutate(cat.code)}
                >
                  <RefreshCw className={`h-3.5 w-3.5 ${syncingCatalog === cat.code ? 'animate-spin' : ''}`} />
                  {syncingCatalog === cat.code ? 'Sincronizando...' : 'Sincronizar'}
                </Button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {syncMutation.error && (
        <div className="mt-4 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">
          Error al sincronizar el catálogo. Intenta de nuevo.
        </div>
      )}

      {syncMutation.isSuccess && (
        <div className="mt-4 rounded-md bg-green-50 px-4 py-3 text-sm text-green-700">
          Catálogo sincronizado correctamente.
        </div>
      )}
    </div>
  );
}
