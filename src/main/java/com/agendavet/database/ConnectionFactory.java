package com.agendavet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnectionFactory {

    private static final String URL = "jdbc:sqlite:agendavet.db";

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        criarTabelaTutor(connection);
        return connection;
    }

    private static void criarTabelaTutor(Connection connection) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS tutor (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    cpf TEXT NOT NULL UNIQUE,
                    telefone TEXT,
                    endereco TEXT
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
