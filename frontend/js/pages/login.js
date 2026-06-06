/**
 * Página de login — AgendaVet
 */

import { getToken, login, validateSession } from '../services/auth.js';
import { showToast } from '../utils/helpers.js';

const form = document.getElementById('login-form');

/** Se já logado, vai direto ao dashboard */
async function checkExistingSession() {
  if (!getToken()) return;
  const usuario = await validateSession();
  if (usuario) {
    window.location.href = 'index.html';
  }
}

form.addEventListener('submit', async (event) => {
  event.preventDefault();

  const email = form.email.value.trim();
  const senha = form.senha.value;

  if (!email || !senha) {
    showToast('Preencha e-mail e senha.', 'error');
    return;
  }

  try {
    await login(email, senha);
    showToast('Login realizado!', 'success');
    setTimeout(() => {
      window.location.href = 'index.html';
    }, 400);
  } catch (error) {
    showToast(error.message, 'error');
  }
});

document.addEventListener('DOMContentLoaded', checkExistingSession);
