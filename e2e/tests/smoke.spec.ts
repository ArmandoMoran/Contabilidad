import { test, expect } from '@playwright/test';

test.describe('Clients', () => {
  test('should display clients page', async ({ page }) => {
    await page.goto('/clientes');
    await expect(page.getByRole('heading', { name: 'Clientes' })).toBeVisible();
  });

  test('should navigate to new client form', async ({ page }) => {
    await page.goto('/clientes');
    await page.getByRole('link', { name: 'Nuevo cliente' }).click();
    await expect(page).toHaveURL('/clientes/nuevo');
    await expect(page.getByLabel('RFC')).toBeVisible();
  });

  test('should create a new client', async ({ page }) => {
    await page.goto('/clientes/nuevo');
    await page.getByLabel('RFC').fill('XAXX010101000');
    await page.getByLabel('Razón social').fill('Cliente de Prueba S.A. de C.V.');
    await page.getByLabel('Email').fill('test@ejemplo.mx');
    await page.getByLabel('Régimen fiscal').selectOption('601');
    await page.getByRole('button', { name: 'Guardar' }).click();
  });
});

test.describe('Products', () => {
  test('should display products page', async ({ page }) => {
    await page.goto('/productos');
    await expect(page.getByRole('heading', { name: 'Productos y Servicios' })).toBeVisible();
  });
});

test.describe('Invoicing', () => {
  test('should navigate to new invoice', async ({ page }) => {
    await page.goto('/facturacion/nueva');
    await expect(page.getByText('Emisor')).toBeVisible();
    await expect(page.getByText('Tu cliente')).toBeVisible();
    await expect(page.getByText('Productos o servicios')).toBeVisible();
  });
});
