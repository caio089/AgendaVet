package com.agendai.app;

import com.agendai.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public void salvar(Usuario usuario) {
        String sql = """
                INSERT INTO usuario (nome, email, senha, perfil)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getPerfil());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }

            System.out.println("[SALVO] " + usuario);

        } catch (SQLException e) {
            System.err.println("[ERRO] salvar: " + e.getMessage());
        }
    }

    @Override
    public List<Usuario> listar() {
        String sql = "SELECT id, nome, email, senha, perfil FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] listar: " + e.getMessage());
        }

        return usuarios;
    }

    @Override
    public Usuario buscarPorId(int id) {
        String sql = "SELECT id, nome, email, senha, perfil FROM usuario WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] buscarPorId: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = """
                UPDATE usuario
                   SET nome   = ?,
                       email  = ?,
                       senha  = ?,
                       perfil = ?
                 WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getPerfil());
            ps.setInt(5, usuario.getId());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("[ATUALIZADO] " + usuario);
            } else {
                System.out.println("[AVISO] Nenhum usuário encontrado com id=" + usuario.getId());
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] atualizar: " + e.getMessage());
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("[DELETADO] Usuario id=" + id);
            } else {
                System.out.println("[AVISO] Nenhum usuário encontrado com id=" + id);
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] deletar: " + e.getMessage());
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                rs.getString("perfil")
        );
    }
}
