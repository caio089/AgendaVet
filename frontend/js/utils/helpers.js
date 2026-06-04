/**
 * Funções utilitárias reutilizadas nas páginas do frontend.
 */

/**
 * Exibe mensagem temporária de feedback ao usuário.
 * @param {string} message
 * @param {'success'|'error'|'info'} type
 */
export function showToast(message, type = 'info') {
  const toast = document.getElementById('toast');
  if (!toast) return;

  const colors = {
    success: 'bg-emerald-600',
    error: 'bg-red-600',
    info: 'bg-sky-600',
  };

  toast.textContent = message;
  toast.className = `fixed bottom-6 right-6 z-50 rounded-lg px-4 py-3 text-sm text-white shadow-lg transition-opacity ${colors[type] ?? colors.info}`;
  toast.classList.remove('hidden', 'opacity-0');

  clearTimeout(showToast._timer);
  showToast._timer = setTimeout(() => {
    toast.classList.add('opacity-0');
    setTimeout(() => toast.classList.add('hidden'), 300);
  }, 3200);
}

/**
 * Formata data/hora ISO para exibição em pt-BR.
 * @param {string} isoDate
 * @returns {string}
 */
export function formatDateTime(isoDate) {
  if (!isoDate) return '-';
  const date = new Date(isoDate);
  if (Number.isNaN(date.getTime())) return isoDate;
  return date.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

/**
 * Retorna badge HTML com cor conforme status da consulta.
 * @param {string} status
 * @returns {string}
 */
export function statusBadge(status) {
  const map = {
    Agendada: 'bg-amber-100 text-amber-800',
    Confirmada: 'bg-sky-100 text-sky-800',
    Realizada: 'bg-emerald-100 text-emerald-800',
    Cancelada: 'bg-red-100 text-red-800',
  };

  const css = map[status] ?? 'bg-slate-100 text-slate-700';
  return `<span class="inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ${css}">${status}</span>`;
}

/**
 * Preenche um <select> com opções.
 * @param {HTMLSelectElement} select
 * @param {Array<{value: string|number, label: string}>} options
 * @param {string|number|null} selectedValue
 */
export function fillSelect(select, options, selectedValue = null) {
  select.innerHTML = options
    .map(
      (opt) =>
        `<option value="${opt.value}" ${String(opt.value) === String(selectedValue) ? 'selected' : ''}>${opt.label}</option>`
    )
    .join('');
}

/**
 * Lê valores de um formulário HTML como objeto simples.
 * @param {HTMLFormElement} form
 * @returns {Record<string, string>}
 */
export function getFormData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

/**
 * Destaca o link ativo no menu lateral conforme a URL atual.
 * @param {string} currentPage - Nome do arquivo, ex: "tutores.html"
 */
export function setActiveNav(currentPage) {
  document.querySelectorAll('[data-nav]').forEach((link) => {
    const isActive = link.getAttribute('href')?.endsWith(currentPage);
    link.classList.toggle('bg-teal-700', isActive);
    link.classList.toggle('text-white', isActive);
    link.classList.toggle('text-teal-100', !isActive);
  });
}
