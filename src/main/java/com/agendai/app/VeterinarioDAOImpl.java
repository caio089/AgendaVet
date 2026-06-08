package com.agendai.app;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agendai.database.DatabaseConnection;

public class VeterinarioDAOImpl implements VeterinarioDAO {

    // -------------------------------------------------------------------------
    // salvar
    // -------------------------------------------------------------------------

    @Override
    public boolean salvar(Veterinario veterinario) {
        final String sql = """
                INSERT INTO veterinario (nome, crmv, especialidade, telefone)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, veterinario.getNome());
            stmt.setString(2, veterinario.getCrmv());
            stmt.setString(3, veterinario.getEspecialidade());
            stmt.setString(4, veterinario.getTelefone());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {
                    if (chavesGeradas.next()) {
                        veterinario.setId(chavesGeradas.getInt(1));
                    }
                }
                System.out.println("Veterinário salvo com ID: " + veterinario.getId());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar veterinário: " + e.getMessage());
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // listar
    // -------------------------------------------------------------------------

    @Override
    public List<Veterinario> listar() {
        final String sql = "SELECT id, nome, crmv, especialidade, telefone FROM veterinario ORDER BY nome";
        List<Veterinario> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar veterinários: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------------------------------------------------
    // buscarPorId
    // -------------------------------------------------------------------------

    @Override
    public Veterinario buscarPorId(int id) {
        final String sql = "SELECT id, nome, crmv, especialidade, telefone FROM veterinario WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar veterinário por ID: " + e.getMessage());
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // atualizar
    // -------------------------------------------------------------------------

    @Override
    public boolean atualizar(Veterinario veterinario) {
        final String sql = """
                UPDATE veterinario
                SET nome = ?, crmv = ?, especialidade = ?, telefone = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, veterinario.getNome());
            stmt.setString(2, veterinario.getCrmv());
            stmt.setString(3, veterinario.getEspecialidade());
            stmt.setString(4, veterinario.getTelefone());
            stmt.setInt(5, veterinario.getId());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Veterinário ID " + veterinario.getId() + " atualizado com sucesso.");
                return true;
            } else {
                System.out.println("Nenhum veterinário encontrado com ID: " + veterinario.getId());
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar veterinário: " + e.getMessage());
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // deletar
    // -------------------------------------------------------------------------

    @Override
    public boolean deletar(int id) {
        final String sql = "DELETE FROM veterinario WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Veterinário ID " + id + " removido com sucesso.");
                return true;
            } else {
                System.out.println("Nenhum veterinário encontrado com ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao deletar veterinário: " + e.getMessage());
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // Método auxiliar de mapeamento
    // -------------------------------------------------------------------------

    /**
     * Mapeia uma linha do ResultSet para um objeto Veterinario.
     */
    private Veterinario mapearResultSet(ResultSet rs) throws SQLException {
        return new Veterinario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("crmv"),
                rs.getString("especialidade"),
                rs.getString("telefone")
        );
    }
}