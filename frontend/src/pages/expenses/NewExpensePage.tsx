import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';
import { useMutation } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Expense } from '@/lib/types';
import PageToolbar from '@/components/ui/PageToolbar';
import Button from '@/components/ui/Button';

interface ExpenseFormData {
  description: string;
  expenseType: string;
  total: number;
  currencyCode: string;
  category: string;
  deductible: boolean;
  notes: string;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

export function NewExpensePage() {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors } } = useForm<ExpenseFormData>({
    defaultValues: { expenseType: 'MANUAL', currencyCode: 'MXN', deductible: true },
  });

  const mutation = useMutation({
    mutationFn: (data: ExpenseFormData) => api.post<Expense>('/expenses', data),
    onSuccess: (expense) => navigate(`/gastos/${expense.id}`),
  });

  function onSubmit(data: ExpenseFormData) {
    mutation.mutate({ ...data, total: Number(data.total) });
  }

  return (
    <div>
      <PageToolbar title="Nuevo gasto" />

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
        {mutation.error && (
          <div className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">
            {mutation.error instanceof Error ? mutation.error.message : 'Error al crear el gasto. Verifica los datos e intenta de nuevo.'}
          </div>
        )}

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Datos del gasto</h2>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div className="md:col-span-2">
              <label className={labelClass}>Descripción *</label>
              <input {...register('description', { required: 'Descripción es requerida' })} className={inputClass} placeholder="Descripción del gasto" />
              {errors.description && <p className="mt-1 text-xs text-red-600">{errors.description.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Tipo</label>
              <select {...register('expenseType')} className={inputClass}>
                <option value="MANUAL">Manual</option>
                <option value="CFDI_RECEIVED">CFDI Recibido</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Total *</label>
              <input
                {...register('total', { required: 'Total es requerido', valueAsNumber: true })}
                type="number"
                step="0.01"
                min="0"
                className={inputClass}
                placeholder="0.00"
              />
              {errors.total && <p className="mt-1 text-xs text-red-600">{errors.total.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Moneda</label>
              <select {...register('currencyCode')} className={inputClass}>
                <option value="MXN">MXN - Peso mexicano</option>
                <option value="USD">USD - Dólar americano</option>
                <option value="EUR">EUR - Euro</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Categoría</label>
              <select {...register('category')} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="SERVICIOS">Servicios profesionales</option>
                <option value="MATERIALES">Materiales</option>
                <option value="RENTA">Renta</option>
                <option value="NOMINA">Nómina</option>
                <option value="TRANSPORTE">Transporte</option>
                <option value="ALIMENTACION">Alimentación</option>
                <option value="SOFTWARE">Software y tecnología</option>
                <option value="OTROS">Otros</option>
              </select>
            </div>
            <div className="flex items-center gap-2 pt-6">
              <input {...register('deductible')} type="checkbox" id="deductible" className="h-4 w-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500" />
              <label htmlFor="deductible" className="text-sm text-gray-700">Deducible</label>
            </div>
          </div>
        </section>

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Notas</h2>
          <textarea {...register('notes')} className={inputClass} rows={3} placeholder="Notas adicionales..." />
        </section>

        <div className="flex justify-end gap-2">
          <Button type="button" variant="secondary" onClick={() => navigate('/gastos')}>
            Cancelar
          </Button>
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? 'Guardando...' : 'Guardar gasto'}
          </Button>
        </div>
      </form>
    </div>
  );
}
