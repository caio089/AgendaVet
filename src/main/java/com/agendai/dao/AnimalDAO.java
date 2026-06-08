package com.agendai.dao;

import com.agendai.model.Animal;

import java.util.List;

public interface AnimalDAO {

    void salvar(Animal animal);

    List<Animal> listar();

    Animal buscarPorId(int id);

    void atualizar(Animal animal);

    void deletar(int id);
}
