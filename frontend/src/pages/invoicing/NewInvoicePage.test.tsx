import '@testing-library/jest-dom/vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '@/lib/api';
import { NewInvoicePage } from './NewInvoicePage';

const navigateMock = vi.fn();
const invoiceApiMocks = vi.hoisted(() => ({
  getCurrentUser: vi.fn(),
  getCompany: vi.fn(),
  listUsoCfdi: vi.fn(),
  listFormaPago: vi.fn(),
  listMetodoPago: vi.fn(),
  listMonedas: vi.fn(),
  searchClients: vi.fn(),
  searchProducts: vi.fn(),
  validateDraft: vi.fn(),
  createDraft: vi.fn(),
  createStamped: vi.fn(),
}));

vi.mock('react-router', async () => {
  const actual = await vi.importActual<typeof import('react-router')>('react-router');
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

vi.mock('@/lib/invoice-api', () => ({
  invoiceApi: invoiceApiMocks,
}));

function renderPage() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return render(
    <MemoryRouter>
      <QueryClientProvider client={queryClient}>
        <NewInvoicePage />
      </QueryClientProvider>
    </MemoryRouter>,
  );
}

function setupCommonMocks() {
  invoiceApiMocks.getCurrentUser.mockResolvedValue({
    id: 'user-1',
    companyId: 'company-1',
    email: 'admin@demo.com',
    fullName: 'Admin Demo',
    role: 'admin',
  });
  invoiceApiMocks.getCompany.mockResolvedValue({
    id: 'company-1',
    rfc: 'EMP010101AAA',
    legalName: 'Empresa Demo SA de CV',
    taxpayerType: 'MORAL',
    fiscalRegimeCode: '601',
    taxZoneProfile: 'STANDARD',
    postalCode: '06600',
    active: true,
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-01T00:00:00Z',
  });
  invoiceApiMocks.listUsoCfdi.mockResolvedValue([{ code: 'G03', description: 'Gastos en general' }, { code: 'S01', description: 'Sin efectos fiscales' }]);
  invoiceApiMocks.listFormaPago.mockResolvedValue([{ code: '03', description: 'Transferencia' }, { code: '28', description: 'Tarjeta de debito' }]);
  invoiceApiMocks.listMetodoPago.mockResolvedValue([{ code: 'PUE', description: 'Pago en una sola exhibicion' }, { code: 'PPD', description: 'Pago en parcialidades' }]);
  invoiceApiMocks.listMonedas.mockResolvedValue([{ code: 'MXN', description: 'Peso mexicano' }]);
  invoiceApiMocks.searchClients.mockResolvedValue({
    items: [{
      id: 'client-1',
      companyId: 'company-1',
      rfc: 'AAA010101AA1',
      legalName: 'Distribuidora Norte SA de CV',
      tradeName: 'DisNorte',
      email: 'ventas@disnorte.mx',
      phone: '5551234001',
      website: '',
      fiscalRegimeCode: '601',
      defaultUsoCfdiCode: 'S01',
      defaultFormaPagoCode: '28',
      defaultMetodoPagoCode: 'PPD',
      defaultPostalCode: '06600',
      active: true,
      createdAt: '2026-01-01T00:00:00Z',
      updatedAt: '2026-01-01T00:00:00Z',
    }],
    page: 0,
    size: 8,
    totalItems: 1,
    totalPages: 1,
  });
  invoiceApiMocks.searchProducts.mockResolvedValue({
    items: [{
      id: 'product-1',
      companyId: 'company-1',
      internalCode: 'SERV-002',
      internalName: 'Desarrollo de Software',
      description: 'Desarrollo de software a medida por hora',
      satProductCode: '81112100',
      satUnitCode: 'E48',
      unitPrice: 850,
      currencyCode: 'MXN',
      objetoImpCode: '02',
      active: true,
    }],
    page: 0,
    size: 8,
    totalItems: 1,
    totalPages: 1,
  });
  invoiceApiMocks.validateDraft.mockResolvedValue({ valid: true, issues: [], preview: null });
  invoiceApiMocks.createDraft.mockResolvedValue({ id: 'invoice-1' });
  invoiceApiMocks.createStamped.mockResolvedValue({ id: 'invoice-2' });
}

describe('NewInvoicePage', () => {
  beforeEach(() => {
    navigateMock.mockReset();
    Object.values(invoiceApiMocks).forEach((mockFn) => mockFn.mockReset());
    setupCommonMocks();
  });

  it('fills CFDI defaults when a client is selected', async () => {
    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText(/Buscar cliente/i), 'dis');
    await user.click(await screen.findByRole('button', { name: /Distribuidora Norte SA de CV/i }));

    await waitFor(() => {
      expect(screen.getByLabelText(/Método de pago/i)).toHaveValue('PPD');
      expect(screen.getByLabelText(/Forma de pago/i)).toHaveValue('28');
      expect(screen.getByLabelText(/Uso CFDI/i)).toHaveValue('S01');
    });
  });

  it('fills line values when a product is selected', async () => {
    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText(/Buscar producto/i), 'software');
    await user.click(await screen.findByRole('button', { name: /Desarrollo de Software/i }));

    await waitFor(() => {
      expect(screen.getByLabelText(/Descripción/i)).toHaveValue('Desarrollo de software a medida por hora');
      expect(screen.getByLabelText(/Precio unitario/i)).toHaveValue(850);
      expect(screen.getAllByText(/81112100 · E48/i).length).toBeGreaterThan(0);
    });
  });

  it('shows backend mutation errors when saving a draft fails', async () => {
    invoiceApiMocks.createDraft.mockRejectedValueOnce(
      new ApiError(422, 'BUSINESS_VALIDATION', 'El backend rechazo la factura.', [
        { field: 'lines[0].unitPrice', message: 'Precio invalido' },
      ]),
    );

    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText(/Buscar cliente/i), 'dis');
    await user.click(await screen.findByRole('button', { name: /Distribuidora Norte SA de CV/i }));
    await user.type(screen.getByLabelText(/Buscar producto/i), 'software');
    await user.click(await screen.findByRole('button', { name: /Desarrollo de Software/i }));

    await user.click(screen.getByRole('button', { name: /Guardar borrador/i }));

    await waitFor(() => {
      expect(screen.getByText(/El backend rechazo la factura\./i)).toBeInTheDocument();
      expect(screen.getByText(/Precio invalido/i)).toBeInTheDocument();
    });
  });
});
