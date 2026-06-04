package com.agendai.app;

<<<<<<< HEAD
import java.sql.SQLException;
import java.util.List;

import com.agendai.database.DatabaseConnection;
=======
import com.agendai.database.DatabaseConnection;
import com.agendai.database.DatabaseInitializer;
>>>>>>> d410e11172a544856c3259d13da5458a70682c9c

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public static void main(String[] args) {
        DatabaseInitializer.init();

        int tutorId = garantirTutorExemplo();

        AnimalDAO dao = new AnimalDAOImpl();

        Animal rex = new Animal("Rex", "Cão", "Labrador", 28.5, tutorId);
        dao.salvar(rex);

        System.out.println("Todos: " + dao.listar());
        System.out.println("Por id: " + dao.buscarPorId(rex.getId()));
    }

    private static int garantirTutorExemplo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement count = conn.prepareStatement("SELECT COUNT(*) FROM tutor");
                 ResultSet rs = count.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    try (PreparedStatement first = conn.prepareStatement("SELECT id FROM tutor LIMIT 1");
                         ResultSet idRs = first.executeQuery()) {
                        if (idRs.next()) {
                            return idRs.getInt(1);
                        }
                    }
                }
            }

            String sql = "INSERT INTO tutor (nome, cpf, telefone, endereco) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Maria Silva");
                ps.setString(2, "00000000000");
                ps.setString(3, "11999999999");
                ps.setString(4, "Rua Exemplo, 100");
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] garantirTutorExemplo: " + e.getMessage());
        }

        return 1;
    }
}
