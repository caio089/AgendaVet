package com.agendai.dao;

import com.agendai.model.Tutor;

import java.util.List;

public interface TutorDAO {

    void salvar(Tutor tutor);

    List<Tutor> listar();

    Tutor buscarPorId(int id);

    void atualizar(Tutor tutor);

    void deletar(int id);
}
