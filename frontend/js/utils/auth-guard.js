/**
 * Proteção de páginas — exige login antes de carregar o sistema.
 */

import { getUser, logout, redirectToLogin, validateSession } from '../services/auth.js';

/** Bloqueia página se usuário não estiver autenticado */
export async function requireAuth() {
  const usuario = await validateSession();
  if (!usuario) {
    redirectToLogin();
    throw new Error('Redirecionando para login...');
  }
  return usuario;
}

/** Exibe nome do usuário e botão sair na sidebar */
export function setupUserPanel() {
  const panel = document.getElementById('user-panel');
  const logoutBtn = document.getElementById('btn-logout');
  const usuario = getUser();

  if (panel && usuario) {
    panel.innerHTML = `
      <p class="text-xs text-teal-200">Logado como</p>
      <p class="mt-1 font-semibold text-white">${usuario.nome}</p>
      <p class="text-xs text-teal-100">${usuario.email} · ${usuario.perfil}</p>
    `;
  }

  if (logoutBtn) {
    logoutBtn.addEventListener('click', async () => {
      await logout();
      redirectToLogin();
    });
  }
}
