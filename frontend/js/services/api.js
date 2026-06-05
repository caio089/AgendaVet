/**
 * Cliente HTTP da API REST AgendaVet.
 * Envia token de autenticação em todas as requisições protegidas.
 */

import { clearSession, getToken, redirectToLogin } from './auth.js';

const API_BASE = '/api';

async function request(path, options = {}, authRequired = true) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };

  if (authRequired) {
    const token = getToken();
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (response.status === 204) return null;

  const data = await response.json().catch(() => ({}));

  if (response.status === 401 && authRequired) {
    clearSession();
    redirectToLogin();
    throw new Error('Sessão expirada. Faça login novamente.');
  }

  if (!response.ok) {
    throw new Error(data.message || `Erro HTTP ${response.status}`);
  }

  return data;
}

function createApiRepo(resource) {
  return {
    listar: () => request(`/${resource}`),
    buscarPorId: (id) => request(`/${resource}/${id}`),
    salvar: (payload) => request(`/${resource}`, { method: 'POST', body: JSON.stringify(payload) }),
    atualizar: (id, payload) =>
      request(`/${resource}/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
    deletar: (id) => request(`/${resource}/${id}`, { method: 'DELETE' }),
  };
}

export const TutorRepo = createApiRepo('tutores');
export const AnimalRepo = createApiRepo('animais');
export const VeterinarioRepo = createApiRepo('veterinarios');
export const ConsultaRepo = createApiRepo('consultas');

export async function fetchDashboardStats() {
  return request('/dashboard');
}

export { login, logout, validateSession } from './auth.js';
