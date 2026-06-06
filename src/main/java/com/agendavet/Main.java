package com.agendavet;

import com.agendavet.dao.TutorDAO;
import com.agendavet.dao.TutorDAOImpl;
import com.agendavet.model.Tutor;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        TutorDAO tutorDAO = new TutorDAOImpl();

        try {
            Tutor tutor = new Tutor(
                    "Maria Silva",
                    "123.456.789-00",
                    "(11) 98765-4321",
                    "Rua das Flores, 100"
            );

            tutorDAO.salvar(tutor);
            System.out.println("Salvo: " + tutor);

            System.out.println("Listagem:");
            tutorDAO.listar().forEach(System.out::println);

            tutorDAO.buscarPorId(tutor.getId())
                    .ifPresent(t -> System.out.println("Busca por ID: " + t));

            tutor.setTelefone("(11) 91234-5678");
            tutorDAO.atualizar(tutor);
            System.out.println("Atualizado: " + tutorDAO.buscarPorId(tutor.getId()).orElseThrow());

            tutorDAO.deletar(tutor.getId());
            System.out.println("Registros após exclusão: " + tutorDAO.listar().size());

        } catch (SQLException e) {
            System.err.println("Erro de banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
