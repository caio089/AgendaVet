package com.agendavet.dao;

import com.agendavet.model.Tutor;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TutorDAO {

    void salvar(Tutor tutor) throws SQLException;

    List<Tutor> listar() throws SQLException;

    Optional<Tutor> buscarPorId(Long id) throws SQLException;

    void atualizar(Tutor tutor) throws SQLException;

    void deletar(Long id) throws SQLException;
}
