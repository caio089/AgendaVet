package com.agendai.dao;

import com.agendai.config.DatabaseConnection;
import com.agendai.model.Animal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAOImpl implements AnimalDAO {

    @Override
    public void salvar(Animal animal) {
        String sql = """
                INSERT INTO animal (nome, especie, raca, peso, tutor_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, animal.getNome());
            ps.setString(2, animal.getEspecie());
            ps.setString(3, animal.getRaca());
            ps.setDouble(4, animal.getPeso());
            ps.setInt(5, animal.getTutorId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    animal.setId(rs.getInt(1));
                }
            }

            System.out.println("[SALVO] " + animal);

        } catch (SQLException e) {
            System.err.println("[ERRO] salvar: " + e.getMessage());
        }
    }

    @Override
    public List<Animal> listar() {
        String sql = "SELECT id, nome, especie, raca, peso, tutor_id FROM animal";
        List<Animal> animais = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                animais.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] listar: " + e.getMessage());
        }

        return animais;
    }

    @Override
    public Animal buscarPorId(int id) {
        String sql = "SELECT id, nome, especie, raca, peso, tutor_id FROM animal WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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
    public void atualizar(Animal animal) {
        String sql = """
                UPDATE animal
                   SET nome     = ?,
                       especie  = ?,
                       raca     = ?,
                       peso     = ?,
                       tutor_id = ?
                 WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, animal.getNome());
            ps.setString(2, animal.getEspecie());
            ps.setString(3, animal.getRaca());
            ps.setDouble(4, animal.getPeso());
            ps.setInt(5, animal.getTutorId());
            ps.setInt(6, animal.getId());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("[ATUALIZADO] " + animal);
            } else {
                System.out.println("[AVISO] Nenhum animal encontrado com id=" + animal.getId());
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] atualizar: " + e.getMessage());
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM animal WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("[DELETADO] Animal id=" + id);
            } else {
                System.out.println("[AVISO] Nenhum animal encontrado com id=" + id);
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] deletar: " + e.getMessage());
        }
    }

    private Animal mapear(ResultSet rs) throws SQLException {
        return new Animal(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("especie"),
                rs.getString("raca"),
                rs.getDouble("peso"),
                rs.getInt("tutor_id")
        );
    }
}
