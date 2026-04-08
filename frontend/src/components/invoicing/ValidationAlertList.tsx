import type { InvoiceValidationIssue } from '@/lib/invoice-types';

interface ValidationAlertListProps {
  issues: InvoiceValidationIssue[];
  title?: string;
}

export default function ValidationAlertList({ issues, title = 'Validaciones y errores' }: ValidationAlertListProps) {
  if (issues.length === 0) {
    return null;
  }

  return (
    <section className="rounded-lg border border-red-200 bg-red-50 p-6">
      <h2 className="mb-3 text-lg font-semibold text-red-800">{title}</h2>
      <ul className="space-y-2 text-sm text-red-700">
        {issues.map((issue, index) => (
          <li key={`${issue.code}-${issue.fieldPath ?? 'general'}-${index}`} className="rounded-md bg-white/60 px-3 py-2">
            {issue.fieldPath && <span className="font-medium">{issue.fieldPath}: </span>}
            {issue.message}
          </li>
        ))}
      </ul>
    </section>
  );
}
