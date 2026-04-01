import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { api } from './api';

describe('api client', () => {
  const fetchMock = vi.fn();

  beforeEach(() => {
    vi.stubGlobal('fetch', fetchMock);
    localStorage.clear();
    fetchMock.mockReset();
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('sends only the bearer token header for authenticated requests', async () => {
    localStorage.setItem('accessToken', 'token-123');
    fetchMock.mockResolvedValue(new Response(JSON.stringify({ ok: true }), { status: 200 }));

    await api.get('/auth/me');

    expect(fetchMock).toHaveBeenCalledWith('/api/v1/auth/me', {
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer token-123',
      },
    });
  });

  it('raises ApiError with server details', async () => {
    fetchMock.mockResolvedValue(new Response(JSON.stringify({
      code: 'VALIDATION',
      detail: 'Datos invalidos',
      fieldErrors: [{ field: 'rfc', message: 'RFC requerido' }],
    }), { status: 422 }));

    await expect(api.post('/clients', {})).rejects.toMatchObject({
      status: 422,
      code: 'VALIDATION',
      name: 'ApiError',
      message: 'Datos invalidos',
    });
  });
});
