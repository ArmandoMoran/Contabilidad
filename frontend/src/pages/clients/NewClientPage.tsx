import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';
import { useMutation } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Client } from '@/lib/types';
import PageToolbar from '@/components/ui/PageToolbar';
import Button from '@/components/ui/Button';

interface ClientFormData {
  rfc: string;
  legalName: string;
  email: string;
  phone: string;
  website: string;
  fiscalRegimeCode: string;
  defaultUsoCfdiCode: string;
  defaultFormaPagoCode: string;
  defaultPostalCode: string;
  street: string;
  exteriorNumber: string;
  interiorNumber: string;
  neighborhood: string;
  city: string;
  municipality: string;
  state: string;
  postalCode: string;
  country: string;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

export function NewClientPage() {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors } } = useForm<ClientFormData>();

  const mutation = useMutation({
    mutationFn: (data: ClientFormData) => api.post<Client>('/clients', data),
    onSuccess: (client) => navigate(`/clientes/${client.id}`),
  });

  function onSubmit(data: ClientFormData) {
    mutation.mutate(data);
  }

  return (
    <div>
      <PageToolbar title="Nuevo cliente" />

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
        {mutation.error && (
          <div className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">
            Error al crear el cliente. Verifica los datos e intenta de nuevo.
          </div>
        )}

        {/* Datos fiscales */}
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
                <option value="620">620 - Sociedades Cooperativas de Producción</option>
                <option value="621">621 - Incorporación Fiscal</option>
                <option value="625">625 - Régimen de las Actividades Empresariales con ingresos a través de Plataformas Tecnológicas</option>
                <option value="626">626 - Régimen Simplificado de Confianza</option>
              </select>
              {errors.fiscalRegimeCode && <p className="mt-1 text-xs text-red-600">{errors.fiscalRegimeCode.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Uso CFDI por defecto</label>
              <select {...register('defaultUsoCfdiCode')} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="G01">G01 - Adquisición de mercancías</option>
                <option value="G03">G03 - Gastos en general</option>
                <option value="P01">P01 - Por definir</option>
                <option value="S01">S01 - Sin efectos fiscales</option>
              </select>
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
                <option value="99">99 - Por definir</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Código postal fiscal</label>
              <input {...register('defaultPostalCode')} className={inputClass} placeholder="06600" />
            </div>
          </div>
        </section>

        {/* Dirección */}
        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Dirección</h2>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div className="md:col-span-2">
              <label className={labelClass}>Calle</label>
              <input {...register('street')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>Número exterior</label>
              <input {...register('exteriorNumber')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>Número interior</label>
              <input {...register('interiorNumber')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>Colonia</label>
              <input {...register('neighborhood')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>Ciudad</label>
              <input {...register('city')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>Municipio</label>
              <input {...register('municipality')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>Estado</label>
              <input {...register('state')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>C.P.</label>
              <input {...register('postalCode')} className={inputClass} />
            </div>
            <div>
              <label className={labelClass}>País</label>
              <input {...register('country')} className={inputClass} defaultValue="MEX" />
            </div>
          </div>
        </section>

        {/* Actions */}
        <div className="flex items-center justify-end gap-3">
          <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
            Cancelar
          </Button>
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? 'Guardando...' : 'Guardar cliente'}
          </Button>
        </div>
      </form>
    </div>
  );
}
