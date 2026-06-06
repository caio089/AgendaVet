/**
 * CRUD de Animais — consome API REST /api/animais
 */

import { AnimalRepo, TutorRepo } from '../services/api.js';
import { requireAuth, setupUserPanel } from '../utils/auth-guard.js';
import { fillSelect, getFormData, setActiveNav, showToast } from '../utils/helpers.js';

const form = document.getElementById('animal-form');
const tbody = document.getElementById('animal-table');
const tutorSelect = document.getElementById('tutorId');
const formTitle = document.getElementById('form-title');
const btnCancel = document.getElementById('btn-cancel');
let editingId = null;

async function loadTutorOptions(selectedId = null) {
  const tutores = await TutorRepo.listar();
  const options = tutores.map((t) => ({
    value: t.id,
    label: `${t.nome} (CPF: ${t.cpf})`,
  }));

  if (options.length === 0) {
    tutorSelect.innerHTML = '<option value="">Cadastre um tutor primeiro</option>';
    tutorSelect.disabled = true;
    return;
  }

  tutorSelect.disabled = false;
  fillSelect(tutorSelect, [{ value: '', label: 'Selecione o tutor' }, ...options], selectedId);
}

async function renderTable() {
  const [animais, tutores] = await Promise.all([AnimalRepo.listar(), TutorRepo.listar()]);

  if (animais.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" class="px-4 py-8 text-center text-slate-500">Nenhum animal cadastrado.</td></tr>`;
    return;
  }

  tbody.innerHTML = animais
    .map((a) => {
      const tutor = tutores.find((t) => t.id === a.tutorId);
      return `
        <tr class="table-row-hover border-t border-slate-100">
          <td class="px-4 py-3 font-medium text-slate-800">${a.nome}</td>
          <td class="px-4 py-3">${a.especie}</td>
          <td class="px-4 py-3">${a.raca ?? '—'}</td>
          <td class="px-4 py-3">${Number(a.peso).toFixed(1)} kg</td>
          <td class="px-4 py-3">${tutor?.nome ?? '—'}</td>
          <td class="px-4 py-3 text-right space-x-2">
            <button data-edit="${a.id}" class="rounded-lg bg-sky-50 px-3 py-1.5 text-sm text-sky-700 hover:bg-sky-100">Editar</button>
            <button data-delete="${a.id}" class="rounded-lg bg-red-50 px-3 py-1.5 text-sm text-red-700 hover:bg-red-100">Excluir</button>
          </td>
        </tr>
      `;
    })
    .join('');
}

function resetForm() {
  editingId = null;
  form.reset();
  formTitle.textContent = 'Novo Animal';
  btnCancel.classList.add('hidden');
  loadTutorOptions();
}

async function loadForEdit(id) {
  const animal = await AnimalRepo.buscarPorId(id);
  if (!animal) return;

  editingId = id;
  form.nome.value = animal.nome;
  form.especie.value = animal.especie;
  form.raca.value = animal.raca ?? '';
  form.peso.value = animal.peso;
  await loadTutorOptions(animal.tutorId);
  formTitle.textContent = 'Editar Animal';
  btnCancel.classList.remove('hidden');
}

function validate(data) {
  if (!data.nome?.trim()) return 'Informe o nome do animal.';
  if (!data.especie?.trim()) return 'Informe a espécie.';
  if (!data.tutorId) return 'Selecione o tutor responsável.';
  if (Number(data.peso) <= 0) return 'Informe um peso válido.';
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
      especie: data.especie.trim(),
      raca: data.raca.trim(),
      peso: Number(data.peso),
      tutorId: Number(data.tutorId),
    };

    try {
      if (editingId) {
        await AnimalRepo.atualizar(editingId, payload);
        showToast('Animal atualizado!', 'success');
      } else {
        await AnimalRepo.salvar(payload);
        showToast('Animal cadastrado!', 'success');
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

    if (deleteBtn && confirm('Deseja excluir este animal?')) {
      try {
        await AnimalRepo.deletar(deleteBtn.dataset.delete);
        showToast('Animal removido.', 'info');
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
    setActiveNav('animais.html');
    await loadTutorOptions();
    await renderTable();
    bindEvents();
  } catch (err) {
    showToast('Erro ao carregar animais: ' + err.message, 'error');
  }
});
