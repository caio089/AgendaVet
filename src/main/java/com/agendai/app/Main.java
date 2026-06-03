package com.agendai.app;

import com.agendai.database.DatabaseConnection;
import com.agendai.database.DatabaseInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        DatabaseInitializer.init();

        int tutorId = garantirTutorExemplo();

        AnimalDAO dao = new AnimalDAOImpl();

        Animal rex = new Animal("Rex", "Cão", "Labrador", 28.5, tutorId);
        dao.salvar(rex);

        System.out.println("Todos: " + dao.listar());
        System.out.println("Por id: " + dao.buscarPorId(rex.getId()));
    }

    private static int garantirTutorExemplo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement count = conn.prepareStatement("SELECT COUNT(*) FROM tutor");
                 ResultSet rs = count.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    try (PreparedStatement first = conn.prepareStatement("SELECT id FROM tutor LIMIT 1");
                         ResultSet idRs = first.executeQuery()) {
                        if (idRs.next()) {
                            return idRs.getInt(1);
                        }
                    }
                }
            }

            String sql = "INSERT INTO tutor (nome, cpf, telefone, endereco) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Maria Silva");
                ps.setString(2, "00000000000");
                ps.setString(3, "11999999999");
                ps.setString(4, "Rua Exemplo, 100");
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] garantirTutorExemplo: " + e.getMessage());
        }

        return 1;
    }
}
