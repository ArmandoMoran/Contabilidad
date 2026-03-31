import { useState } from 'react';
import { useParams, Link } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, Pencil } from 'lucide-react';
import { api } from '@/lib/api';
import type { Client } from '@/lib/types';
import KpiStrip from '@/components/ui/KpiStrip';
import Button from '@/components/ui/Button';

type Tab = 'info' | 'contacts' | 'addresses' | 'history';

const tabs: { key: Tab; label: string }[] = [
  { key: 'info', label: 'Información' },
  { key: 'contacts', label: 'Contactos' },
  { key: 'addresses', label: 'Direcciones' },
  { key: 'history', label: 'Historial' },
];

export function ClientDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [activeTab, setActiveTab] = useState<Tab>('info');
  const [editing, setEditing] = useState(false);

  const { data: client, isLoading, error } = useQuery({
    queryKey: ['clients', id],
    queryFn: () => api.get<Client>(`/clients/${id}`),
    enabled: !!id,
  });

  if (isLoading) {
    return <div className="py-12 text-center text-gray-500">Cargando...</div>;
  }

  if (error || !client) {
    return (
      <div className="py-12 text-center text-red-600">
        Error al cargar el cliente. <Link to="/clientes" className="underline">Volver</Link>
      </div>
    );
  }

  const kpis = [
    { label: 'Total facturado', value: '$0.00', trend: 'neutral' as const },
    { label: 'Saldo pendiente', value: '$0.00', trend: 'neutral' as const },
    { label: 'Facturas emitidas', value: '0', trend: 'neutral' as const },
  ];

  return (
    <div>
      <div className="mb-4">
        <Link to="/clientes" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700">
          <ArrowLeft className="h-4 w-4" /> Clientes
        </Link>
      </div>

      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{client.legalName}</h1>
          <p className="text-sm text-gray-500">{client.rfc}</p>
        </div>
        <Button variant="secondary" onClick={() => setEditing(!editing)}>
          <Pencil className="h-4 w-4" />
          {editing ? 'Cancelar edición' : 'Editar'}
        </Button>
      </div>

      <KpiStrip items={kpis} />

      {/* Tabs */}
      <div className="mb-6 border-b border-gray-200">
        <nav className="-mb-px flex gap-6">
          {tabs.map((tab) => (
            <button
              key={tab.key}
              type="button"
              onClick={() => setActiveTab(tab.key)}
              className={`border-b-2 pb-3 text-sm font-medium transition-colors ${
                activeTab === tab.key
                  ? 'border-primary-600 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Tab Content */}
      {activeTab === 'info' && (
        <div className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Datos del cliente</h2>
          <dl className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">RFC</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.rfc}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Razón social</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.legalName}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Nombre comercial</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.tradeName ?? '—'}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Email</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.email ?? '—'}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Teléfono</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.phone ?? '—'}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Sitio web</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.website ?? '—'}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Régimen fiscal</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.fiscalRegimeCode}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Uso CFDI</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.defaultUsoCfdiCode ?? '—'}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Forma de pago</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.defaultFormaPagoCode ?? '—'}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">C.P. fiscal</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.defaultPostalCode ?? '—'}</dd>
            </div>
            <div>
              <dt className="text-xs font-medium uppercase text-gray-500">Estado</dt>
              <dd className="mt-1 text-sm text-gray-900">{client.active ? 'Activo' : 'Inactivo'}</dd>
            </div>
          </dl>
        </div>
      )}

      {activeTab === 'contacts' && (
        <div className="rounded-lg border border-gray-200 bg-white p-6 text-center text-sm text-gray-500">
          Aún no hay contactos registrados.
        </div>
      )}

      {activeTab === 'addresses' && (
        <div className="rounded-lg border border-gray-200 bg-white p-6 text-center text-sm text-gray-500">
          Aún no hay direcciones registradas.
        </div>
      )}

      {activeTab === 'history' && (
        <div className="rounded-lg border border-gray-200 bg-white p-6 text-center text-sm text-gray-500">
          Sin historial disponible.
        </div>
      )}
    </div>
  );
}
