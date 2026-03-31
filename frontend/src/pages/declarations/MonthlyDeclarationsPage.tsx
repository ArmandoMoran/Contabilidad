import { useState } from 'react';
import PeriodFilter from '@/components/ui/PeriodFilter';
import Button from '@/components/ui/Button';

interface ObligationCard {
  title: string;
  period: string;
  baseGravable: number;
  impuestoDeterminado: number;
  retenciones: number;
  aCargo: number;
}

export function MonthlyDeclarationsPage() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const monthName = new Date(year, month - 1).toLocaleString('es-MX', { month: 'long' });
  const periodLabel = `${monthName.charAt(0).toUpperCase() + monthName.slice(1)} ${year}`;

  const obligations: ObligationCard[] = [
    {
      title: 'ISR Provisional',
      period: periodLabel,
      baseGravable: 0,
      impuestoDeterminado: 0,
      retenciones: 0,
      aCargo: 0,
    },
    {
      title: 'IVA Mensual',
      period: periodLabel,
      baseGravable: 0,
      impuestoDeterminado: 0,
      retenciones: 0,
      aCargo: 0,
    },
    {
      title: 'DIOT',
      period: periodLabel,
      baseGravable: 0,
      impuestoDeterminado: 0,
      retenciones: 0,
      aCargo: 0,
    },
  ];

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Declaraciones mensuales</h1>

      <div className="mb-6">
        <PeriodFilter year={year} month={month} onYearChange={setYear} onMonthChange={setMonth} />
      </div>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
        {obligations.map((ob) => (
          <div key={ob.title} className="rounded-lg border border-gray-200 bg-white p-6">
            <h3 className="mb-1 text-lg font-semibold text-gray-900">{ob.title}</h3>
            <p className="mb-4 text-sm text-gray-500">{ob.period}</p>

            <dl className="space-y-2 text-sm">
              <div className="flex justify-between">
                <dt className="text-gray-600">Base gravable</dt>
                <dd className="font-medium text-gray-900">${ob.baseGravable.toFixed(2)}</dd>
              </div>
              <div className="flex justify-between">
                <dt className="text-gray-600">Impuesto determinado</dt>
                <dd className="font-medium text-gray-900">${ob.impuestoDeterminado.toFixed(2)}</dd>
              </div>
              <div className="flex justify-between">
                <dt className="text-gray-600">Retenciones</dt>
                <dd className="font-medium text-gray-900">${ob.retenciones.toFixed(2)}</dd>
              </div>
              <div className="flex justify-between border-t border-gray-200 pt-2">
                <dt className="font-semibold text-gray-900">
                  {ob.aCargo >= 0 ? 'A cargo' : 'A favor'}
                </dt>
                <dd className={`text-lg font-bold ${ob.aCargo >= 0 ? 'text-red-600' : 'text-green-600'}`}>
                  ${Math.abs(ob.aCargo).toFixed(2)}
                </dd>
              </div>
            </dl>
          </div>
        ))}
      </div>

      <div className="mt-8 flex justify-end">
        <Button>Generar papeles de trabajo</Button>
      </div>
    </div>
  );
}
