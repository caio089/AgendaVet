package com.agendai.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton: garante que apenas uma instância de DatabaseConnection exista em toda a aplicação.
public final class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:agendavet.db";

    private static DatabaseConnection instance;

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(true);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver SQLite não encontrado. Verifique a dependência sqlite-jdbc.",
                    e
            );
        }
    }
}
