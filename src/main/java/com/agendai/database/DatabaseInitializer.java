package com.agendai.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DatabaseInitializer {

    public static void init() {

        try (Connection conn = DatabaseConnection.getConnection()) {

            String usuario =
                    "CREATE TABLE IF NOT EXISTS usuario (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT," +
                            "email TEXT," +
                            "senha TEXT," +
                            "perfil TEXT" +
                            ")";

            String tutor =
                    "CREATE TABLE IF NOT EXISTS tutor (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT," +
                            "cpf TEXT," +
                            "telefone TEXT," +
                            "endereco TEXT" +
                            ")";

            String animal =
                    "CREATE TABLE IF NOT EXISTS animal (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT," +
                            "especie TEXT," +
                            "raca TEXT," +
                            "peso REAL," +
                            "tutor_id INTEGER" +
                            ")";

            String veterinario =
                    "CREATE TABLE IF NOT EXISTS veterinario (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT," +
                            "crmv TEXT," +
                            "especialidade TEXT," +
                            "telefone TEXT" +
                            ")";

            String consulta =
                    "CREATE TABLE IF NOT EXISTS consulta (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "animal_id INTEGER," +
                            "veterinario_id INTEGER," +
                            "data_consulta TEXT," +
                            "status TEXT" +
                            ")";

            String dashboard =
                    "CREATE TABLE IF NOT EXISTS dashboard (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "titulo TEXT," +
                            "valor INTEGER" +
                            ")";

            PreparedStatement stmtUsuario = conn.prepareStatement(usuario);
            PreparedStatement stmtTutor = conn.prepareStatement(tutor);
            PreparedStatement stmtAnimal = conn.prepareStatement(animal);
            PreparedStatement stmtVeterinario = conn.prepareStatement(veterinario);
            PreparedStatement stmtConsulta = conn.prepareStatement(consulta);
            PreparedStatement stmtDashboard = conn.prepareStatement(dashboard);

            stmtUsuario.execute();
            stmtTutor.execute();
            stmtAnimal.execute();
            stmtVeterinario.execute();
            stmtConsulta.execute();
            stmtDashboard.execute();

            System.out.println("Banco criado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}