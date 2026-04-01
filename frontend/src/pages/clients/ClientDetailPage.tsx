import { useState } from 'react';
import { useParams, Link } from 'react-router';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { ArrowLeft, Pencil, Plus, Trash2, MapPin, User } from 'lucide-react';
import { api } from '@/lib/api';
import type { Client, Contact, Address } from '@/lib/types';
import KpiStrip from '@/components/ui/KpiStrip';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';

type Tab = 'info' | 'contacts' | 'addresses' | 'history';

const tabs: { key: Tab; label: string }[] = [
  { key: 'info', label: 'Información' },
  { key: 'contacts', label: 'Contactos' },
  { key: 'addresses', label: 'Direcciones' },
  { key: 'history', label: 'Historial' },
];

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';
const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

interface ContactFormData {
  fullName: string;
  email: string;
  phone: string;
  position: string;
  isPrimary: boolean;
}

interface AddressFormData {
  addressType: string;
  street1: string;
  street2: string;
  exteriorNumber: string;
  interiorNumber: string;
  neighborhood: string;
  city: string;
  stateCode: string;
  postalCode: string;
  countryCode: string;
  isPrimary: boolean;
}

export function ClientDetailPage() {
  const { id } = useParams<{ id: string }>();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState<Tab>('info');
  const [editing, setEditing] = useState(false);
  const [contactModalOpen, setContactModalOpen] = useState(false);
  const [addressModalOpen, setAddressModalOpen] = useState(false);

  const contactForm = useForm<ContactFormData>();
  const addressForm = useForm<AddressFormData>({ defaultValues: { addressType: 'FISCAL', countryCode: 'MEX' } });

  const { data: client, isLoading, error } = useQuery({
    queryKey: ['clients', id],
    queryFn: () => api.get<Client>(`/clients/${id}`),
    enabled: !!id,
  });

  const { data: contacts = [], isLoading: contactsLoading } = useQuery({
    queryKey: ['clients', id, 'contacts'],
    queryFn: () => api.get<Contact[]>(`/clients/${id}/contacts`),
    enabled: !!id && activeTab === 'contacts',
  });

  const { data: addresses = [], isLoading: addressesLoading } = useQuery({
    queryKey: ['clients', id, 'addresses'],
    queryFn: () => api.get<Address[]>(`/clients/${id}/addresses`),
    enabled: !!id && activeTab === 'addresses',
  });

  const createContact = useMutation({
    mutationFn: (data: ContactFormData) => api.post<Contact>(`/clients/${id}/contacts`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clients', id, 'contacts'] });
      setContactModalOpen(false);
      contactForm.reset();
    },
  });

  const deleteContact = useMutation({
    mutationFn: (contactId: string) => api.delete(`/clients/${id}/contacts/${contactId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['clients', id, 'contacts'] }),
  });

  const createAddress = useMutation({
    mutationFn: (data: AddressFormData) => api.post<Address>(`/clients/${id}/addresses`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clients', id, 'addresses'] });
      setAddressModalOpen(false);
      addressForm.reset({ addressType: 'FISCAL', countryCode: 'MEX' });
    },
  });

  const deleteAddress = useMutation({
    mutationFn: (addressId: string) => api.delete(`/clients/${id}/addresses/${addressId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['clients', id, 'addresses'] }),
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
        <div>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">Contactos</h2>
            <Button size="sm" onClick={() => setContactModalOpen(true)}>
              <Plus className="h-4 w-4" /> Agregar contacto
            </Button>
          </div>

          {contactsLoading ? (
            <div className="py-8 text-center text-gray-500">Cargando contactos...</div>
          ) : contacts.length === 0 ? (
            <div className="rounded-lg border border-gray-200 bg-white p-8 text-center">
              <User className="mx-auto mb-3 h-10 w-10 text-gray-300" />
              <p className="text-sm text-gray-500">Aún no hay contactos registrados.</p>
              <Button size="sm" className="mt-3" onClick={() => setContactModalOpen(true)}>
                <Plus className="h-4 w-4" /> Agregar primer contacto
              </Button>
            </div>
          ) : (
            <div className="space-y-3">
              {contacts.map((contact) => (
                <div key={contact.id} className="flex items-center justify-between rounded-lg border border-gray-200 bg-white px-5 py-4">
                  <div className="flex items-center gap-4">
                    <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary-100 text-primary-700">
                      <User className="h-5 w-5" />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-900">
                        {contact.fullName}
                        {contact.isPrimary && (
                          <span className="ml-2 rounded-full bg-primary-100 px-2 py-0.5 text-xs font-medium text-primary-700">Principal</span>
                        )}
                      </p>
                      <p className="text-xs text-gray-500">
                        {contact.position && <span>{contact.position} · </span>}
                        {contact.email && <span>{contact.email} · </span>}
                        {contact.phone && <span>{contact.phone}</span>}
                      </p>
                    </div>
                  </div>
                  <button
                    type="button"
                    onClick={() => deleteContact.mutate(contact.id)}
                    className="text-gray-400 hover:text-red-600 transition-colors"
                    title="Eliminar contacto"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'addresses' && (
        <div>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">Direcciones</h2>
            <Button size="sm" onClick={() => setAddressModalOpen(true)}>
              <Plus className="h-4 w-4" /> Agregar dirección
            </Button>
          </div>

          {addressesLoading ? (
            <div className="py-8 text-center text-gray-500">Cargando direcciones...</div>
          ) : addresses.length === 0 ? (
            <div className="rounded-lg border border-gray-200 bg-white p-8 text-center">
              <MapPin className="mx-auto mb-3 h-10 w-10 text-gray-300" />
              <p className="text-sm text-gray-500">Aún no hay direcciones registradas.</p>
              <Button size="sm" className="mt-3" onClick={() => setAddressModalOpen(true)}>
                <Plus className="h-4 w-4" /> Agregar primera dirección
              </Button>
            </div>
          ) : (
            <div className="space-y-3">
              {addresses.map((addr) => (
                <div key={addr.id} className="flex items-center justify-between rounded-lg border border-gray-200 bg-white px-5 py-4">
                  <div className="flex items-center gap-4">
                    <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100 text-blue-700">
                      <MapPin className="h-5 w-5" />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-900">
                        {addr.addressType === 'FISCAL' ? 'Dirección Fiscal' : addr.addressType}
                        {addr.isPrimary && (
                          <span className="ml-2 rounded-full bg-primary-100 px-2 py-0.5 text-xs font-medium text-primary-700">Principal</span>
                        )}
                      </p>
                      <p className="text-xs text-gray-500">
                        {addr.street1}
                        {addr.exteriorNumber && ` #${addr.exteriorNumber}`}
                        {addr.interiorNumber && ` Int. ${addr.interiorNumber}`}
                        {addr.neighborhood && `, Col. ${addr.neighborhood}`}
                      </p>
                      <p className="text-xs text-gray-500">
                        {addr.city && `${addr.city}, `}
                        {addr.stateCode && `${addr.stateCode} `}
                        C.P. {addr.postalCode}
                      </p>
                    </div>
                  </div>
                  <button
                    type="button"
                    onClick={() => deleteAddress.mutate(addr.id)}
                    className="text-gray-400 hover:text-red-600 transition-colors"
                    title="Eliminar dirección"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'history' && (
        <div className="rounded-lg border border-gray-200 bg-white p-6 text-center text-sm text-gray-500">
          Sin historial disponible.
        </div>
      )}

      {/* Contact Modal */}
      <Modal
        open={contactModalOpen}
        onClose={() => { setContactModalOpen(false); contactForm.reset(); }}
        title="Agregar contacto"
        footer={
          <>
            <Button variant="secondary" onClick={() => { setContactModalOpen(false); contactForm.reset(); }}>
              Cancelar
            </Button>
            <Button onClick={contactForm.handleSubmit((d) => createContact.mutate(d))} disabled={createContact.isPending}>
              {createContact.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
          </>
        }
      >
        <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
          {createContact.error && (
            <div className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">Error al crear el contacto.</div>
          )}
          <div>
            <label className={labelClass}>Nombre completo *</label>
            <input {...contactForm.register('fullName', { required: true })} className={inputClass} placeholder="Juan Pérez García" />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Email</label>
              <input {...contactForm.register('email')} type="email" className={inputClass} placeholder="email@ejemplo.com" />
            </div>
            <div>
              <label className={labelClass}>Teléfono</label>
              <input {...contactForm.register('phone')} className={inputClass} placeholder="55 1234 5678" />
            </div>
          </div>
          <div>
            <label className={labelClass}>Cargo</label>
            <input {...contactForm.register('position')} className={inputClass} placeholder="Director de Compras" />
          </div>
          <div className="flex items-center gap-2">
            <input {...contactForm.register('isPrimary')} type="checkbox" id="contactPrimary" className="h-4 w-4 rounded border-gray-300 text-primary-600" />
            <label htmlFor="contactPrimary" className="text-sm text-gray-700">Contacto principal</label>
          </div>
        </form>
      </Modal>

      {/* Address Modal */}
      <Modal
        open={addressModalOpen}
        onClose={() => { setAddressModalOpen(false); addressForm.reset({ addressType: 'FISCAL', countryCode: 'MEX' }); }}
        title="Agregar dirección"
        footer={
          <>
            <Button variant="secondary" onClick={() => { setAddressModalOpen(false); addressForm.reset({ addressType: 'FISCAL', countryCode: 'MEX' }); }}>
              Cancelar
            </Button>
            <Button onClick={addressForm.handleSubmit((d) => createAddress.mutate(d))} disabled={createAddress.isPending}>
              {createAddress.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
          </>
        }
      >
        <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
          {createAddress.error && (
            <div className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">Error al crear la dirección.</div>
          )}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Tipo de dirección</label>
              <select {...addressForm.register('addressType')} className={inputClass}>
                <option value="FISCAL">Fiscal</option>
                <option value="DELIVERY">Entrega</option>
                <option value="BILLING">Facturación</option>
              </select>
            </div>
            <div>
              <label className={labelClass}>Código postal *</label>
              <input {...addressForm.register('postalCode', { required: true })} className={inputClass} placeholder="06600" />
            </div>
          </div>
          <div>
            <label className={labelClass}>Calle *</label>
            <input {...addressForm.register('street1', { required: true })} className={inputClass} placeholder="Av. Insurgentes Sur" />
          </div>
          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className={labelClass}>No. exterior</label>
              <input {...addressForm.register('exteriorNumber')} className={inputClass} placeholder="1234" />
            </div>
            <div>
              <label className={labelClass}>No. interior</label>
              <input {...addressForm.register('interiorNumber')} className={inputClass} placeholder="Piso 5" />
            </div>
            <div>
              <label className={labelClass}>Colonia</label>
              <input {...addressForm.register('neighborhood')} className={inputClass} placeholder="Del Valle" />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Ciudad</label>
              <input {...addressForm.register('city')} className={inputClass} placeholder="Ciudad de México" />
            </div>
            <div>
              <label className={labelClass}>Estado</label>
              <input {...addressForm.register('stateCode')} className={inputClass} placeholder="CDMX" />
            </div>
          </div>
          <div className="flex items-center gap-2">
            <input {...addressForm.register('isPrimary')} type="checkbox" id="addressPrimary" className="h-4 w-4 rounded border-gray-300 text-primary-600" />
            <label htmlFor="addressPrimary" className="text-sm text-gray-700">Dirección principal</label>
          </div>
        </form>
      </Modal>
    </div>
  );
}
