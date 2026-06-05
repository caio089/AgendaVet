package com.agendai.app;

public interface UsuarioDAO {

    Usuario buscarPorEmail(String email);

    Usuario autenticar(String email, String senhaPlana);
}
