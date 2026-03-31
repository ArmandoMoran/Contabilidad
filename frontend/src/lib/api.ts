const API_BASE = '/api/v1';

class ApiError extends Error {
  constructor(public status: number, public code: string, message: string, public fieldErrors?: Array<{ field: string; message: string }>) {
    super(message);
    this.name = 'ApiError';
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new ApiError(response.status, body.code ?? 'UNKNOWN', body.detail ?? response.statusText, body.fieldErrors);
  }
  if (response.status === 204) return undefined as T;
  return response.json();
}

// Dev company UUID — matches V12 seed migration
const DEV_COMPANY_ID = '11111111-1111-7111-8111-111111111111';

function getHeaders(): HeadersInit {
  const headers: HeadersInit = { 'Content-Type': 'application/json' };
  const token = localStorage.getItem('accessToken');
  if (token) headers['Authorization'] = `Bearer ${token}`;
  // X-Company-Id required by all backend controllers
  const companyId = localStorage.getItem('companyId') ?? DEV_COMPANY_ID;
  headers['X-Company-Id'] = companyId;
  return headers;
}

export const api = {
  get: <T>(path: string) => fetch(`${API_BASE}${path}`, { headers: getHeaders() }).then(r => handleResponse<T>(r)),
  post: <T>(path: string, body?: unknown) => fetch(`${API_BASE}${path}`, { method: 'POST', headers: getHeaders(), body: body ? JSON.stringify(body) : undefined }).then(r => handleResponse<T>(r)),
  patch: <T>(path: string, body: unknown) => fetch(`${API_BASE}${path}`, { method: 'PATCH', headers: getHeaders(), body: JSON.stringify(body) }).then(r => handleResponse<T>(r)),
  delete: (path: string) => fetch(`${API_BASE}${path}`, { method: 'DELETE', headers: getHeaders() }).then(r => handleResponse<void>(r)),
};

export { ApiError };
