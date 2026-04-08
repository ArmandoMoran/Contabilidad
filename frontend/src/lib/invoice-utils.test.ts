import { describe, expect, it } from 'vitest';
import {
  calculateLocalInvoiceSummary,
  createEmptyInvoiceLine,
  mapInvoiceFormToDraftRequest,
} from './invoice-utils';

describe('invoice utils', () => {
  it('calculates local invoice preview totals from line inputs', () => {
    const line = createEmptyInvoiceLine();
    line.productId = 'prod-1';
    line.description = 'Servicio mensual';
    line.quantity = 2;
    line.unitPrice = 1500;
    line.discount = 200;

    const summary = calculateLocalInvoiceSummary([line]);

    expect(summary).toEqual({
      subtotal: 3000,
      discount: 200,
      transferredTaxTotal: 0,
      withheldTaxTotal: 0,
      total: 2800,
    });
  });

  it('maps form values to the backend draft contract', () => {
    const payload = mapInvoiceFormToDraftRequest({
      clientId: 'client-1',
      invoiceType: 'I',
      series: 'A',
      folio: '',
      paymentMethodCode: 'PUE',
      paymentFormCode: '03',
      usoCfdiCode: 'G03',
      currencyCode: 'MXN',
      lines: [{
        productId: 'prod-1',
        description: 'Servicio mensual',
        quantity: 1,
        unitPrice: 850,
        discount: 0,
        satProductCode: '81112100',
        satUnitCode: 'E48',
        objetoImpCode: '02',
        currencyCode: 'MXN',
      }],
    });

    expect(payload).toEqual({
      clientId: 'client-1',
      invoiceType: 'I',
      series: 'A',
      folio: undefined,
      paymentMethodCode: 'PUE',
      paymentFormCode: '03',
      usoCfdiCode: 'G03',
      currencyCode: 'MXN',
      lines: [{
        productId: 'prod-1',
        description: 'Servicio mensual',
        quantity: 1,
        unitPrice: 850,
        discount: 0,
      }],
    });
  });
});
