package com.agendai.app;

import java.sql.SQLException;
import java.util.List;

import com.agendai.database.DatabaseConnection;

public class Main {

    public static void main(String[] args) throws SQLException {

        // Inicializa o banco e cria a tabela se não existir
        DatabaseConnection.inicializarBancoDeDados();

        Veterinariodao dao = new VeterinarioDAOImpl();

        System.out.println("\n===== SALVAR =====");
        Veterinario v1 = new Veterinario("Dra. Ana Souza",   "CRMV-SP-12345", "Clínica Geral",  "(11) 91234-5678");
        Veterinario v2 = new Veterinario("Dr. Carlos Lima",  "CRMV-RJ-67890", "Ortopedia",      "(21) 98765-4321");
        Veterinario v3 = new Veterinario("Dra. Beatriz Melo","CRMV-MG-11223", "Dermatologia",   "(31) 99876-5432");

        dao.salvar(v1);
        dao.salvar(v2);
        dao.salvar(v3);

        System.out.println("\n===== LISTAR TODOS =====");
        List<Veterinario> lista = dao.listar();
        lista.forEach(System.out::println);

        System.out.println("\n===== BUSCAR POR ID =====");
        Veterinario encontrado = dao.buscarPorId(v1.getId());
        System.out.println("Encontrado: " + encontrado);

        System.out.println("\n===== ATUALIZAR =====");
        v2.setTelefone("(21) 11111-2222");
        v2.setEspecialidade("Neurologia");
        dao.atualizar(v2);
        System.out.println("Após atualização: " + dao.buscarPorId(v2.getId()));

        System.out.println("\n===== DELETAR =====");
        dao.deletar(v3.getId());

        System.out.println("\n===== LISTAR APÓS DELETAR =====");
        dao.listar().forEach(System.out::println);

        // Encerra a conexão
        DatabaseConnection.fecharConexao();
    }
}