package com.agendai.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Cria todas as tabelas do sistema (módulo Icaro Ryan).
 * Chamado uma vez na inicialização do servidor.
 */
public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void init() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS usuario (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        email TEXT NOT NULL UNIQUE,
                        senha TEXT NOT NULL,
                        perfil TEXT NOT NULL DEFAULT 'admin'
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tutor (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        cpf TEXT NOT NULL UNIQUE,
                        telefone TEXT,
                        endereco TEXT
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS animal (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        especie TEXT NOT NULL,
                        raca TEXT,
                        peso REAL NOT NULL,
                        tutor_id INTEGER NOT NULL,
                        FOREIGN KEY (tutor_id) REFERENCES tutor(id)
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS veterinario (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        crmv TEXT NOT NULL UNIQUE,
                        especialidade TEXT NOT NULL,
                        telefone TEXT NOT NULL
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS consulta (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        animal_id INTEGER NOT NULL,
                        veterinario_id INTEGER NOT NULL,
                        data_consulta TEXT NOT NULL,
                        status TEXT NOT NULL,
                        FOREIGN KEY (animal_id) REFERENCES animal(id),
                        FOREIGN KEY (veterinario_id) REFERENCES veterinario(id)
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS dashboard (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        titulo TEXT NOT NULL,
                        valor INTEGER NOT NULL
                    )
                    """);

            System.out.println("[DB] Tabelas verificadas/criadas com sucesso.");

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao inicializar banco de dados", e);
        }
    }
}
