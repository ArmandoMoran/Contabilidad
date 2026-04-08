import { useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { useFieldArray, useForm } from 'react-hook-form';
import { useMutation, useQuery } from '@tanstack/react-query';
import { Plus, ArrowLeft } from 'lucide-react';
import PageToolbar from '@/components/ui/PageToolbar';
import Button from '@/components/ui/Button';
import ClientSearchSelect from '@/components/invoicing/ClientSearchSelect';
import InvoiceLineEditor from '@/components/invoicing/InvoiceLineEditor';
import InvoiceSummaryCard from '@/components/invoicing/InvoiceSummaryCard';
import ValidationAlertList from '@/components/invoicing/ValidationAlertList';
import { ApiError } from '@/lib/api';
import { invoiceApi } from '@/lib/invoice-api';
import type {
  CatalogOption,
  InvoiceClientOption,
  InvoiceFormValues,
  InvoiceProductOption,
  InvoiceValidationIssue,
  InvoiceValidationResult,
} from '@/lib/invoice-types';
import {
  calculateLocalLineSummary,
  calculateLocalInvoiceSummary,
  createEmptyInvoiceLine,
  invoiceFieldPathToFormPath,
  mapInvoiceFormToDraftRequest,
} from '@/lib/invoice-utils';

const inputClass =
  'block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

const labelClass = 'mb-1 block text-sm font-medium text-gray-700';

const defaultCatalogs = {
  usoCfdi: [{ code: 'G03', description: 'Gastos en general' }],
  formaPago: [{ code: '03', description: 'Transferencia electrónica de fondos' }],
  metodoPago: [{ code: 'PUE', description: 'Pago en una sola exhibición' }],
  monedas: [{ code: 'MXN', description: 'Peso mexicano' }],
} satisfies Record<string, CatalogOption[]>;

export function NewInvoicePage() {
  const navigate = useNavigate();
  const [selectedClient, setSelectedClient] = useState<InvoiceClientOption | null>(null);
  const [selectedProducts, setSelectedProducts] = useState<Record<string, InvoiceProductOption | null>>({});
  const [validationResult, setValidationResult] = useState<InvoiceValidationResult | null>(null);
  const [generalError, setGeneralError] = useState<string | null>(null);

  const {
    control,
    watch,
    setValue,
    getValues,
    setError,
    clearErrors,
    handleSubmit,
    formState: { errors },
  } = useForm<InvoiceFormValues>({
    defaultValues: {
      clientId: '',
      invoiceType: 'I',
      series: '',
      folio: '',
      paymentMethodCode: 'PUE',
      paymentFormCode: '03',
      usoCfdiCode: 'G03',
      currencyCode: 'MXN',
      lines: [createEmptyInvoiceLine()],
    },
  });

  const { fields: lineFields, append, remove } = useFieldArray({
    control,
    name: 'lines',
  });
  const values = watch();
  const localSummary = useMemo(() => calculateLocalInvoiceSummary(values.lines ?? []), [values.lines]);

  const { data: currentUser } = useQuery({
    queryKey: ['auth', 'me'],
    queryFn: () => invoiceApi.getCurrentUser(),
  });

  const { data: company } = useQuery({
    queryKey: ['companies', currentUser?.companyId],
    queryFn: () => invoiceApi.getCompany(currentUser!.companyId),
    enabled: !!currentUser?.companyId,
  });

  const { data: usoCfdiOptions = defaultCatalogs.usoCfdi } = useQuery({
    queryKey: ['catalogs', 'uso-cfdi'],
    queryFn: () => invoiceApi.listUsoCfdi(),
  });

  const { data: formaPagoOptions = defaultCatalogs.formaPago } = useQuery({
    queryKey: ['catalogs', 'forma-pago'],
    queryFn: () => invoiceApi.listFormaPago(),
  });

  const { data: metodoPagoOptions = defaultCatalogs.metodoPago } = useQuery({
    queryKey: ['catalogs', 'metodo-pago'],
    queryFn: () => invoiceApi.listMetodoPago(),
  });

  const { data: monedaOptions = defaultCatalogs.monedas } = useQuery({
    queryKey: ['catalogs', 'moneda'],
    queryFn: () => invoiceApi.listMonedas(),
  });

  const validateMutation = useMutation({
    mutationFn: invoiceApi.validateDraft,
    onSuccess: (result) => {
      clearAllServerFeedback();
      setValidationResult(result);
      applyValidationIssues(result.issues);

      if (result.preview) {
        setValue('series', result.preview.series ?? getValues('series'));
        setValue('folio', result.preview.folio ?? getValues('folio'));
        setValue('paymentMethodCode', result.preview.paymentMethodCode ?? getValues('paymentMethodCode'));
        setValue('paymentFormCode', result.preview.paymentFormCode ?? getValues('paymentFormCode'));
        setValue('usoCfdiCode', result.preview.usoCfdiCode ?? getValues('usoCfdiCode'));
        setValue('currencyCode', result.preview.currencyCode ?? getValues('currencyCode'));
      }
    },
    onError: handleMutationError,
  });

  const draftMutation = useMutation({
    mutationFn: invoiceApi.createDraft,
    onSuccess: (invoice) => navigate(`/facturacion/${invoice.id}`),
    onError: handleMutationError,
  });

  const stampMutation = useMutation({
    mutationFn: invoiceApi.createStamped,
    onSuccess: (invoice) => navigate(`/facturacion/${invoice.id}`),
    onError: handleMutationError,
  });

  const isBusy = validateMutation.isPending || draftMutation.isPending || stampMutation.isPending;

  function clearAllServerFeedback() {
    clearErrors();
    setGeneralError(null);
    setValidationResult(null);
  }

  function setFormValue<K extends keyof InvoiceFormValues>(field: K, value: InvoiceFormValues[K]) {
    clearAllServerFeedback();
    setValue(field as never, value as never, { shouldDirty: true, shouldValidate: false });
  }

  function setLineValue(index: number, field: keyof InvoiceFormValues['lines'][number], value: string | number) {
    clearAllServerFeedback();
    setValue(`lines.${index}.${field}` as const, value as never, { shouldDirty: true, shouldValidate: false });
  }

  function handleClientSelect(client: InvoiceClientOption) {
    clearAllServerFeedback();
    setSelectedClient(client);
    setValue('clientId', client.id, { shouldDirty: true });
    setValue('usoCfdiCode', client.defaultUsoCfdiCode || 'G03', { shouldDirty: true });
    setValue('paymentFormCode', client.defaultFormaPagoCode || '03', { shouldDirty: true });
    setValue('paymentMethodCode', client.defaultMetodoPagoCode || 'PUE', { shouldDirty: true });
  }

  function handleClientClear() {
    clearAllServerFeedback();
    setSelectedClient(null);
    setValue('clientId', '', { shouldDirty: true });
  }

  function handleProductSelect(lineId: string, index: number, product: InvoiceProductOption) {
    clearAllServerFeedback();
    setSelectedProducts((previous) => ({ ...previous, [lineId]: product }));
    setValue(`lines.${index}.productId`, product.id, { shouldDirty: true });
    setValue(`lines.${index}.description`, product.description || product.internalName, { shouldDirty: true });
    setValue(`lines.${index}.unitPrice`, product.unitPrice, { shouldDirty: true });
    setValue(`lines.${index}.satProductCode`, product.satProductCode, { shouldDirty: true });
    setValue(`lines.${index}.satUnitCode`, product.satUnitCode, { shouldDirty: true });
    setValue(`lines.${index}.objetoImpCode`, product.objetoImpCode, { shouldDirty: true });
    setValue(`lines.${index}.currencyCode`, product.currencyCode, { shouldDirty: true });

    if (!getValues('currencyCode') || getValues('currencyCode') === 'MXN') {
      setValue('currencyCode', product.currencyCode, { shouldDirty: true });
    }
  }

  function handleProductClear(lineId: string, index: number) {
    clearAllServerFeedback();
    setSelectedProducts((previous) => ({ ...previous, [lineId]: null }));
    setValue(`lines.${index}.productId`, '', { shouldDirty: true });
    setValue(`lines.${index}.satProductCode`, '', { shouldDirty: true });
    setValue(`lines.${index}.satUnitCode`, '', { shouldDirty: true });
    setValue(`lines.${index}.objetoImpCode`, '', { shouldDirty: true });
    setValue(`lines.${index}.currencyCode`, '', { shouldDirty: true });
  }

  function addLine() {
    clearAllServerFeedback();
    append(createEmptyInvoiceLine());
  }

  function removeLine(index: number, lineId: string) {
    clearAllServerFeedback();
    remove(index);
    setSelectedProducts((previous) => {
      const next = { ...previous };
      delete next[lineId];
      return next;
    });
  }

  function applyValidationIssues(issues: InvoiceValidationIssue[]) {
    issues.forEach((issue) => {
      const formPath = invoiceFieldPathToFormPath(issue.fieldPath);
      if (formPath && !['subtotal', 'discount', 'total', 'transferredTaxTotal', 'withheldTaxTotal'].includes(formPath)) {
        setError(formPath as never, { type: 'server', message: issue.message });
      }
    });
  }

  function handleMutationError(error: unknown) {
    clearErrors();
    if (error instanceof ApiError) {
      error.fieldErrors?.forEach((fieldError) => {
        const formPath = invoiceFieldPathToFormPath(fieldError.field);
        if (formPath) {
          setError(formPath as never, { type: 'server', message: fieldError.message });
        }
      });
      setGeneralError(error.message);
      return;
    }

    setGeneralError('Ocurrió un error inesperado al procesar la factura.');
  }

  function validateClientSide(formValues: InvoiceFormValues) {
    clearErrors();
    const issues: InvoiceValidationIssue[] = [];

    if (!formValues.clientId) {
      issues.push({ fieldPath: 'clientId', message: 'Selecciona un cliente.', code: 'CLIENT_REQUIRED' });
    }

    if (!formValues.lines.length) {
      issues.push({ fieldPath: 'lines', message: 'Agrega al menos un concepto.', code: 'LINES_REQUIRED' });
    }

    formValues.lines.forEach((line, index) => {
      if (!line.productId) {
        issues.push({ fieldPath: `lines[${index}].productId`, message: 'Selecciona un producto.', code: 'PRODUCT_REQUIRED' });
      }
      if (!line.description?.trim()) {
        issues.push({ fieldPath: `lines[${index}].description`, message: 'La descripción es obligatoria.', code: 'DESCRIPTION_REQUIRED' });
      }
      if (Number(line.quantity) <= 0) {
        issues.push({ fieldPath: `lines[${index}].quantity`, message: 'La cantidad debe ser mayor a cero.', code: 'INVALID_QUANTITY' });
      }
      if (Number(line.unitPrice) <= 0) {
        issues.push({ fieldPath: `lines[${index}].unitPrice`, message: 'El precio unitario debe ser mayor a cero.', code: 'INVALID_UNIT_PRICE' });
      }
      if (Number(line.discount) < 0) {
        issues.push({ fieldPath: `lines[${index}].discount`, message: 'El descuento no puede ser negativo.', code: 'INVALID_DISCOUNT' });
      }
    });

    applyValidationIssues(issues);
    if (issues.length) {
      setValidationResult({ issues, valid: false, preview: null });
      return false;
    }

    return true;
  }

  const onValidate = handleSubmit(async (formValues) => {
    clearAllServerFeedback();
    if (!validateClientSide(formValues)) {
      return;
    }
    try {
      await validateMutation.mutateAsync(mapInvoiceFormToDraftRequest(formValues));
    } catch {}
  });

  const onSaveDraft = handleSubmit(async (formValues) => {
    clearAllServerFeedback();
    if (!validateClientSide(formValues)) {
      return;
    }
    try {
      await draftMutation.mutateAsync(mapInvoiceFormToDraftRequest(formValues));
    } catch {}
  });

  const onStamp = handleSubmit(async (formValues) => {
    clearAllServerFeedback();
    if (!validateClientSide(formValues)) {
      return;
    }
    try {
      await stampMutation.mutateAsync(mapInvoiceFormToDraftRequest(formValues));
    } catch {}
  });

  return (
    <div className="space-y-6">
      <div>
        <Link to="/facturacion" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700">
          <ArrowLeft className="h-4 w-4" />
          Volver a facturación
        </Link>
      </div>

      <PageToolbar title="Generar factura" />

      <section className="rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Emisor</h2>
        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div>
            <p className="text-sm text-gray-500">Razón social</p>
            <p className="font-medium text-gray-900">{company?.legalName ?? 'Cargando empresa...'}</p>
          </div>
          <div>
            <p className="text-sm text-gray-500">RFC</p>
            <p className="font-medium text-gray-900">{company?.rfc ?? '—'}</p>
          </div>
          <div>
            <p className="text-sm text-gray-500">Régimen fiscal</p>
            <p className="font-medium text-gray-900">{company?.fiscalRegimeCode ?? '—'}</p>
          </div>
        </div>
      </section>

      <section className="rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Cliente / Receptor</h2>
        <ClientSearchSelect
          value={selectedClient}
          onSelect={handleClientSelect}
          onClear={handleClientClear}
          error={errors.clientId?.message}
        />
        {selectedClient?.defaultPostalCode && (
          <p className="mt-3 text-sm text-gray-500">Código postal fiscal sugerido: {selectedClient.defaultPostalCode}</p>
        )}
      </section>

      <section className="rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Datos del CFDI</h2>
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
          <div>
            <label htmlFor="invoice-type" className={labelClass}>Tipo de comprobante</label>
            <select id="invoice-type" value={values.invoiceType} onChange={(event) => setFormValue('invoiceType', event.target.value)} className={inputClass}>
              <option value="I">I - Ingreso</option>
              <option value="E">E - Egreso</option>
            </select>
          </div>
          <div>
            <label htmlFor="invoice-series" className={labelClass}>Serie</label>
            <input id="invoice-series" value={values.series} onChange={(event) => setFormValue('series', event.target.value)} className={inputClass} placeholder="A" />
          </div>
          <div>
            <label htmlFor="invoice-folio" className={labelClass}>Folio</label>
            <input id="invoice-folio" value={values.folio} onChange={(event) => setFormValue('folio', event.target.value)} className={inputClass} placeholder="Se asigna automáticamente si lo dejas vacío" />
          </div>
          <div>
            <label htmlFor="invoice-currency" className={labelClass}>Moneda</label>
            <select id="invoice-currency" value={values.currencyCode} onChange={(event) => setFormValue('currencyCode', event.target.value)} className={inputClass}>
              {monedaOptions.map((option) => (
                <option key={option.code} value={option.code}>{option.code} - {option.description}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="invoice-payment-method" className={labelClass}>Método de pago</label>
            <select id="invoice-payment-method" value={values.paymentMethodCode} onChange={(event) => setFormValue('paymentMethodCode', event.target.value)} className={inputClass}>
              {metodoPagoOptions.map((option) => (
                <option key={option.code} value={option.code}>{option.code} - {option.description}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="invoice-payment-form" className={labelClass}>Forma de pago</label>
            <select id="invoice-payment-form" value={values.paymentFormCode} onChange={(event) => setFormValue('paymentFormCode', event.target.value)} className={inputClass}>
              {formaPagoOptions.map((option) => (
                <option key={option.code} value={option.code}>{option.code} - {option.description}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="invoice-uso-cfdi" className={labelClass}>Uso CFDI</label>
            <select id="invoice-uso-cfdi" value={values.usoCfdiCode} onChange={(event) => setFormValue('usoCfdiCode', event.target.value)} className={inputClass}>
              {usoCfdiOptions.map((option) => (
                <option key={option.code} value={option.code}>{option.code} - {option.description}</option>
              ))}
            </select>
          </div>
        </div>
      </section>

      <section className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">Conceptos</h2>
          <Button variant="secondary" size="sm" onClick={addLine}>
            <Plus className="h-4 w-4" />
            Agregar concepto
          </Button>
        </div>

        {lineFields.map((field, index) => (
          <InvoiceLineEditor
            key={field.id}
            index={index}
            line={values.lines[index] ?? createEmptyInvoiceLine()}
            localSummary={calculateLocalLineSummary(values.lines[index] ?? createEmptyInvoiceLine())}
            selectedProduct={selectedProducts[field.id] ?? null}
            errors={{
              productId: errors.lines?.[index]?.productId?.message,
              description: errors.lines?.[index]?.description?.message,
              quantity: errors.lines?.[index]?.quantity?.message,
              unitPrice: errors.lines?.[index]?.unitPrice?.message,
              discount: errors.lines?.[index]?.discount?.message,
            }}
            onSelectProduct={(product) => handleProductSelect(field.id, index, product)}
            onClearProduct={() => handleProductClear(field.id, index)}
            onChange={(lineField, value) => setLineValue(index, lineField, value)}
            onRemove={() => removeLine(index, field.id)}
            canRemove={lineFields.length > 1}
          />
        ))}
      </section>

      <InvoiceSummaryCard localSummary={localSummary} validatedPreview={validationResult?.preview} />

      {(validationResult?.issues?.length || generalError) ? (
        <ValidationAlertList
          issues={[
            ...(generalError ? [{ message: generalError, code: 'GENERAL_ERROR' }] : []),
            ...(validationResult?.issues ?? []),
          ]}
        />
      ) : null}

      <section className="rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Acciones</h2>
        <div className="flex flex-wrap items-center justify-end gap-3">
          <Button variant="secondary" onClick={onSaveDraft} disabled={isBusy}>
            {draftMutation.isPending ? 'Guardando borrador...' : 'Guardar borrador'}
          </Button>
          <Button variant="secondary" onClick={onValidate} disabled={isBusy}>
            {validateMutation.isPending ? 'Validando...' : 'Validar'}
          </Button>
          <Button onClick={onStamp} disabled={isBusy}>
            {stampMutation.isPending ? 'Timbrando...' : 'Timbrar'}
          </Button>
        </div>
      </section>
    </div>
  );
}
