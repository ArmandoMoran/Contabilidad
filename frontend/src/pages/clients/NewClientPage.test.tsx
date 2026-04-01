import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { NewClientPage } from './NewClientPage';

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
        <NewClientPage />
      </QueryClientProvider>
    </MemoryRouter>,
  );
}

describe('NewClientPage', () => {
  beforeEach(() => {
    navigateMock.mockReset();
    apiPostMock.mockReset();
  });

  it('creates the client and then persists its fiscal address', async () => {
    apiPostMock
      .mockResolvedValueOnce({ id: 'client-123' })
      .mockResolvedValueOnce({ id: 'address-123' });

    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText(/RFC/i), 'TES010101AB1');
    await user.type(screen.getByLabelText(/Razon social/i), 'Cliente de prueba S.A. de C.V.');
    await user.selectOptions(screen.getByLabelText(/Regimen fiscal/i), '601');
    await user.type(screen.getByLabelText(/^Calle$/i), 'Av Reforma 100');
    await user.type(screen.getByLabelText(/C\.P\./i), '06600');
    await user.type(screen.getByLabelText(/Ciudad/i), 'Ciudad de Mexico');
    await user.type(screen.getByLabelText(/Estado/i), 'CMX');

    await user.click(screen.getByRole('button', { name: /Guardar cliente/i }));

    await waitFor(() => expect(apiPostMock).toHaveBeenCalledTimes(2));
    expect(apiPostMock).toHaveBeenNthCalledWith(1, '/clients', expect.objectContaining({
      rfc: 'TES010101AB1',
      legalName: 'Cliente de prueba S.A. de C.V.',
      fiscalRegimeCode: '601',
      defaultPostalCode: '06600',
    }));
    expect(apiPostMock).toHaveBeenNthCalledWith(2, '/clients/client-123/addresses', expect.objectContaining({
      addressType: 'FISCAL',
      street1: 'Av Reforma 100',
      city: 'Ciudad de Mexico',
      stateCode: 'CMX',
      postalCode: '06600',
      countryCode: 'MEX',
      isPrimary: true,
    }));
    expect(navigateMock).toHaveBeenCalledWith('/clientes/client-123');
  });
});
