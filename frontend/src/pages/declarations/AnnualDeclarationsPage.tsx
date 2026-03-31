import { useState } from 'react';

export function AnnualDeclarationsPage() {
  const currentYear = new Date().getFullYear();
  const [year, setYear] = useState(currentYear - 1);
  const years = Array.from({ length: 5 }, (_, i) => currentYear - i);

  const obligations = [
    { title: 'Declaración anual de ISR', status: 'Pendiente' },
    { title: 'Informativa de operaciones con terceros', status: 'Pendiente' },
    { title: 'Dictamen fiscal (si aplica)', status: 'No aplica' },
  ];

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Declaraciones anuales</h1>

      <div className="mb-6">
        <select
          value={year}
          onChange={(e) => setYear(Number(e.target.value))}
          className="rounded-md border border-gray-200 bg-white px-3 py-1.5 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
        >
          {years.map((y) => (
            <option key={y} value={y}>{y}</option>
          ))}
        </select>
      </div>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
        {obligations.map((ob) => (
          <div key={ob.title} className="rounded-lg border border-gray-200 bg-white p-6">
            <h3 className="mb-2 text-lg font-semibold text-gray-900">{ob.title}</h3>
            <p className="mb-4 text-sm text-gray-500">Ejercicio fiscal {year}</p>
            <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${
              ob.status === 'Pendiente'
                ? 'bg-yellow-100 text-yellow-700'
                : 'bg-gray-100 text-gray-600'
            }`}>
              {ob.status}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}
