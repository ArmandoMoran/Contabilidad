import type { ReactNode } from 'react';

interface PageToolbarProps {
  title: string;
  actions?: ReactNode;
  children?: ReactNode;
}

export default function PageToolbar({ title, actions, children }: PageToolbarProps) {
  return (
    <div className="mb-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">{title}</h1>
        {actions && <div className="flex items-center gap-2">{actions}</div>}
      </div>
      {children && <div className="flex items-center gap-3">{children}</div>}
    </div>
  );
}
