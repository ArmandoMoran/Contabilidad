import KpiStrip from '@/components/ui/KpiStrip';

export function DashboardPage() {
  const kpis = [
    { label: 'Ingresos del mes', value: '$142,580.00', trend: 'up' as const },
    { label: 'Gastos del mes', value: '$87,320.00', trend: 'down' as const },
    { label: 'Impuestos pendientes', value: '$12,450.00', trend: 'neutral' as const },
    { label: 'Facturas por cobrar', value: '8', trend: 'up' as const },
  ];

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Dashboard</h1>

      <KpiStrip items={kpis} />

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Actividad reciente</h2>
          <ul className="space-y-3 text-sm text-gray-600">
            <li className="flex items-center justify-between border-b border-gray-100 pb-3">
              <span>Factura A-001 emitida</span>
              <span className="text-xs text-gray-400">Hace 2 horas</span>
            </li>
            <li className="flex items-center justify-between border-b border-gray-100 pb-3">
              <span>Pago recibido - Cliente ABC</span>
              <span className="text-xs text-gray-400">Hace 5 horas</span>
            </li>
            <li className="flex items-center justify-between border-b border-gray-100 pb-3">
              <span>Gasto registrado - Proveedor XYZ</span>
              <span className="text-xs text-gray-400">Ayer</span>
            </li>
            <li className="flex items-center justify-between">
              <span>Declaración mensual generada</span>
              <span className="text-xs text-gray-400">Hace 2 días</span>
            </li>
          </ul>
        </div>

        <div className="rounded-lg border border-gray-200 bg-white p-6">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">Próximos vencimientos</h2>
          <ul className="space-y-3 text-sm text-gray-600">
            <li className="flex items-center justify-between border-b border-gray-100 pb-3">
              <span>Declaración IVA mensual</span>
              <span className="rounded bg-yellow-100 px-2 py-0.5 text-xs font-medium text-yellow-700">17 Abr</span>
            </li>
            <li className="flex items-center justify-between border-b border-gray-100 pb-3">
              <span>ISR Provisional</span>
              <span className="rounded bg-yellow-100 px-2 py-0.5 text-xs font-medium text-yellow-700">17 Abr</span>
            </li>
            <li className="flex items-center justify-between border-b border-gray-100 pb-3">
              <span>DIOT</span>
              <span className="rounded bg-red-100 px-2 py-0.5 text-xs font-medium text-red-700">17 Abr</span>
            </li>
            <li className="flex items-center justify-between">
              <span>Pago factura F-2024-015</span>
              <span className="rounded bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-700">25 Abr</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}
