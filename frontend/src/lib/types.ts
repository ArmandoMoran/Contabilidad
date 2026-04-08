export interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface Client {
  id: string;
  companyId: string;
  rfc: string;
  legalName: string;
  tradeName?: string;
  email?: string;
  phone?: string;
  website?: string;
  fiscalRegimeCode: string;
  defaultUsoCfdiCode?: string;
  defaultFormaPagoCode?: string;
  defaultMetodoPagoCode?: string;
  defaultPostalCode?: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Supplier {
  id: string;
  companyId: string;
  rfc: string;
  legalName: string;
  tradeName?: string;
  email?: string;
  phone?: string;
  website?: string;
  fiscalRegimeCode: string;
  defaultFormaPagoCode?: string;
  nationality: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Product {
  id: string;
  companyId: string;
  internalCode?: string;
  internalName: string;
  description?: string;
  satProductCode: string;
  satUnitCode: string;
  unitPrice: number;
  currencyCode: string;
  objetoImpCode: string;
  active: boolean;
}

export interface Company {
  id: string;
  rfc: string;
  legalName: string;
  taxpayerType: string;
  fiscalRegimeCode: string;
  taxZoneProfile: string;
  postalCode?: string;
  logoUrl?: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Invoice {
  id: string;
  companyId: string;
  clientId: string;
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

export interface Expense {
  id: string;
  companyId: string;
  supplierId?: string;
  expenseType: string;
  status: string;
  issuerRfc?: string;
  issuerName?: string;
  subtotal: number;
  total: number;
  category?: string;
}

export interface Payment {
  id: string;
  companyId: string;
  paymentDirection: string;
  status: string;
  paymentFormCode: string;
  currencyCode: string;
  amount: number;
  paidAt: string;
}

export interface Contact {
  id: string;
  companyId: string;
  partyType: string;
  partyId: string;
  fullName: string;
  email?: string;
  phone?: string;
  position?: string;
  isPrimary: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Address {
  id: string;
  companyId: string;
  partyType: string;
  partyId: string;
  addressType: string;
  street1: string;
  street2?: string;
  exteriorNumber?: string;
  interiorNumber?: string;
  neighborhood?: string;
  city?: string;
  municipalityCode?: string;
  stateCode?: string;
  postalCode: string;
  countryCode: string;
  isPrimary: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserInfo {
  id: string;
  companyId: string;
  email: string;
  fullName: string;
  role: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}
