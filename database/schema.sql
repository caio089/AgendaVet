-- =============================================================================
-- AgendaVet — Schema SQLite
-- Sistema de gestão de clínica veterinária (projeto acadêmico)
--
-- Autor do schema: Icaro Ryan (DatabaseInitializer.java)
-- Banco gerado automaticamente na raiz do projeto: agendavet.db
--
-- Como executar manualmente (opcional):
--   sqlite3 agendavet.db < database/schema.sql
-- =============================================================================

PRAGMA foreign_keys = ON;

-- -----------------------------------------------------------------------------
-- USUARIO — Login e controle de acesso
-- Responsável: módulo de autenticação (Caio — Frontend e Arquiteto)
-- Senhas: armazenadas com hash SHA-256 (ver DataSeeder.java)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    nome    TEXT    NOT NULL,
    email   TEXT    NOT NULL UNIQUE,
    senha   TEXT    NOT NULL,
    perfil  TEXT    NOT NULL DEFAULT 'admin'
);

-- -----------------------------------------------------------------------------
-- TUTOR — Responsável legal pelos animais
-- Responsável: Ryan-nextLvl
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tutor (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    nome      TEXT    NOT NULL,
    cpf       TEXT    NOT NULL UNIQUE,
    telefone  TEXT,
    endereco  TEXT
);

-- -----------------------------------------------------------------------------
-- ANIMAL — Pets cadastrados na clínica
-- Responsável: João Manoel
-- Relacionamento: N animais → 1 tutor (tutor_id)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS animal (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    nome      TEXT    NOT NULL,
    especie   TEXT    NOT NULL,
    raca      TEXT,
    peso      REAL    NOT NULL,
    tutor_id  INTEGER NOT NULL,
    FOREIGN KEY (tutor_id) REFERENCES tutor(id)
);

-- -----------------------------------------------------------------------------
-- VETERINARIO — Profissionais da clínica
-- Responsável: Ismaelnz0
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS veterinario (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    nome           TEXT    NOT NULL,
    crmv           TEXT    NOT NULL UNIQUE,
    especialidade  TEXT    NOT NULL,
    telefone       TEXT    NOT NULL
);

-- -----------------------------------------------------------------------------
-- CONSULTA — Agendamentos
-- Responsável: Erick Ruan
-- Relacionamentos: animal_id → animal | veterinario_id → veterinario
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS consulta (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    animal_id       INTEGER NOT NULL,
    veterinario_id  INTEGER NOT NULL,
    data_consulta   TEXT    NOT NULL,
    status          TEXT    NOT NULL,
    FOREIGN KEY (animal_id)      REFERENCES animal(id),
    FOREIGN KEY (veterinario_id) REFERENCES veterinario(id)
);

-- -----------------------------------------------------------------------------
-- DASHBOARD — Contadores exibidos no painel inicial
-- Responsável: Icaro Ryan
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dashboard (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo  TEXT    NOT NULL,
    valor   INTEGER NOT NULL
);

-- =============================================================================
-- DIAGRAMA DE RELACIONAMENTOS
--
--   tutor (1) ──────< animal (N)
--                         │
--                         └──< consulta (N) >── veterinario (1)
--
--   usuario — autenticação independente
--   dashboard — métricas agregadas
-- =============================================================================

-- =============================================================================
-- DADOS DE EXEMPLO
-- Inseridos automaticamente pelo Java (DataSeeder.java) na 1ª execução.
-- As senhas abaixo são apenas referência — no banco ficam hasheadas (SHA-256).
--
--   admin@agendavet.com    / admin123     (perfil: admin)
--   recepcao@agendavet.com / recepcao123  (perfil: recepcao)
-- =============================================================================
