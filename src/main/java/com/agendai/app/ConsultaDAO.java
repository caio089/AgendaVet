package com.agendai.app;

import java.util.List;

public interface ConsultaDAO {

    void salvar(Consulta consulta);

    List<Consulta> listar();

    Consulta buscarPorId(int id);

    void atualizar(Consulta consulta);

    void deletar(int id);
}
