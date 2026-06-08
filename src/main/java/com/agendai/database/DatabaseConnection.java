package com.agendai.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexão singleton com o banco SQLite do projeto.
 * Arquivo gerado na raiz do projeto: agendavet.db
 */
public final class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:agendavet.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
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

    public static void fecharConexao() {
        // Conexões são criadas por chamada e fechadas via try-with-resources nos DAOs.
    }
}
