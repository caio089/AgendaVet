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
    private static Connection instance;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                instance = DriverManager.getConnection(URL);
                instance.setAutoCommit(true);
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                        "Driver SQLite não encontrado. Verifique a dependência sqlite-jdbc.",
                        e
                );
            }
        }
        return instance;
    }

    public static void fecharConexao() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) {
                    instance.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            } finally {
                instance = null;
            }
        }
    }
}
