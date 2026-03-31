import { useState } from 'react';
import { useParams, Link } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, ChevronDown, ChevronRight } from 'lucide-react';
import { api } from '@/lib/api';
import type { Supplier } from '@/lib/types';
import KpiStrip from '@/components/ui/KpiStrip';

interface MonthlyBreakdown {
  period: string;
  subtotal: number;
  tax: number;
  total: number;
}

export function SupplierDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [expandedMonth, setExpandedMonth] = useState<string | null>(null);

  const { data: supplier, isLoading, error } = useQuery({
    queryKey: ['suppliers', id],
    queryFn: () => api.get<Supplier>(`/suppliers/${id}`),
    enabled: !!id,
  });

  if (isLoading) {
    return <div className="py-12 text-center text-gray-500">Cargando...</div>;
  }

  if (error || !supplier) {
    return (
      <div className="py-12 text-center text-red-600">
        Error al cargar el proveedor. <Link to="/proveedores" className="underline">Volver</Link>
      </div>
    );
  }

  const kpis = [
    { label: 'Total con impuestos', value: '$0.00', trend: 'neutral' as const },
    { label: 'Subtotal', value: '$0.00', trend: 'neutral' as const },
    { label: 'Transacciones', value: '0', trend: 'neutral' as const },
  ];

  const sampleMonths: MonthlyBreakdown[] = [
    { period: 'Marzo 2026', subtotal: 0, tax: 0, total: 0 },
    { period: 'Febrero 2026', subtotal: 0, tax: 0, total: 0 },
    { period: 'Enero 2026', subtotal: 0, tax: 0, total: 0 },
  ];

  return (
    <div>
      <div className="mb-4">
        <Link to="/proveedores" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700">
          <ArrowLeft className="h-4 w-4" /> Proveedores
        </Link>
      </div>

      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">{supplier.legalName}</h1>
        <p className="text-sm text-gray-500">{supplier.rfc}</p>
      </div>

      <KpiStrip items={kpis} />

      {/* Supplier Info */}
      <div className="mb-6 rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Información del proveedor</h2>
        <dl className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">RFC</dt>
            <dd className="mt-1 text-sm text-gray-900">{supplier.rfc}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Razón social</dt>
            <dd className="mt-1 text-sm text-gray-900">{supplier.legalName}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Nombre comercial</dt>
            <dd className="mt-1 text-sm text-gray-900">{supplier.tradeName ?? '—'}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Email</dt>
            <dd className="mt-1 text-sm text-gray-900">{supplier.email ?? '—'}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Teléfono</dt>
            <dd className="mt-1 text-sm text-gray-900">{supplier.phone ?? '—'}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Nacionalidad</dt>
            <dd className="mt-1 text-sm text-gray-900">{supplier.nationality}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Régimen fiscal</dt>
            <dd className="mt-1 text-sm text-gray-900">{supplier.fiscalRegimeCode}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Estado</dt>
            <dd className="mt-1 text-sm text-gray-900">{supplier.active ? 'Activo' : 'Inactivo'}</dd>
          </div>
        </dl>
      </div>

      {/* Monthly Breakdown */}
      <div className="rounded-lg border border-gray-200 bg-white">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-lg font-semibold text-gray-900">Desglose mensual</h2>
        </div>
        <div className="divide-y divide-gray-100">
          {sampleMonths.map((m) => (
            <div key={m.period}>
              <button
                type="button"
                onClick={() => setExpandedMonth(expandedMonth === m.period ? null : m.period)}
                className="flex w-full items-center justify-between px-6 py-4 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
              >
                <span className="font-medium">{m.period}</span>
                <div className="flex items-center gap-4">
                  <span>Total: ${m.total.toFixed(2)}</span>
                  {expandedMonth === m.period ? (
                    <ChevronDown className="h-4 w-4 text-gray-400" />
                  ) : (
                    <ChevronRight className="h-4 w-4 text-gray-400" />
                  )}
                </div>
              </button>
              {expandedMonth === m.period && (
                <div className="border-t border-gray-100 bg-gray-50 px-6 py-4">
                  <dl className="grid grid-cols-3 gap-4 text-sm">
                    <div>
                      <dt className="text-xs text-gray-500">Subtotal</dt>
                      <dd className="font-medium text-gray-900">${m.subtotal.toFixed(2)}</dd>
                    </div>
                    <div>
                      <dt className="text-xs text-gray-500">Impuestos</dt>
                      <dd className="font-medium text-gray-900">${m.tax.toFixed(2)}</dd>
                    </div>
                    <div>
                      <dt className="text-xs text-gray-500">Total</dt>
                      <dd className="font-medium text-gray-900">${m.total.toFixed(2)}</dd>
                    </div>
                  </dl>
                  <p className="mt-3 text-xs text-gray-400">Sin transacciones en este período.</p>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
