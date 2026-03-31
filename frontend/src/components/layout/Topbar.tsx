import { Search, User } from 'lucide-react';

export default function Topbar() {
  return (
    <header className="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-6">
      <div className="relative w-80">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
        <input
          type="text"
          placeholder="Buscar..."
          className="w-full pl-10 pr-4 py-2 text-sm rounded-md border border-gray-200 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
        />
      </div>

      <div className="flex items-center gap-4">
        <span className="text-sm text-gray-600">Mi Empresa S.A. de C.V.</span>
        <button
          type="button"
          className="flex items-center justify-center h-8 w-8 rounded-full bg-primary-100 text-primary-700 hover:bg-primary-200 transition-colors"
        >
          <User className="h-4 w-4" />
        </button>
      </div>
    </header>
  );
}
