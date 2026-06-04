package com.agendai.app;

import com.agendai.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT id, nome, email, senha, perfil FROM usuario WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Usuario autenticar(String email, String senhaPlana) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) {
            return null;
        }
        if (!PasswordUtil.matches(senhaPlana, usuario.getSenha())) {
            return null;
        }
        return usuario;
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
