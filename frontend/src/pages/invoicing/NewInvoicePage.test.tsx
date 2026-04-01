import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { NewInvoicePage } from './NewInvoicePage';

const navigateMock = vi.fn();
const apiPostMock = vi.fn();

vi.mock('react-router', async () => {
  const actual = await vi.importActual<typeof import('react-router')>('react-router');
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

vi.mock('@/lib/api', () => ({
  api: {
    post: (...args: unknown[]) => apiPostMock(...args),
  },
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

describe('NewInvoicePage', () => {
  beforeEach(() => {
    navigateMock.mockReset();
    apiPostMock.mockReset();
  });

  it('creates a draft and stamps it when validation passes', async () => {
    apiPostMock
      .mockResolvedValueOnce({ id: 'invoice-123' })
      .mockResolvedValueOnce({ id: 'invoice-123' });

    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText(/Buscar cliente/i), 'client-123');
    await user.type(screen.getByLabelText(/Descripcion/i), 'Servicio mensual');
    await user.clear(screen.getByLabelText(/Precio unit\./i));
    await user.type(screen.getByLabelText(/Precio unit\./i), '1500');

    await user.click(screen.getByRole('button', { name: /Timbrar/i }));

    await waitFor(() => expect(apiPostMock).toHaveBeenCalledTimes(2));
    expect(apiPostMock).toHaveBeenNthCalledWith(1, '/invoices/drafts', expect.objectContaining({
      clientId: 'client-123',
      invoiceType: 'I',
      paymentMethodCode: 'PUE',
      paymentFormCode: '03',
      usoCfdiCode: 'G03',
    }));
    expect(apiPostMock).toHaveBeenNthCalledWith(2, '/invoices/invoice-123/stamp', {});
    expect(navigateMock).toHaveBeenCalledWith('/facturacion/invoice-123');
  });
});
