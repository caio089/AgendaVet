package com.agendavet.dao;

import com.agendavet.database.ConnectionFactory;
import com.agendavet.model.Tutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TutorDAOImpl implements TutorDAO {

    @Override
    public void salvar(Tutor tutor) throws SQLException {
        String sql = """
                INSERT INTO tutor (nome, cpf, telefone, endereco)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, tutor.getNome());
            statement.setString(2, tutor.getCpf());
            statement.setString(3, tutor.getTelefone());
            statement.setString(4, tutor.getEndereco());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tutor.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    @Override
    public List<Tutor> listar() throws SQLException {
        String sql = "SELECT id, nome, cpf, telefone, endereco FROM tutor ORDER BY nome";
        List<Tutor> tutores = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tutores.add(mapearResultSet(resultSet));
            }
        }

        return tutores;
    }

    @Override
    public Optional<Tutor> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome, cpf, telefone, endereco FROM tutor WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapearResultSet(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void atualizar(Tutor tutor) throws SQLException {
        String sql = """
                UPDATE tutor
                SET nome = ?, cpf = ?, telefone = ?, endereco = ?
                WHERE id = ?
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tutor.getNome());
            statement.setString(2, tutor.getCpf());
            statement.setString(3, tutor.getTelefone());
            statement.setString(4, tutor.getEndereco());
            statement.setLong(5, tutor.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM tutor WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private Tutor mapearResultSet(ResultSet resultSet) throws SQLException {
        Tutor tutor = new Tutor();
        tutor.setId(resultSet.getLong("id"));
        tutor.setNome(resultSet.getString("nome"));
        tutor.setCpf(resultSet.getString("cpf"));
        tutor.setTelefone(resultSet.getString("telefone"));
        tutor.setEndereco(resultSet.getString("endereco"));
        return tutor;
    }
}
