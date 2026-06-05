package com.agendai.database;

import com.agendai.app.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Insere dados de exemplo quando o banco está vazio (útil para demonstração).
 */
public final class DataSeeder {

    private DataSeeder() {
    }

    public static void seedIfEmpty() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            seedUsuariosIfEmpty(conn);

            if (count(conn, "tutor") > 0) {
                System.out.println("[DB] Dados clínicos já existem — seed parcial ignorado.");
                return;
            }

            int tutorMaria = insertTutor(conn, "Maria Silva", "00000000000", "11999999999", "Rua Exemplo, 100");
            insertTutor(conn, "João Santos", "11122233344", "21988887777", "Av. Central, 250");

            int animalRex = insertAnimal(conn, "Rex", "Cão", "Labrador", 28.5, tutorMaria);
            insertAnimal(conn, "Mimi", "Gato", "Siamês", 4.2, tutorMaria);

            int vetAna = insertVeterinario(conn, "Dra. Ana Souza", "CRMV-SP-12345", "Clínica Geral", "(11) 91234-5678");
            insertVeterinario(conn, "Dr. Carlos Lima", "CRMV-RJ-67890", "Ortopedia", "(21) 98765-4321");

            insertConsulta(conn, animalRex, vetAna, "2026-06-10T09:00", "Agendada");

            updateDashboard(conn, "Tutores", 2);
            updateDashboard(conn, "Animais", 2);
            updateDashboard(conn, "Veterinários", 2);
            updateDashboard(conn, "Consultas", 1);

            System.out.println("[DB] Dados de exemplo inseridos.");

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao popular banco de dados", e);
        }
    }

    /** Usuários padrão para login (senhas hasheadas com SHA-256) */
    private static void seedUsuariosIfEmpty(Connection conn) throws SQLException {
        if (count(conn, "usuario") > 0) {
            return;
        }

        insertUsuario(conn, "Administrador", "admin@agendavet.com", "admin123", "admin");
        insertUsuario(conn, "Recepção", "recepcao@agendavet.com", "recepcao123", "recepcao");

        System.out.println("[DB] Usuários de login criados:");
        System.out.println("      admin@agendavet.com / admin123");
        System.out.println("      recepcao@agendavet.com / recepcao123");
    }

    private static void insertUsuario(Connection conn, String nome, String email, String senha, String perfil)
            throws SQLException {
        String sql = "INSERT INTO usuario (nome, email, senha, perfil) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, email.toLowerCase());
            ps.setString(3, PasswordUtil.hash(senha));
            ps.setString(4, perfil);
            ps.executeUpdate();
        }
    }

    private static int count(Connection conn, String table) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + table);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static int insertTutor(Connection conn, String nome, String cpf, String tel, String end)
            throws SQLException {
        String sql = "INSERT INTO tutor (nome, cpf, telefone, endereco) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setString(2, cpf);
            ps.setString(3, tel);
            ps.setString(4, end);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    private static int insertAnimal(Connection conn, String nome, String especie, String raca, double peso, int tutorId)
            throws SQLException {
        String sql = "INSERT INTO animal (nome, especie, raca, peso, tutor_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setString(2, especie);
            ps.setString(3, raca);
            ps.setDouble(4, peso);
            ps.setInt(5, tutorId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    private static int insertVeterinario(Connection conn, String nome, String crmv, String esp, String tel)
            throws SQLException {
        String sql = "INSERT INTO veterinario (nome, crmv, especialidade, telefone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setString(2, crmv);
            ps.setString(3, esp);
            ps.setString(4, tel);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    private static void insertConsulta(Connection conn, int animalId, int vetId, String data, String status)
            throws SQLException {
        String sql = "INSERT INTO consulta (animal_id, veterinario_id, data_consulta, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, animalId);
            ps.setInt(2, vetId);
            ps.setString(3, data);
            ps.setString(4, status);
            ps.executeUpdate();
        }
    }

    private static void updateDashboard(Connection conn, String titulo, int valor) throws SQLException {
        String sql = "INSERT INTO dashboard (titulo, valor) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setInt(2, valor);
            ps.executeUpdate();
        }
    }
}
