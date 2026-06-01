package com.agendai.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:clinica_veterinaria.db";

    public static Connection getConnection() {
        try {

            Class.forName("org.sqlite.JDBC");

            return DriverManager.getConnection(URL);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar com o banco.", e);
        }
    }
}