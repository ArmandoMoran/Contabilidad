const statusStyles: Record<string, string> = {
  DRAFT: 'bg-gray-100 text-gray-700',
  VALIDATED: 'bg-blue-100 text-blue-700',
  STAMPED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700',
  PENDING: 'bg-yellow-100 text-yellow-700',
  REGISTERED: 'bg-blue-100 text-blue-700',
  APPLIED: 'bg-green-100 text-green-700',
};

const statusLabels: Record<string, string> = {
  DRAFT: 'Borrador',
  VALIDATED: 'Validada',
  STAMPED: 'Timbrada',
  CANCELLED: 'Cancelada',
  PENDING: 'Pendiente',
  REGISTERED: 'Registrado',
  APPLIED: 'Aplicado',
};

interface StatusBadgeProps {
  status: string;
}

export default function StatusBadge({ status }: StatusBadgeProps) {
  const style = statusStyles[status] ?? 'bg-gray-100 text-gray-700';
  const label = statusLabels[status] ?? status;

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${style}`}>
      {label}
    </span>
  );
}
