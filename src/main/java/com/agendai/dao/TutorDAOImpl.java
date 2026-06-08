package com.agendai.dao;

import com.agendai.config.DatabaseConnection;
import com.agendai.model.Tutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação JDBC do CRUD de Tutor (integrado da branch feature/tutor-crud).
 */
public class TutorDAOImpl implements TutorDAO {

    @Override
    public void salvar(Tutor tutor) {
        String sql = """
                INSERT INTO tutor (nome, cpf, telefone, endereco)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tutor.getNome());
            ps.setString(2, tutor.getCpf());
            ps.setString(3, tutor.getTelefone());
            ps.setString(4, tutor.getEndereco());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    tutor.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar tutor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Tutor> listar() {
        String sql = "SELECT id, nome, cpf, telefone, endereco FROM tutor ORDER BY nome";
        List<Tutor> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tutores: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public Tutor buscarPorId(int id) {
        String sql = "SELECT id, nome, cpf, telefone, endereco FROM tutor WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tutor: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void atualizar(Tutor tutor) {
        String sql = """
                UPDATE tutor
                   SET nome = ?, cpf = ?, telefone = ?, endereco = ?
                 WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tutor.getNome());
            ps.setString(2, tutor.getCpf());
            ps.setString(3, tutor.getTelefone());
            ps.setString(4, tutor.getEndereco());
            ps.setInt(5, tutor.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tutor: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM tutor WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar tutor: " + e.getMessage(), e);
        }
    }

    private Tutor mapear(ResultSet rs) throws SQLException {
        return new Tutor(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("telefone"),
                rs.getString("endereco")
        );
    }
}
