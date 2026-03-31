import { useParams, Link } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft } from 'lucide-react';
import { api } from '@/lib/api';
import type { Expense } from '@/lib/types';
import StatusBadge from '@/components/ui/StatusBadge';

export function ExpenseDetailPage() {
  const { id } = useParams<{ id: string }>();

  const { data: expense, isLoading, error } = useQuery({
    queryKey: ['expenses', id],
    queryFn: () => api.get<Expense>(`/expenses/${id}`),
    enabled: !!id,
  });

  if (isLoading) {
    return <div className="py-12 text-center text-gray-500">Cargando...</div>;
  }

  if (error || !expense) {
    return (
      <div className="py-12 text-center text-red-600">
        Error al cargar el gasto. <Link to="/gastos" className="underline">Volver</Link>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-4">
        <Link to="/gastos" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700">
          <ArrowLeft className="h-4 w-4" /> Gastos
        </Link>
      </div>

      <div className="mb-6 flex items-center gap-3">
        <h1 className="text-2xl font-bold text-gray-900">Detalle del gasto</h1>
        <StatusBadge status={expense.status} />
      </div>

      {/* Info */}
      <div className="mb-6 rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Información general</h2>
        <dl className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Proveedor</dt>
            <dd className="mt-1 text-sm text-gray-900">{expense.issuerName ?? '—'}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">RFC</dt>
            <dd className="mt-1 text-sm text-gray-900">{expense.issuerRfc ?? '—'}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Tipo</dt>
            <dd className="mt-1 text-sm text-gray-900">{expense.expenseType}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Categoría</dt>
            <dd className="mt-1 text-sm text-gray-900">{expense.category ?? '—'}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Subtotal</dt>
            <dd className="mt-1 text-sm text-gray-900">${expense.subtotal.toFixed(2)}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium uppercase text-gray-500">Total</dt>
            <dd className="mt-1 text-sm font-bold text-gray-900">${expense.total.toFixed(2)}</dd>
          </div>
        </dl>
      </div>

      {/* Lines placeholder */}
      <div className="mb-6 rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Conceptos</h2>
        <p className="text-sm text-gray-400">Los conceptos se mostrarán cuando la API provea este detalle.</p>
      </div>

      {/* Tax breakdown placeholder */}
      <div className="mb-6 rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Desglose de impuestos</h2>
        <p className="text-sm text-gray-400">El desglose se mostrará cuando la API provea este detalle.</p>
      </div>

      {/* Attachments */}
      <div className="rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Archivos adjuntos</h2>
        <p className="text-sm text-gray-400">Sin archivos adjuntos.</p>
      </div>
    </div>
  );
}
