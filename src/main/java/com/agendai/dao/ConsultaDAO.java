package com.agendai.dao;

import com.agendai.model.Consulta;

import java.util.List;

public interface ConsultaDAO {

    void salvar(Consulta consulta);

    List<Consulta> listar();

    Consulta buscarPorId(int id);

    void atualizar(Consulta consulta);

    void deletar(int id);
}
