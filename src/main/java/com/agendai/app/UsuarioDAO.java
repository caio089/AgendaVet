package com.agendai.app;

import java.util.List;

public interface UsuarioDAO {

    void salvar(Usuario usuario);

    List<Usuario> listar();

    Usuario buscarPorId(int id);

    void atualizar(Usuario usuario);

    void deletar(int id);
}
