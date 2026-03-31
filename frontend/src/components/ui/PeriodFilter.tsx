const MONTHS = [
  'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
  'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic',
] as const;

interface PeriodFilterProps {
  year: number;
  month: number;
  onYearChange: (year: number) => void;
  onMonthChange: (month: number) => void;
  yearRange?: [number, number];
}

export default function PeriodFilter({
  year,
  month,
  onYearChange,
  onMonthChange,
  yearRange = [2020, new Date().getFullYear()],
}: PeriodFilterProps) {
  const years = Array.from(
    { length: yearRange[1] - yearRange[0] + 1 },
    (_, i) => yearRange[0] + i,
  );

  return (
    <div className="flex items-center gap-4">
      <select
        value={year}
        onChange={(e) => onYearChange(Number(e.target.value))}
        className="rounded-md border border-gray-200 bg-white px-3 py-1.5 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
      >
        {years.map((y) => (
          <option key={y} value={y}>
            {y}
          </option>
        ))}
      </select>

      <div className="flex gap-1">
        {MONTHS.map((label, idx) => {
          const m = idx + 1;
          return (
            <button
              key={label}
              type="button"
              onClick={() => onMonthChange(m)}
              className={`px-2.5 py-1 text-xs font-medium rounded-md transition-colors ${
                month === m
                  ? 'bg-primary-600 text-white'
                  : 'bg-white border border-gray-200 text-gray-600 hover:bg-gray-50'
              }`}
            >
              {label}
            </button>
          );
        })}
      </div>
    </div>
  );
}
