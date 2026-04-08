import '@testing-library/jest-dom/vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { describe, expect, it, vi } from 'vitest';
import { App } from './App';

vi.mock('./pages/invoicing/InvoicesPage', () => ({
  InvoicesPage: () => <div>Lista de facturas</div>,
}));

vi.mock('./pages/invoicing/NewInvoicePage', () => ({
  NewInvoicePage: () => <div>Formulario de factura</div>,
}));

vi.mock('./pages/invoicing/InvoiceDetailPage', () => ({
  InvoiceDetailPage: () => <div>Detalle de factura</div>,
}));

describe('invoice routes', () => {
  it('renders the invoice list route', () => {
    render(
      <MemoryRouter initialEntries={['/facturacion']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByText('Lista de facturas')).toBeInTheDocument();
  });

  it('renders the new invoice route', () => {
    render(
      <MemoryRouter initialEntries={['/facturacion/nueva']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByText('Formulario de factura')).toBeInTheDocument();
  });

  it('renders the invoice detail route', () => {
    render(
      <MemoryRouter initialEntries={['/facturacion/invoice-123']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByText('Detalle de factura')).toBeInTheDocument();
  });

  it('keeps the sidebar new invoice link on the canonical route', () => {
    render(
      <MemoryRouter initialEntries={['/facturacion']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByRole('link', { name: 'Nueva factura' })).toHaveAttribute('href', '/facturacion/nueva');
  });
});
