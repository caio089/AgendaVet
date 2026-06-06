/**
 * CRUD de Consultas — consome API REST /api/consultas
 */

import { AnimalRepo, ConsultaRepo, TutorRepo, VeterinarioRepo } from '../services/api.js';
import { STATUS_CONSULTA } from '../config/constants.js';
import { requireAuth, setupUserPanel } from '../utils/auth-guard.js';
import { fillSelect, formatDateTime, getFormData, setActiveNav, showToast, statusBadge } from '../utils/helpers.js';

const form = document.getElementById('consulta-form');
const tbody = document.getElementById('consulta-table');
const animalSelect = document.getElementById('animalId');
const vetSelect = document.getElementById('veterinarioId');
const statusSelect = document.getElementById('status');
const formTitle = document.getElementById('form-title');
const btnCancel = document.getElementById('btn-cancel');
let editingId = null;

async function loadSelectOptions(animalId = null, vetId = null, status = 'Agendada') {
  const [animais, vets, tutores] = await Promise.all([
    AnimalRepo.listar(),
    VeterinarioRepo.listar(),
    TutorRepo.listar(),
  ]);

  const animalOptions = animais.map((a) => {
    const tutor = tutores.find((t) => t.id === a.tutorId);
    return { value: a.id, label: `${a.nome} — Tutor: ${tutor?.nome ?? '?'}` };
  });

  const vetOptions = vets.map((v) => ({
    value: v.id,
    label: `${v.nome} (${v.especialidade})`,
  }));

  fillSelect(
    animalSelect,
    [{ value: '', label: animais.length ? 'Selecione o animal' : 'Cadastre um animal primeiro' }, ...animalOptions],
    animalId
  );

  fillSelect(
    vetSelect,
    [{ value: '', label: vets.length ? 'Selecione o veterinário' : 'Cadastre um veterinário primeiro' }, ...vetOptions],
    vetId
  );

  fillSelect(
    statusSelect,
    STATUS_CONSULTA.map((s) => ({ value: s, label: s })),
    status
  );

  animalSelect.disabled = animais.length === 0;
  vetSelect.disabled = vets.length === 0;
}

async function renderTable() {
  const [consultas, animais, vets] = await Promise.all([
    ConsultaRepo.listar(),
    AnimalRepo.listar(),
    VeterinarioRepo.listar(),
  ]);

  if (consultas.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-8 text-center text-slate-500">Nenhuma consulta agendada.</td></tr>`;
    return;
  }

  tbody.innerHTML = consultas
    .map((c) => {
      const animal = animais.find((a) => a.id === c.animalId);
      const vet = vets.find((v) => v.id === c.veterinarioId);

      return `
        <tr class="table-row-hover border-t border-slate-100">
          <td class="px-4 py-3 font-medium text-slate-800">${animal?.nome ?? '—'}</td>
          <td class="px-4 py-3">${vet?.nome ?? '—'}</td>
          <td class="px-4 py-3">${formatDateTime(c.dataConsulta)}</td>
          <td class="px-4 py-3">${statusBadge(c.status)}</td>
          <td class="px-4 py-3 text-right space-x-2">
            <button data-edit="${c.id}" class="rounded-lg bg-sky-50 px-3 py-1.5 text-sm text-sky-700 hover:bg-sky-100">Editar</button>
            <button data-delete="${c.id}" class="rounded-lg bg-red-50 px-3 py-1.5 text-sm text-red-700 hover:bg-red-100">Excluir</button>
          </td>
        </tr>
      `;
    })
    .join('');
}

function resetForm() {
  editingId = null;
  form.reset();
  formTitle.textContent = 'Nova Consulta';
  btnCancel.classList.add('hidden');
  loadSelectOptions();
}

async function loadForEdit(id) {
  const consulta = await ConsultaRepo.buscarPorId(id);
  if (!consulta) return;

  editingId = id;
  form.dataConsulta.value = consulta.dataConsulta;
  await loadSelectOptions(consulta.animalId, consulta.veterinarioId, consulta.status);
  formTitle.textContent = 'Editar Consulta';
  btnCancel.classList.remove('hidden');
}

function validate(data) {
  if (!data.animalId) return 'Selecione o animal.';
  if (!data.veterinarioId) return 'Selecione o veterinário.';
  if (!data.dataConsulta) return 'Informe data e hora da consulta.';
  if (!data.status) return 'Selecione o status.';
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
      animalId: Number(data.animalId),
      veterinarioId: Number(data.veterinarioId),
      dataConsulta: data.dataConsulta,
      status: data.status,
    };

    try {
      if (editingId) {
        await ConsultaRepo.atualizar(editingId, payload);
        showToast('Consulta atualizada!', 'success');
      } else {
        await ConsultaRepo.salvar(payload);
        showToast('Consulta agendada!', 'success');
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

    if (deleteBtn && confirm('Deseja cancelar/excluir esta consulta?')) {
      try {
        await ConsultaRepo.deletar(deleteBtn.dataset.delete);
        showToast('Consulta removida.', 'info');
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
    setActiveNav('consultas.html');
    await loadSelectOptions();
    await renderTable();
    bindEvents();
  } catch (err) {
    showToast('Erro ao carregar consultas: ' + err.message, 'error');
  }
});
