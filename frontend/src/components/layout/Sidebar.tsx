import { NavLink } from 'react-router';
import {
  LayoutDashboard,
  Users,
  Truck,
  Package,
  FileText,
  Receipt,
  CreditCard,
  Calculator,
  BarChart3,
  Building2,
  Database,
  type LucideIcon,
} from 'lucide-react';

interface NavItem {
  label: string;
  to: string;
  icon: LucideIcon;
}

interface NavGroup {
  title: string;
  items: NavItem[];
}

const navigation: NavGroup[] = [
  {
    title: 'PRINCIPAL',
    items: [
      { label: 'Dashboard', to: '/', icon: LayoutDashboard },
      { label: 'Clientes', to: '/clientes', icon: Users },
      { label: 'Proveedores', to: '/proveedores', icon: Truck },
      { label: 'Productos', to: '/productos', icon: Package },
    ],
  },
  {
    title: 'FACTURACIÓN',
    items: [
      { label: 'Nueva factura', to: '/facturacion/nueva', icon: FileText },
      { label: 'Gastos', to: '/gastos', icon: Receipt },
      { label: 'Pagos', to: '/pagos', icon: CreditCard },
    ],
  },
  {
    title: 'FISCAL',
    items: [
      { label: 'Declaraciones mensuales', to: '/declaraciones/mensuales', icon: Calculator },
      { label: 'Declaraciones anuales', to: '/declaraciones/anuales', icon: Calculator },
    ],
  },
  {
    title: 'REPORTES',
    items: [
      { label: 'Ingresos/Egresos', to: '/reportes/ingresos-egresos', icon: BarChart3 },
      { label: 'Impuestos', to: '/reportes/impuestos', icon: BarChart3 },
      { label: 'Clientes', to: '/reportes/clientes', icon: BarChart3 },
      { label: 'Proveedores', to: '/reportes/proveedores', icon: BarChart3 },
    ],
  },
  {
    title: 'CONFIGURACIÓN',
    items: [
      { label: 'Empresa', to: '/configuracion/empresa', icon: Building2 },
      { label: 'Catálogos', to: '/configuracion/catalogos', icon: Database },
    ],
  },
];

export default function Sidebar() {
  return (
    <aside className="fixed bottom-0 left-0 top-0 flex w-60 flex-col overflow-y-auto border-r border-gray-200 bg-white">
      <div className="flex h-14 items-center border-b border-gray-200 px-5">
        <span className="text-lg font-bold text-primary-700">Contabilidad</span>
      </div>

      <nav className="flex-1 space-y-6 px-3 py-4">
        {navigation.map((group) => (
          <div key={group.title}>
            <p className="mb-2 px-2 text-xs font-semibold uppercase tracking-wider text-gray-400">
              {group.title}
            </p>
            <ul className="space-y-0.5">
              {group.items.map((item) => (
                <li key={item.to}>
                  <NavLink
                    to={item.to}
                    end={item.to === '/'}
                    className={({ isActive }) =>
                      `flex items-center gap-3 rounded-md px-2 py-2 text-sm transition-colors ${
                        isActive
                          ? 'bg-primary-50 font-medium text-primary-700'
                          : 'text-gray-600 hover:bg-gray-50'
                      }`
                    }
                  >
                    <item.icon className="h-4 w-4 shrink-0" />
                    {item.label}
                  </NavLink>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </nav>
    </aside>
  );
}
