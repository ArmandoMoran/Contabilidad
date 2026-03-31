import { useForm } from 'react-hook-form';
import { useMutation } from '@tanstack/react-query';
import { api } from '@/lib/api';
import Button from '@/components/ui/Button';

interface CompanyFormData {
  rfc: string;
  legalName: string;
  personType: string;
  fiscalRegimeCode: string;
  fiscalZone: string;
  postalCode: string;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

export function CompanySettingsPage() {
  const { register, handleSubmit } = useForm<CompanyFormData>();

  const mutation = useMutation({
    mutationFn: (data: CompanyFormData) => api.patch('/company', data),
  });

  function onSubmit(data: CompanyFormData) {
    mutation.mutate(data);
  }

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Configuración de empresa</h1>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
        {mutation.isSuccess && (
          <div className="rounded-md bg-green-50 px-4 py-3 text-sm text-green-700">
            Configuración guardada correctamente.
          </div>
        )}
        {mutation.error && (
          <div className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">
            Error al guardar la configuración.
          </div>
        )}

        {/* Company Info */}
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Información fiscal</h2>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div>
              <label className={labelClass}>RFC</label>
              <input {...register('rfc')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>Razón social</label>
              <input {...register('legalName')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>Tipo de persona</label>
              <select {...register('personType')} className={inputClass}>
                <option value="MORAL">Persona Moral</option>
                <option value="FISICA">Persona Física</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Régimen fiscal</label>
              <select {...register('fiscalRegimeCode')} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="601">601 - General de Ley Personas Morales</option>
                <option value="603">603 - Personas Morales con Fines no Lucrativos</option>
                <option value="605">605 - Sueldos y Salarios</option>
                <option value="606">606 - Arrendamiento</option>
                <option value="612">612 - Personas Físicas con Actividades Empresariales y Profesionales</option>
                <option value="621">621 - Incorporación Fiscal</option>
                <option value="626">626 - Régimen Simplificado de Confianza</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Zona fiscal</label>
              <input {...register('fiscalZone')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>C.P.</label>
              <input {...register('postalCode')} className={inputClass} placeholder="06600" />
            </div>
          </div>
        </section>

        {/* CSD */}
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Certificados (CSD)</h2>
          <div className="flex h-32 items-center justify-center rounded-md border border-dashed border-gray-300 bg-gray-50">
            <p className="text-sm text-gray-400">Carga de certificados CSD — próximamente</p>
          </div>
        </section>

        {/* PAC */}
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Configuración PAC</h2>
          <div className="flex h-32 items-center justify-center rounded-md border border-dashed border-gray-300 bg-gray-50">
            <p className="text-sm text-gray-400">Configuración del proveedor de timbrado — próximamente</p>
          </div>
        </section>

        <div className="flex justify-end">
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? 'Guardando...' : 'Guardar configuración'}
          </Button>
        </div>
      </form>
    </div>
  );
}
