import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { api } from '@/lib/api';
import type { Product, PageResponse } from '@/lib/types';
import PageToolbar from '@/components/ui/PageToolbar';
import DataGrid from '@/components/ui/DataGrid';
import Button from '@/components/ui/Button';
import Modal from '@/components/ui/Modal';
import Pagination from '@/components/ui/Pagination';

interface ProductFormData {
  internalName: string;
  description: string;
  satProductCode: string;
  satUnitCode: string;
  unitPrice: number;
  currencyCode: string;
  objetoImpCode: string;
}

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

export function ProductsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);

  const { register, handleSubmit, reset, formState: { errors } } = useForm<ProductFormData>();

  const { data, isLoading } = useQuery({
    queryKey: ['products', page],
    queryFn: () => api.get<PageResponse<Product>>(`/products?page=${page - 1}&size=10`),
  });

  const createMutation = useMutation({
    mutationFn: (data: ProductFormData) => api.post<Product>('/products', data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
      setModalOpen(false);
      reset();
    },
  });

  const columns = [
    { key: 'internalName', header: 'Nombre' },
    { key: 'satProductCode', header: 'Código SAT' },
    { key: 'satUnitCode', header: 'Unidad' },
    {
      key: 'unitPrice',
      header: 'Precio unitario',
      render: (row: Product) => `$${row.unitPrice.toFixed(2)} ${row.currencyCode}`,
    },
    {
      key: 'actions',
      header: 'Acciones',
      render: () => (
        <button type="button" className="text-sm text-primary-600 hover:underline">
          Editar
        </button>
      ),
    },
  ];

  function onSubmit(formData: ProductFormData) {
    createMutation.mutate(formData);
  }

  return (
    <div>
      <PageToolbar
        title="Productos y Servicios"
        actions={<Button onClick={() => setModalOpen(true)}>Nuevo producto</Button>}
      />

      <DataGrid columns={columns} data={data?.items ?? []} isLoading={isLoading} emptyMessage="No se encontraron productos" />
      {data && data.totalPages > 1 && (
        <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
      )}

      <Modal
        open={modalOpen}
        onClose={() => {
          setModalOpen(false);
          reset();
        }}
        title="Nuevo producto"
        footer={
          <>
            <Button variant="secondary" onClick={() => { setModalOpen(false); reset(); }}>
              Cancelar
            </Button>
            <Button onClick={handleSubmit(onSubmit)} disabled={createMutation.isPending}>
              {createMutation.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
          </>
        }
      >
        <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
          {createMutation.error && (
            <div className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">
              Error al crear el producto.
            </div>
          )}
          <div>
            <label className={labelClass}>Nombre *</label>
            <input {...register('internalName', { required: 'Nombre es requerido' })} className={inputClass} />
            {errors.internalName && <p className="mt-1 text-xs text-red-600">{errors.internalName.message}</p>}
          </div>
          <div>
            <label className={labelClass}>Descripción</label>
            <textarea {...register('description')} className={inputClass} rows={2} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Código SAT *</label>
              <input {...register('satProductCode', { required: 'Código SAT es requerido' })} className={inputClass} placeholder="01010101" />
              {errors.satProductCode && <p className="mt-1 text-xs text-red-600">{errors.satProductCode.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Unidad SAT *</label>
              <input {...register('satUnitCode', { required: 'Unidad SAT es requerida' })} className={inputClass} placeholder="E48" />
              {errors.satUnitCode && <p className="mt-1 text-xs text-red-600">{errors.satUnitCode.message}</p>}
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Precio unitario *</label>
              <input
                {...register('unitPrice', { required: 'Precio es requerido', valueAsNumber: true })}
                type="number"
                step="0.01"
                min="0"
                className={inputClass}
              />
              {errors.unitPrice && <p className="mt-1 text-xs text-red-600">{errors.unitPrice.message}</p>}
            </div>
            <div>
              <label className={labelClass}>Moneda</label>
              <select {...register('currencyCode')} className={inputClass} defaultValue="MXN">
                <option value="MXN">MXN - Peso mexicano</option>
                <option value="USD">USD - Dólar americano</option>
                <option value="EUR">EUR - Euro</option>
              </select>
            </div>
          </div>
          <div>
            <label className={labelClass}>Objeto de impuesto</label>
            <select {...register('objetoImpCode')} className={inputClass} defaultValue="02">
              <option value="01">01 - No objeto de impuesto</option>
              <option value="02">02 - Sí objeto de impuesto</option>
              <option value="03">03 - Sí objeto de impuesto y no obligado al desglose</option>
            </select>
          </div>

          <div className="rounded-md border border-dashed border-gray-300 bg-gray-50 p-4 text-center text-xs text-gray-500">
            Perfiles de impuestos — próximamente
          </div>
        </form>
      </Modal>
    </div>
  );
}
