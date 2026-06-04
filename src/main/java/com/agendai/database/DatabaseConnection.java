package com.agendai.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:veterinario.db";
    private static Connection instance;

    private DatabaseConnection() {}

    /**
     * Retorna uma instância única da conexão (Singleton).
     */
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                 instance = DriverManager.getConnection(URL);
                 instance.setAutoCommit(true);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite não encontrado. Verifique se sqlite-jdbc está no classpath.", e);
            }
            
        }
        return instance;
    }

    /**
     * Cria a tabela veterinario caso não exista.
     */
    public static void inicializarBancoDeDados() {
        String sql = """
                CREATE TABLE IF NOT EXISTS veterinario (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome        TEXT    NOT NULL,
                    crmv        TEXT    NOT NULL UNIQUE,
                    especialidade TEXT  NOT NULL,
                    telefone    TEXT    NOT NULL
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Banco de dados inicializado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
        }
    }

    /**
     * Encerra a conexão com o banco de dados.
     * @throws SQLException 
     */
    public static void fecharConexao() throws SQLException {
        if (instance != null && !instance.isClosed()) {
            try {
                instance.close();
                System.out.println("Conexão encerrada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}