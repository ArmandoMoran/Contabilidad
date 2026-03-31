import { useState } from 'react';
import PeriodFilter from '@/components/ui/PeriodFilter';

interface TaxRow {
  concept: string;
  base: number;
  rate: string;
  amount: number;
}

export function TaxReportPage() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const ivaTraslado: TaxRow[] = [
    { concept: 'IVA 16% Trasladado', base: 0, rate: '16%', amount: 0 },
  ];

  const ivaRetenido: TaxRow[] = [
    { concept: 'IVA Retenido', base: 0, rate: '10.6667%', amount: 0 },
  ];

  const isrRetenido: TaxRow[] = [
    { concept: 'ISR Retenido', base: 0, rate: '10%', amount: 0 },
  ];

  function TaxTable({ title, rows }: { title: string; rows: TaxRow[] }) {
    const total = rows.reduce((s, r) => s + r.amount, 0);
    return (
      <div className="rounded-lg border border-gray-200 bg-white">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-lg font-semibold text-gray-900">{title}</h2>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-200 bg-gray-50">
              <th className="px-6 py-2 text-left text-xs font-semibold uppercase text-gray-500">Concepto</th>
              <th className="px-6 py-2 text-right text-xs font-semibold uppercase text-gray-500">Base</th>
              <th className="px-6 py-2 text-right text-xs font-semibold uppercase text-gray-500">Tasa</th>
              <th className="px-6 py-2 text-right text-xs font-semibold uppercase text-gray-500">Importe</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {rows.map((r) => (
              <tr key={r.concept}>
                <td className="px-6 py-3 text-gray-700">{r.concept}</td>
                <td className="px-6 py-3 text-right text-gray-700">${r.base.toFixed(2)}</td>
                <td className="px-6 py-3 text-right text-gray-700">{r.rate}</td>
                <td className="px-6 py-3 text-right font-medium text-gray-900">${r.amount.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr className="border-t border-gray-200 bg-gray-50">
              <td colSpan={3} className="px-6 py-3 text-right text-sm font-semibold text-gray-900">Total</td>
              <td className="px-6 py-3 text-right text-sm font-bold text-gray-900">${total.toFixed(2)}</td>
            </tr>
          </tfoot>
        </table>
      </div>
    );
  }

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Reporte de impuestos</h1>

      <div className="mb-6">
        <PeriodFilter year={year} month={month} onYearChange={setYear} onMonthChange={setMonth} />
      </div>

      <div className="space-y-6">
        <TaxTable title="IVA Trasladado" rows={ivaTraslado} />
        <TaxTable title="IVA Retenido" rows={ivaRetenido} />
        <TaxTable title="ISR Retenido" rows={isrRetenido} />
      </div>
    </div>
  );
}
