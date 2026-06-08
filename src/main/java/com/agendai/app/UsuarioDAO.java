package com.agendai.app;

import java.util.List;

public interface UsuarioDAO {

    void salvar(Usuario usuario);

    List<Usuario> listar();

    Usuario buscarPorId(int id);

    Usuario autenticar(String email, String senha);

    void atualizar(Usuario usuario);

    void deletar(int id);
}
