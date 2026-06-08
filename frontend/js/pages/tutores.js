/**
 * CRUD de Tutores — consome API REST /api/tutores
 */

import { TutorRepo } from '../services/api.js';
import { requireAuth, setupNav, setupUserPanel } from '../utils/auth-guard.js';
import { getFormData, setActiveNav, showToast } from '../utils/helpers.js';

const form = document.getElementById('tutor-form');
const tbody = document.getElementById('tutor-table');
const formTitle = document.getElementById('form-title');
const btnCancel = document.getElementById('btn-cancel');
let editingId = null;

async function renderTable() {
  const tutores = await TutorRepo.listar();

  if (tutores.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-8 text-center text-slate-500">Nenhum tutor cadastrado.</td></tr>`;
    return;
  }

  tbody.innerHTML = tutores
    .map(
      (t) => `
      <tr class="table-row-hover border-t border-slate-100">
        <td class="px-4 py-3 font-medium text-slate-800">${t.nome}</td>
        <td class="px-4 py-3">${t.cpf}</td>
        <td class="px-4 py-3">${t.telefone}</td>
        <td class="px-4 py-3">${t.endereco ?? '—'}</td>
        <td class="px-4 py-3 text-right space-x-2">
          <button data-edit="${t.id}" class="rounded-lg bg-sky-50 px-3 py-1.5 text-sm text-sky-700 hover:bg-sky-100">Editar</button>
          <button data-delete="${t.id}" class="rounded-lg bg-red-50 px-3 py-1.5 text-sm text-red-700 hover:bg-red-100">Excluir</button>
        </td>
      </tr>
    `
    )
    .join('');
}

function resetForm() {
  editingId = null;
  form.reset();
  formTitle.textContent = 'Novo Tutor';
  btnCancel.classList.add('hidden');
}

async function loadForEdit(id) {
  const tutor = await TutorRepo.buscarPorId(id);
  if (!tutor) return;

  editingId = id;
  form.nome.value = tutor.nome;
  form.cpf.value = tutor.cpf;
  form.telefone.value = tutor.telefone;
  form.endereco.value = tutor.endereco ?? '';
  formTitle.textContent = 'Editar Tutor';
  btnCancel.classList.remove('hidden');
}

function validate(data) {
  if (!data.nome?.trim()) return 'Informe o nome do tutor.';
  if (!data.cpf?.trim()) return 'Informe o CPF.';
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
      cpf: data.cpf.trim(),
      telefone: data.telefone.trim(),
      endereco: data.endereco.trim(),
    };

    try {
      if (editingId) {
        await TutorRepo.atualizar(editingId, payload);
        showToast('Tutor atualizado com sucesso!', 'success');
      } else {
        await TutorRepo.salvar(payload);
        showToast('Tutor cadastrado com sucesso!', 'success');
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

    if (deleteBtn && confirm('Deseja excluir este tutor?')) {
      try {
        await TutorRepo.deletar(deleteBtn.dataset.delete);
        showToast('Tutor removido.', 'info');
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
    setupUserPanel();
    setupNav();
    setActiveNav('tutores.html');
    await renderTable();
    bindEvents();
  } catch (err) {
    showToast('Erro ao carregar tutores: ' + err.message, 'error');
  }
});
