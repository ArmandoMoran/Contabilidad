import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';
import { useMutation } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Address, Client } from '@/lib/types';
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

const labelClass = 'mb-1 block text-sm font-medium text-gray-700';

export function NewClientPage() {
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ClientFormData>();

  const mutation = useMutation({
    mutationFn: async (data: ClientFormData) => {
      const client = await api.post<Client>('/clients', {
        rfc: data.rfc,
        legalName: data.legalName,
        email: data.email || undefined,
        phone: data.phone || undefined,
        website: data.website || undefined,
        fiscalRegimeCode: data.fiscalRegimeCode,
        defaultUsoCfdiCode: data.defaultUsoCfdiCode || undefined,
        defaultFormaPagoCode: data.defaultFormaPagoCode || undefined,
        defaultPostalCode: data.defaultPostalCode || data.postalCode || undefined,
      });

      const hasAddress = [data.street, data.postalCode, data.city, data.state, data.country]
        .some((value) => value?.trim());

      if (hasAddress && data.street.trim() && data.postalCode.trim()) {
        await api.post<Address>(`/clients/${client.id}/addresses`, {
          addressType: 'FISCAL',
          street1: data.street.trim(),
          exteriorNumber: data.exteriorNumber.trim() || undefined,
          interiorNumber: data.interiorNumber.trim() || undefined,
          neighborhood: data.neighborhood.trim() || undefined,
          city: data.city.trim() || undefined,
          municipalityCode: data.municipality.trim() || undefined,
          stateCode: data.state.trim() || undefined,
          postalCode: data.postalCode.trim(),
          countryCode: (data.country.trim() || 'MEX').toUpperCase(),
          isPrimary: true,
        });
      }

      return client;
    },
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

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Datos fiscales</h2>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div>
              <label htmlFor="rfc" className={labelClass}>RFC *</label>
              <input id="rfc" {...register('rfc', { required: 'RFC es requerido' })} className={inputClass} placeholder="XAXX010101000" />
              {errors.rfc && <p className="mt-1 text-xs text-red-600">{errors.rfc.message}</p>}
            </div>
            <div>
              <label htmlFor="legalName" className={labelClass}>Razon social *</label>
              <input id="legalName" {...register('legalName', { required: 'Razon social es requerida' })} className={inputClass} placeholder="Empresa S.A. de C.V." />
              {errors.legalName && <p className="mt-1 text-xs text-red-600">{errors.legalName.message}</p>}
            </div>
            <div>
              <label htmlFor="email" className={labelClass}>Email</label>
              <input id="email" {...register('email')} type="email" className={inputClass} placeholder="contacto@empresa.com" />
            </div>
            <div>
              <label htmlFor="phone" className={labelClass}>Telefono</label>
              <input id="phone" {...register('phone')} className={inputClass} placeholder="55 1234 5678" />
            </div>
            <div>
              <label htmlFor="website" className={labelClass}>Sitio web</label>
              <input id="website" {...register('website')} className={inputClass} placeholder="https://empresa.com" />
            </div>
            <div>
              <label htmlFor="fiscalRegimeCode" className={labelClass}>Regimen fiscal *</label>
              <select id="fiscalRegimeCode" {...register('fiscalRegimeCode', { required: 'Regimen fiscal es requerido' })} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="601">601 - General de Ley Personas Morales</option>
                <option value="603">603 - Personas Morales con Fines no Lucrativos</option>
                <option value="605">605 - Sueldos y Salarios</option>
                <option value="606">606 - Arrendamiento</option>
                <option value="612">612 - Personas Fisicas con Actividades Empresariales y Profesionales</option>
                <option value="616">616 - Sin obligaciones fiscales</option>
                <option value="620">620 - Sociedades Cooperativas de Produccion</option>
                <option value="621">621 - Incorporacion Fiscal</option>
                <option value="625">625 - Actividades Empresariales por Plataformas Tecnologicas</option>
                <option value="626">626 - Regimen Simplificado de Confianza</option>
              </select>
              {errors.fiscalRegimeCode && <p className="mt-1 text-xs text-red-600">{errors.fiscalRegimeCode.message}</p>}
            </div>
            <div>
              <label htmlFor="defaultUsoCfdiCode" className={labelClass}>Uso CFDI por defecto</label>
              <select id="defaultUsoCfdiCode" {...register('defaultUsoCfdiCode')} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="G01">G01 - Adquisicion de mercancias</option>
                <option value="G03">G03 - Gastos en general</option>
                <option value="S01">S01 - Sin efectos fiscales</option>
              </select>
            </div>
            <div>
              <label htmlFor="defaultFormaPagoCode" className={labelClass}>Forma de pago por defecto</label>
              <select id="defaultFormaPagoCode" {...register('defaultFormaPagoCode')} className={inputClass}>
                <option value="">Seleccionar...</option>
                <option value="01">01 - Efectivo</option>
                <option value="02">02 - Cheque nominativo</option>
                <option value="03">03 - Transferencia electronica</option>
                <option value="04">04 - Tarjeta de credito</option>
                <option value="28">28 - Tarjeta de debito</option>
                <option value="99">99 - Por definir</option>
              </select>
            </div>
            <div>
              <label htmlFor="defaultPostalCode" className={labelClass}>Codigo postal fiscal</label>
              <input id="defaultPostalCode" {...register('defaultPostalCode')} className={inputClass} placeholder="06600" />
            </div>
          </div>
        </section>

        <section className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Direccion</h2>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div className="md:col-span-2">
              <label htmlFor="street" className={labelClass}>Calle</label>
              <input id="street" {...register('street')} className={inputClass} />
            </div>
            <div>
              <label htmlFor="exteriorNumber" className={labelClass}>Numero exterior</label>
              <input id="exteriorNumber" {...register('exteriorNumber')} className={inputClass} />
            </div>
            <div>
              <label htmlFor="interiorNumber" className={labelClass}>Numero interior</label>
              <input id="interiorNumber" {...register('interiorNumber')} className={inputClass} />
            </div>
            <div>
              <label htmlFor="neighborhood" className={labelClass}>Colonia</label>
              <input id="neighborhood" {...register('neighborhood')} className={inputClass} />
            </div>
            <div>
              <label htmlFor="city" className={labelClass}>Ciudad</label>
              <input id="city" {...register('city')} className={inputClass} />
            </div>
            <div>
              <label htmlFor="municipality" className={labelClass}>Municipio</label>
              <input id="municipality" {...register('municipality')} className={inputClass} />
            </div>
            <div>
              <label htmlFor="state" className={labelClass}>Estado</label>
              <input id="state" {...register('state')} className={inputClass} />
            </div>
            <div>
              <label htmlFor="postalCode" className={labelClass}>C.P.</label>
              <input id="postalCode" {...register('postalCode')} className={inputClass} />
            </div>
            <div>
              <label htmlFor="country" className={labelClass}>Pais</label>
              <input id="country" {...register('country')} className={inputClass} defaultValue="MEX" />
            </div>
          </div>
        </section>

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
