import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';
import { useMutation } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Supplier } from '@/lib/types';
import PageToolbar from '@/components/ui/PageToolbar';
import Button from '@/components/ui/Button';

interface SupplierFormData {
  rfc: string;
  legalName: string;
  tradeName: string;
  email: string;
  phone: string;
  website: string;
  fiscalRegimeCode: string;
  defaultFormaPagoCode: string;
  nationality: string;
  diotOperationType: string;
  notes: string;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

export function NewSupplierPage() {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors } } = useForm<SupplierFormData>();

  const mutation = useMutation({
    mutationFn: (data: SupplierFormData) => api.post<Supplier>('/suppliers', data),
    onSuccess: (supplier) => navigate(`/proveedores/${supplier.id}`),
  });

  function onSubmit(data: SupplierFormData) {
    mutation.mutate(data);
  }

  return (
    <div>
      <PageToolbar title="Nuevo proveedor" />

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
        {mutation.error && (
          <div className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">
            {mutation.error instanceof Error ? mutation.error.message : 'Error al crear el proveedor. Verifica los datos e intenta de nuevo.'}
          </div>
        )}

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Datos fiscales</h2>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div>
              <label className={labelClass}>RFC *</label>
              <input {...register('rfc', { required: 'RFC es requerido' })} className={inputClass} placeholder="XAXX010101000" />
              {errors.rfc && <p className="mt-1 text-xs text-red-600">{errors.rfc.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Razón social *</label>
              <input {...register('legalName', { required: 'Razón social es requerida' })} className={inputClass} placeholder="Empresa S.A. de C.V." />
              {errors.legalName && <p className="mt-1 text-xs text-red-600">{errors.legalName.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Nombre comercial</label>
              <input {...register('tradeName')} className={inputClass} placeholder="Nombre comercial" />
            </div>
            <div>
              <label className={labelClass}>Email</label>
              <input {...register('email')} type="email" className={inputClass} placeholder="contacto@empresa.com" />
            </div>
            <div>
              <label className={labelClass}>Teléfono</label>
              <input {...register('phone')} className={inputClass} placeholder="55 1234 5678" />
            </div>
            <div>
              <label className={labelClass}>Sitio web</label>
              <input {...register('website')} className={inputClass} placeholder="https://empresa.com" />
            </div>
            <div>
              <label className={labelClass}>Régimen fiscal *</label>
              <select {...register('fiscalRegimeCode', { required: 'Régimen fiscal es requerido' })} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="601">601 - General de Ley Personas Morales</option>
                <option value="603">603 - Personas Morales con Fines no Lucrativos</option>
                <option value="605">605 - Sueldos y Salarios</option>
                <option value="606">606 - Arrendamiento</option>
                <option value="612">612 - Personas Físicas con Actividades Empresariales y Profesionales</option>
                <option value="616">616 - Sin obligaciones fiscales</option>
                <option value="621">621 - Incorporación Fiscal</option>
                <option value="625">625 - Régimen de las Actividades Empresariales con ingresos a través de Plataformas Tecnológicas</option>
                <option value="626">626 - Régimen Simplificado de Confianza</option>
              </select>
              {errors.fiscalRegimeCode && <p className="mt-1 text-xs text-red-600">{errors.fiscalRegimeCode.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Forma de pago por defecto</label>
              <select {...register('defaultFormaPagoCode')} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="01">01 - Efectivo</option>
                <option value="02">02 - Cheque nominativo</option>
                <option value="03">03 - Transferencia electrónica</option>
                <option value="04">04 - Tarjeta de crédito</option>
                <option value="28">28 - Tarjeta de débito</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Nacionalidad</label>
              <select {...register('nationality')} className={inputClass} defaultValue="NATIONAL">
                <option value="NATIONAL">Nacional</option>
                <option value="FOREIGN">Extranjero</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Tipo de operación DIOT</label>
              <select {...register('diotOperationType')} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="85">85 - Prestación de servicios profesionales</option>
                <option value="06">06 - Arrendamiento de inmuebles</option>
                <option value="03">03 - Otros</option>
              </select>
            </div>
          </div>
        </section>

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Notas</h2>
          <textarea {...register('notes')} className={inputClass} rows={3} placeholder="Notas adicionales..." />
        </section>

        <div className="flex justify-end gap-2">
          <Button type="button" variant="secondary" onClick={() => navigate('/proveedores')}>
            Cancelar
          </Button>
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? 'Guardando...' : 'Guardar proveedor'}
          </Button>
        </div>
      </form>
    </div>
  );
}
