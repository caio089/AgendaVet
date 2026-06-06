/**
 * Serviço de autenticação — token JWT-like em sessionStorage.
 */

const TOKEN_KEY = 'agendavet_token';
const USER_KEY = 'agendavet_user';

export function getToken() {
  return sessionStorage.getItem(TOKEN_KEY);
}

export function getUser() {
  const raw = sessionStorage.getItem(USER_KEY);
  return raw ? JSON.parse(raw) : null;
}

export function setSession(token, usuario) {
  sessionStorage.setItem(TOKEN_KEY, token);
  sessionStorage.setItem(USER_KEY, JSON.stringify(usuario));
}

export function clearSession() {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(USER_KEY);
}

/** Faz login e salva sessão local */
export async function login(email, senha) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, senha }),
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message || 'Falha no login');
  }

  setSession(data.token, data.usuario);
  return data;
}

/** Encerra sessão no servidor e localmente */
export async function logout() {
  const token = getToken();
  if (token) {
    await fetch('/api/auth/logout', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
    }).catch(() => {});
  }
  clearSession();
}

/** Valida sessão atual com o backend */
export async function validateSession() {
  const token = getToken();
  if (!token) return null;

  const response = await fetch('/api/auth/me', {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!response.ok) {
    clearSession();
    return null;
  }

  const usuario = await response.json();
  sessionStorage.setItem(USER_KEY, JSON.stringify(usuario));
  return usuario;
}

/** Redireciona para login se não autenticado */
export function redirectToLogin() {
  const loginPath = window.location.pathname.includes('/pages/')
    ? '../login.html'
    : 'login.html';
  window.location.href = loginPath;
}
