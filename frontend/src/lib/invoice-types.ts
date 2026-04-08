import type { Client, Product } from './types';

export interface CatalogOption {
  id?: string;
  code: string;
  description?: string;
  active?: boolean;
}

export interface InvoiceSummary {
  id: string;
  companyId: string;
  clientId?: string;
  invoiceType: string;
  status: string;
  series?: string;
  folio?: string;
  issuedAt?: string;
  currencyCode: string;
  subtotal: number;
  transferredTaxTotal: number;
  withheldTaxTotal: number;
  total: number;
  pacUuid?: string;
  receiverRfc: string;
  receiverName: string;
}

export interface InvoiceLine {
  id?: string | null;
  invoiceId?: string | null;
  lineNumber: number;
  productId?: string | null;
  satProductCode: string;
  description: string;
  satUnitCode: string;
  unitName?: string | null;
  quantity: number;
  unitPrice: number;
  discount: number;
  subtotal: number;
  objetoImpCode: string;
  transferredTaxTotal: number;
  withheldTaxTotal: number;
  total: number;
}

export interface InvoiceTaxLine {
  id?: string | null;
  sourceType: string;
  sourceId?: string | null;
  sourceLineId?: string | null;
  taxCode: string;
  factorType: string;
  rate: number;
  baseAmount: number;
  taxAmount: number;
  isTransfer: boolean;
  isWithholding: boolean;
  periodKey?: string | null;
}

export interface InvoiceArtifact {
  type: 'xml' | 'pdf' | string;
  fileName: string;
  contentType: string;
  downloadUrl: string;
}

export interface InvoiceDetail extends InvoiceSummary {
  cfdiVersion: string;
  certifiedAt?: string | null;
  cancelledAt?: string | null;
  exchangeRate: number;
  paymentMethodCode: string;
  paymentFormCode?: string | null;
  usoCfdiCode: string;
  exportCode: string;
  issuerRfc: string;
  issuerName: string;
  issuerRegimeCode: string;
  receiverRegimeCode: string;
  receiverPostalCode: string;
  discount: number;
  pacCertNumber?: string | null;
  satCertNumber?: string | null;
  pacStatus?: string | null;
  cancelReasonCode?: string | null;
  cancelReplacementUuid?: string | null;
  lines: InvoiceLine[];
  taxLines: InvoiceTaxLine[];
  artifacts: InvoiceArtifact[];
}

export interface InvoicePreview {
  series?: string;
  folio?: string;
  currencyCode: string;
  paymentMethodCode: string;
  paymentFormCode?: string | null;
  usoCfdiCode: string;
  issuerRfc: string;
  issuerName: string;
  issuerRegimeCode: string;
  receiverRfc: string;
  receiverName: string;
  receiverRegimeCode: string;
  receiverPostalCode: string;
  subtotal: number;
  discount: number;
  transferredTaxTotal: number;
  withheldTaxTotal: number;
  total: number;
  lines: InvoiceLine[];
  taxLines: InvoiceTaxLine[];
}

export interface InvoiceValidationIssue {
  fieldPath?: string | null;
  message: string;
  code: string;
}

export interface InvoiceValidationResult {
  issues: InvoiceValidationIssue[];
  valid: boolean;
  preview?: InvoicePreview | null;
}

export interface InvoiceDraftLineInput {
  productId: string;
  description?: string;
  quantity: number;
  unitPrice?: number;
  discount?: number;
}

export interface InvoiceCreateDraftRequest {
  clientId: string;
  invoiceType: string;
  series?: string;
  folio?: string;
  paymentMethodCode?: string;
  paymentFormCode?: string;
  usoCfdiCode?: string;
  currencyCode?: string;
  lines: InvoiceDraftLineInput[];
}

export interface InvoiceDraftLineForm extends InvoiceDraftLineInput {
  productLabel?: string;
  satProductCode?: string;
  satUnitCode?: string;
  objetoImpCode?: string;
  currencyCode?: string;
}

export interface InvoiceFormValues {
  clientId: string;
  invoiceType: string;
  series: string;
  folio: string;
  paymentMethodCode: string;
  paymentFormCode: string;
  usoCfdiCode: string;
  currencyCode: string;
  lines: InvoiceDraftLineForm[];
}

export interface InvoiceLocalLineSummary {
  subtotal: number;
  netSubtotal: number;
}

export interface InvoiceLocalSummary {
  subtotal: number;
  discount: number;
  transferredTaxTotal: number;
  withheldTaxTotal: number;
  total: number;
}

export interface InvoiceClientOption extends Client {}

export interface InvoiceProductOption extends Product {}
