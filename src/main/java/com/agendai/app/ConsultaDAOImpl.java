package com.agendai.app; // Módulo de Consultas - Erick Ruan

import com.agendai.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação JDBC do CRUD de Consulta (módulo Erick Ruan).
 */
public class ConsultaDAOImpl implements ConsultaDAO {

    @Override
    public void salvar(Consulta consulta) {
        String sql = """
                INSERT INTO consulta (animal_id, veterinario_id, data_consulta, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, consulta.getAnimalId());
            ps.setInt(2, consulta.getVeterinarioId());
            ps.setString(3, consulta.getDataConsulta());
            ps.setString(4, consulta.getStatus());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    consulta.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar consulta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Consulta> listar() {
        String sql = """
                SELECT id, animal_id, veterinario_id, data_consulta, status
                  FROM consulta
                 ORDER BY data_consulta DESC
                """;
        List<Consulta> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar consultas: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public Consulta buscarPorId(int id) {
        String sql = """
                SELECT id, animal_id, veterinario_id, data_consulta, status
                  FROM consulta
                 WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consulta: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void atualizar(Consulta consulta) {
        String sql = """
                UPDATE consulta
                   SET animal_id = ?,
                       veterinario_id = ?,
                       data_consulta = ?,
                       status = ?
                 WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, consulta.getAnimalId());
            ps.setInt(2, consulta.getVeterinarioId());
            ps.setString(3, consulta.getDataConsulta());
            ps.setString(4, consulta.getStatus());
            ps.setInt(5, consulta.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar consulta: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM consulta WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar consulta: " + e.getMessage(), e);
        }
    }

    private Consulta mapear(ResultSet rs) throws SQLException {
        return new Consulta(
                rs.getInt("id"),
                rs.getInt("animal_id"),
                rs.getInt("veterinario_id"),
                rs.getString("data_consulta"),
                rs.getString("status")
        );
    }
}
