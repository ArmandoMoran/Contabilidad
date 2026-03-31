import { TrendingUp, TrendingDown, Minus } from 'lucide-react';

interface KpiItem {
  label: string;
  value: string | number;
  trend?: 'up' | 'down' | 'neutral';
}

interface KpiStripProps {
  items: KpiItem[];
}

const trendConfig = {
  up: { icon: TrendingUp, color: 'text-green-600' },
  down: { icon: TrendingDown, color: 'text-red-600' },
  neutral: { icon: Minus, color: 'text-gray-400' },
} as const;

export default function KpiStrip({ items }: KpiStripProps) {
  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      {items.map((item) => {
        const trend = item.trend ? trendConfig[item.trend] : null;
        return (
          <div
            key={item.label}
            className="bg-white rounded-lg border border-gray-200 p-4"
          >
            <p className="text-xs font-medium text-gray-500 uppercase tracking-wide">
              {item.label}
            </p>
            <div className="mt-1 flex items-center gap-2">
              <span className="text-2xl font-bold text-gray-900">{item.value}</span>
              {trend && <trend.icon className={`h-4 w-4 ${trend.color}`} />}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export type { KpiItem, KpiStripProps };
