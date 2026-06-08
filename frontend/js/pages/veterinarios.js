/**
 * CRUD de Veterinários — consome API REST /api/veterinarios
 */

import { VeterinarioRepo } from '../services/api.js';
import { requireAdmin, requireAuth, setupNav, setupUserPanel } from '../utils/auth-guard.js';
import { getFormData, setActiveNav, showToast } from '../utils/helpers.js';

const form = document.getElementById('vet-form');
const tbody = document.getElementById('vet-table');
const formTitle = document.getElementById('form-title');
const btnCancel = document.getElementById('btn-cancel');
let editingId = null;

async function renderTable() {
  const lista = await VeterinarioRepo.listar();

  if (lista.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-8 text-center text-slate-500">Nenhum veterinário cadastrado.</td></tr>`;
    return;
  }

  tbody.innerHTML = lista
    .map(
      (v) => `
      <tr class="table-row-hover border-t border-slate-100">
        <td class="px-4 py-3 font-medium text-slate-800">${v.nome}</td>
        <td class="px-4 py-3">${v.crmv}</td>
        <td class="px-4 py-3">${v.especialidade}</td>
        <td class="px-4 py-3">${v.telefone}</td>
        <td class="px-4 py-3 text-right space-x-2">
          <button data-edit="${v.id}" class="rounded-lg bg-sky-50 px-3 py-1.5 text-sm text-sky-700 hover:bg-sky-100">Editar</button>
          <button data-delete="${v.id}" class="rounded-lg bg-red-50 px-3 py-1.5 text-sm text-red-700 hover:bg-red-100">Excluir</button>
        </td>
      </tr>
    `
    )
    .join('');
}

function resetForm() {
  editingId = null;
  form.reset();
  formTitle.textContent = 'Novo Veterinário';
  btnCancel.classList.add('hidden');
}

async function loadForEdit(id) {
  const vet = await VeterinarioRepo.buscarPorId(id);
  if (!vet) return;

  editingId = id;
  form.nome.value = vet.nome;
  form.crmv.value = vet.crmv;
  form.especialidade.value = vet.especialidade;
  form.telefone.value = vet.telefone;
  formTitle.textContent = 'Editar Veterinário';
  btnCancel.classList.remove('hidden');
}

function validate(data) {
  if (!data.nome?.trim()) return 'Informe o nome do veterinário.';
  if (!data.crmv?.trim()) return 'Informe o CRMV.';
  if (!data.especialidade?.trim()) return 'Informe a especialidade.';
  if (!data.telefone?.trim()) return 'Informe o telefone.';
  return null;
}

function bindEvents() {
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const data = getFormData(form);
    const error = validate(data);
    if (error) {
      showToast(error, 'error');
      return;
    }

    const payload = {
      nome: data.nome.trim(),
      crmv: data.crmv.trim(),
      especialidade: data.especialidade.trim(),
      telefone: data.telefone.trim(),
    };

    try {
      if (editingId) {
        await VeterinarioRepo.atualizar(editingId, payload);
        showToast('Veterinário atualizado!', 'success');
      } else {
        await VeterinarioRepo.salvar(payload);
        showToast('Veterinário cadastrado!', 'success');
      }
      resetForm();
      await renderTable();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  btnCancel.addEventListener('click', resetForm);

  tbody.addEventListener('click', async (event) => {
    const editBtn = event.target.closest('[data-edit]');
    const deleteBtn = event.target.closest('[data-delete]');

    if (editBtn) await loadForEdit(editBtn.dataset.edit);

    if (deleteBtn && confirm('Deseja excluir este veterinário?')) {
      try {
        await VeterinarioRepo.deletar(deleteBtn.dataset.delete);
        showToast('Veterinário removido.', 'info');
        await renderTable();
      } catch (err) {
        showToast(err.message, 'error');
      }
    }
  });
}

document.addEventListener('DOMContentLoaded', async () => {
  try {
    await requireAuth();
    requireAdmin();
    setupUserPanel();
    setupNav();
    setActiveNav('veterinarios.html');
    await renderTable();
    bindEvents();
  } catch (err) {
    showToast('Erro ao carregar veterinários: ' + err.message, 'error');
  }
});
