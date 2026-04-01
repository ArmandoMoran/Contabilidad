import { test, expect, type Page } from '@playwright/test';

async function login(page: Page) {
  await page.goto('/login');
  await page.getByLabel(/Correo/i).fill('admin@demo.com');
  await page.getByLabel(/Contrasena|Contrase/i).fill('demo1234');
  await page.getByRole('button', { name: /Iniciar sesion|Iniciar sesi/i }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
}

test.describe('Smoke E2E', () => {
  test('allows a seeded user to log in and reach dashboard', async ({ page }) => {
    await login(page);
    await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();
  });

  test('creates a client from the UI and lands on its detail page', async ({ page }) => {
    await login(page);
    const uniqueRfc = `UIA010101${Date.now().toString().slice(-3)}`;

    await page.goto('/clientes/nuevo');
    await page.getByLabel('RFC *').fill(uniqueRfc);
    await page.getByLabel('Razon social *').fill('Cliente UI Automatizado S.A. de C.V.');
    await page.getByLabel('Regimen fiscal *').selectOption('601');
    await page.getByLabel('Codigo postal fiscal').fill('06600');
    await page.getByLabel('Calle').fill('Av Reforma');
    await page.getByLabel('Ciudad').fill('Ciudad de Mexico');
    await page.getByLabel('Estado').fill('CMX');
    await page.getByLabel('C.P.').fill('06600');
    await page.getByRole('button', { name: 'Guardar cliente' }).click();

    await expect(page).toHaveURL(/\/clientes\/[0-9a-f-]+$/);
  });

  test('shows products and invoicing flows after login', async ({ page }) => {
    await login(page);

    await page.goto('/productos');
    await expect(page.getByRole('heading', { name: 'Productos y Servicios' })).toBeVisible();

    await page.goto('/facturacion/nueva');
    await expect(page.getByRole('heading', { name: 'Emisor' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Tu cliente' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Productos o servicios' })).toBeVisible();
  });
});
