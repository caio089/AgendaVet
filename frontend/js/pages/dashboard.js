/**
 * Lógica da página inicial (Dashboard).
 * Consome a API REST conectada ao SQLite.
 */

import {
  AnimalRepo,
  ConsultaRepo,
  fetchDashboardStats,
  TutorRepo,
  VeterinarioRepo,
} from '../services/api.js';
import { requireAuth, setupNav, setupUserPanel } from '../utils/auth-guard.js';
import { setActiveNav, statusBadge } from '../utils/helpers.js';

/** Monta os cards de estatísticas no topo da página */
async function renderStats() {
  const stats = await fetchDashboardStats();
  const cards = [
    { label: 'Tutores', value: stats.tutores, color: 'bg-teal-500' },
    { label: 'Animais', value: stats.animais, color: 'bg-emerald-500' },
    { label: 'Veterinários', value: stats.veterinarios, color: 'bg-sky-500' },
    { label: 'Consultas', value: stats.consultas, color: 'bg-violet-500' },
  ];

  document.getElementById('stats-cards').innerHTML = cards
    .map(
      (item) => `
      <article class="rounded-2xl bg-white p-5 shadow-sm ring-1 ring-slate-200">
        <p class="text-sm font-medium text-slate-500">${item.label}</p>
        <p class="mt-2 text-3xl font-bold text-slate-800">${item.value}</p>
        <div class="mt-4 h-1.5 w-16 rounded-full ${item.color}"></div>
      </article>
    `
    )
    .join('');
}

/** Lista as consultas mais recentes para visão rápida */
async function renderRecentConsultas() {
  const tbody = document.getElementById('recent-consultas');
  const [tutores, animais, vets, consultas] = await Promise.all([
    TutorRepo.listar(),
    AnimalRepo.listar(),
    VeterinarioRepo.listar(),
    ConsultaRepo.listar(),
  ]);

  const recentes = consultas.slice(0, 5);

  if (recentes.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" class="px-4 py-6 text-center text-slate-500">Nenhuma consulta cadastrada.</td></tr>`;
    return;
  }

  tbody.innerHTML = recentes
    .map((c) => {
      const animal = animais.find((a) => a.id === c.animalId);
      const tutor = tutores.find((t) => t.id === animal?.tutorId);
      const vet = vets.find((v) => v.id === c.veterinarioId);

      return `
        <tr class="table-row-hover border-t border-slate-100">
          <td class="px-4 py-3">${animal?.nome ?? '—'} <span class="text-xs text-slate-400">(${tutor?.nome ?? 'Tutor?'})</span></td>
          <td class="px-4 py-3">${vet?.nome ?? '—'}</td>
          <td class="px-4 py-3">${c.dataConsulta?.replace('T', ' ') ?? '—'}</td>
          <td class="px-4 py-3">${statusBadge(c.status)}</td>
        </tr>
      `;
    })
    .join('');
}

async function init() {
  await requireAuth();
  setupUserPanel();
  setupNav();
  setActiveNav('index.html');
  try {
    await renderStats();
    await renderRecentConsultas();
  } catch (error) {
    console.error(error);
    document.getElementById('stats-cards').innerHTML =
      `<p class="col-span-full rounded-lg bg-red-50 p-4 text-red-700">Erro ao carregar dashboard: ${error.message}. Verifique se o servidor Java está rodando.</p>`;
  }
}

document.addEventListener('DOMContentLoaded', init);
