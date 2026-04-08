import { Routes, Route, Navigate } from 'react-router';
import AppLayout from './components/layout/AppLayout';
import { DashboardPage } from './pages/DashboardPage';
import { ClientsPage } from './pages/clients/ClientsPage';
import { ClientDetailPage } from './pages/clients/ClientDetailPage';
import { NewClientPage } from './pages/clients/NewClientPage';
import { SuppliersPage } from './pages/suppliers/SuppliersPage';
import { SupplierDetailPage } from './pages/suppliers/SupplierDetailPage';
import { NewSupplierPage } from './pages/suppliers/NewSupplierPage';
import { ProductsPage } from './pages/products/ProductsPage';
import { InvoicesPage } from './pages/invoicing/InvoicesPage';
import { NewInvoicePage } from './pages/invoicing/NewInvoicePage';
import { InvoiceDetailPage } from './pages/invoicing/InvoiceDetailPage';
import { ExpensesPage } from './pages/expenses/ExpensesPage';
import { ExpenseDetailPage } from './pages/expenses/ExpenseDetailPage';
import { NewExpensePage } from './pages/expenses/NewExpensePage';
import { PaymentsPage } from './pages/payments/PaymentsPage';
import { MonthlyDeclarationsPage } from './pages/declarations/MonthlyDeclarationsPage';
import { AnnualDeclarationsPage } from './pages/declarations/AnnualDeclarationsPage';
import { IncomeExpenseReportPage } from './pages/reports/IncomeExpenseReportPage';
import { TaxReportPage } from './pages/reports/TaxReportPage';
import { ClientReportPage } from './pages/reports/ClientReportPage';
import { SupplierReportPage } from './pages/reports/SupplierReportPage';
import { CompanySettingsPage } from './pages/settings/CompanySettingsPage';
import { CatalogSettingsPage } from './pages/settings/CatalogSettingsPage';
import { LoginPage } from './pages/LoginPage';

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<AppLayout />}>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/clientes" element={<ClientsPage />} />
        <Route path="/clientes/nuevo" element={<NewClientPage />} />
        <Route path="/clientes/:id" element={<ClientDetailPage />} />
        <Route path="/proveedores" element={<SuppliersPage />} />
        <Route path="/proveedores/nuevo" element={<NewSupplierPage />} />
        <Route path="/proveedores/:id" element={<SupplierDetailPage />} />
        <Route path="/productos" element={<ProductsPage />} />
        <Route path="/facturacion" element={<InvoicesPage />} />
        <Route path="/facturacion/nueva" element={<NewInvoicePage />} />
        <Route path="/facturacion/:id" element={<InvoiceDetailPage />} />
        <Route path="/gastos" element={<ExpensesPage />} />
        <Route path="/gastos/nuevo" element={<NewExpensePage />} />
        <Route path="/gastos/:id" element={<ExpenseDetailPage />} />
        <Route path="/pagos" element={<PaymentsPage />} />
        <Route path="/declaraciones/mensuales" element={<MonthlyDeclarationsPage />} />
        <Route path="/declaraciones/anuales" element={<AnnualDeclarationsPage />} />
        <Route path="/reportes/ingresos-egresos" element={<IncomeExpenseReportPage />} />
        <Route path="/reportes/impuestos" element={<TaxReportPage />} />
        <Route path="/reportes/clientes" element={<ClientReportPage />} />
        <Route path="/reportes/proveedores" element={<SupplierReportPage />} />
        <Route path="/configuracion/empresa" element={<CompanySettingsPage />} />
        <Route path="/configuracion/catalogos" element={<CatalogSettingsPage />} />
      </Route>
    </Routes>
  );
}
