const DEFAULT_API_BASE = '/api/v1';
const configuredApiBase = import.meta.env.VITE_API_BASE_URL?.trim();
const API_BASE = configuredApiBase
  ? configuredApiBase.replace(/\/+$/, '')
  : DEFAULT_API_BASE;

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

async function handleBlobResponse(response: Response): Promise<{ blob: Blob; contentType: string; fileName?: string }> {
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new ApiError(response.status, body.code ?? 'UNKNOWN', body.detail ?? response.statusText, body.fieldErrors);
  }

  const disposition = response.headers.get('content-disposition') ?? '';
  const fileName = disposition.match(/filename="?([^"]+)"?/)?.[1];

  return {
    blob: await response.blob(),
    contentType: response.headers.get('content-type') ?? 'application/octet-stream',
    fileName,
  };
}

function getHeaders(includeJsonContentType = true): HeadersInit {
  const headers: HeadersInit = includeJsonContentType ? { 'Content-Type': 'application/json' } : {};
  const token = localStorage.getItem('accessToken');
  if (token) headers.Authorization = `Bearer ${token}`;
  return headers;
}

export const api = {
  get: <T>(path: string) => fetch(`${API_BASE}${path}`, { headers: getHeaders() }).then(r => handleResponse<T>(r)),
  post: <T>(path: string, body?: unknown) => fetch(`${API_BASE}${path}`, { method: 'POST', headers: getHeaders(), body: body ? JSON.stringify(body) : undefined }).then(r => handleResponse<T>(r)),
  patch: <T>(path: string, body: unknown) => fetch(`${API_BASE}${path}`, { method: 'PATCH', headers: getHeaders(), body: JSON.stringify(body) }).then(r => handleResponse<T>(r)),
  delete: (path: string) => fetch(`${API_BASE}${path}`, { method: 'DELETE', headers: getHeaders() }).then(r => handleResponse<void>(r)),
  getBlob: (path: string) => fetch(`${API_BASE}${path}`, { headers: getHeaders(false) }).then(r => handleBlobResponse(r)),
};

export { ApiError };
