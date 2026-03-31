import { useState } from 'react';
import KpiStrip from '@/components/ui/KpiStrip';
import PeriodFilter from '@/components/ui/PeriodFilter';
import DataGrid from '@/components/ui/DataGrid';

interface MonthRow {
  id: string;
  month: string;
  income: number;
  expense: number;
  result: number;
}

const MONTHS = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

export function IncomeExpenseReportPage() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const sampleData: MonthRow[] = MONTHS.slice(0, month).map((m, i) => ({
    id: String(i),
    month: m,
    income: 0,
    expense: 0,
    result: 0,
  }));

  const totalIncome = sampleData.reduce((s, r) => s + r.income, 0);
  const totalExpense = sampleData.reduce((s, r) => s + r.expense, 0);
  const netResult = totalIncome - totalExpense;

  const kpis = [
    { label: 'Total ingresos', value: `$${totalIncome.toFixed(2)}`, trend: 'up' as const },
    { label: 'Total egresos', value: `$${totalExpense.toFixed(2)}`, trend: 'down' as const },
    {
      label: netResult >= 0 ? 'Utilidad' : 'Pérdida',
      value: `$${Math.abs(netResult).toFixed(2)}`,
      trend: netResult >= 0 ? ('up' as const) : ('down' as const),
    },
  ];

  const columns = [
    { key: 'month', header: 'Mes' },
    { key: 'income', header: 'Ingresos', render: (row: MonthRow) => `$${row.income.toFixed(2)}` },
    { key: 'expense', header: 'Egresos', render: (row: MonthRow) => `$${row.expense.toFixed(2)}` },
    {
      key: 'result',
      header: 'Resultado',
      render: (row: MonthRow) => (
        <span className={row.result >= 0 ? 'text-green-700' : 'text-red-700'}>
          ${Math.abs(row.result).toFixed(2)}
        </span>
      ),
    },
  ];

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Reporte de ingresos y egresos</h1>

      <div className="mb-6">
        <PeriodFilter year={year} month={month} onYearChange={setYear} onMonthChange={setMonth} />
      </div>

      <KpiStrip items={kpis} />

      {/* Chart placeholder */}
      <div className="mb-6 flex h-48 items-center justify-center rounded-lg border border-dashed border-gray-300 bg-gray-50">
        <span className="text-sm text-gray-400">Área de gráfica — próximamente</span>
      </div>

      <DataGrid columns={columns} data={sampleData} emptyMessage="Sin datos para el período seleccionado" />
    </div>
  );
}
