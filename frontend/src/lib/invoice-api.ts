import { api } from './api';
import type { Company, PageResponse, Product, UserInfo, Client } from './types';
import type {
  CatalogOption,
  InvoiceCreateDraftRequest,
  InvoiceDetail,
  InvoiceSummary,
  InvoiceValidationResult,
} from './invoice-types';

export const invoiceApi = {
  listInvoices: (params: { page: number; size?: number; search?: string; status?: string }) => {
    const searchParams = new URLSearchParams({
      page: String(params.page),
      size: String(params.size ?? 10),
    });

    if (params.search?.trim()) {
      searchParams.set('search', params.search.trim());
    }
    if (params.status?.trim() && params.status !== 'ALL') {
      searchParams.set('status', params.status.trim());
    }

    return api.get<PageResponse<InvoiceSummary>>(`/invoices?${searchParams.toString()}`);
  },
  getInvoiceDetail: (invoiceId: string) => api.get<InvoiceDetail>(`/invoices/${invoiceId}`),
  validateDraft: (payload: InvoiceCreateDraftRequest) => api.post<InvoiceValidationResult>('/invoices/validate', payload),
  createDraft: (payload: InvoiceCreateDraftRequest) => api.post<InvoiceSummary>('/invoices/drafts', payload),
  createStamped: (payload: InvoiceCreateDraftRequest) => api.post<InvoiceSummary>('/invoices/stamped', payload),
  validatePersistedDraft: (invoiceId: string) => api.post<InvoiceValidationResult>(`/invoices/${invoiceId}/validate`, {}),
  stampInvoice: (invoiceId: string) => api.post<InvoiceSummary>(`/invoices/${invoiceId}/stamp`, {}),
  cancelInvoice: (invoiceId: string, reasonCode: string, replacementUuid?: string) =>
    api.post<InvoiceSummary>(`/invoices/${invoiceId}/cancel`, {
      reasonCode,
      replacementUuid: replacementUuid?.trim() || undefined,
    }),
  downloadXml: (invoiceId: string) => api.getBlob(`/invoices/${invoiceId}/xml`),
  downloadPdf: (invoiceId: string) => api.getBlob(`/invoices/${invoiceId}/pdf`),
  searchClients: (search: string) =>
    api.get<PageResponse<Client>>(`/clients?page=0&size=8${search.trim() ? `&search=${encodeURIComponent(search.trim())}` : ''}`),
  searchProducts: (search: string) =>
    api.get<PageResponse<Product>>(`/products?page=0&size=8${search.trim() ? `&search=${encodeURIComponent(search.trim())}` : ''}`),
  listUsoCfdi: () => api.get<CatalogOption[]>('/catalogs/uso-cfdi'),
  listFormaPago: () => api.get<CatalogOption[]>('/catalogs/forma-pago'),
  listMetodoPago: () => api.get<CatalogOption[]>('/catalogs/metodo-pago'),
  listMonedas: () => api.get<CatalogOption[]>('/catalogs/moneda'),
  getCurrentUser: () => api.get<UserInfo>('/auth/me'),
  getCompany: (companyId: string) => api.get<Company>(`/companies/${companyId}`),
};
