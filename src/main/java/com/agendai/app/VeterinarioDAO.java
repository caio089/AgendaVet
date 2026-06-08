package com.agendai.app;

import java.util.List;

public interface VeterinarioDAO {

    /**
     * Salva um novo veterinário no banco de dados.
     *
     * @param veterinario objeto a ser salvo
     * @return true se a operação foi bem-sucedida, false caso contrário
     */
    boolean salvar(Veterinario veterinario);

    /**
     * Retorna todos os veterinários cadastrados.
     *
     * @return lista de veterinários
     */
    List<Veterinario> listar();

    /**
     * Busca um veterinário pelo seu ID.
     *
     * @param id identificador do veterinário
     * @return o veterinário encontrado ou null se não existir
     */
    Veterinario buscarPorId(int id);

    /**
     * Atualiza os dados de um veterinário existente.
     *
     * @param veterinario objeto com os dados atualizados (deve conter o ID)
     * @return true se a operação foi bem-sucedida, false caso contrário
     */
    boolean atualizar(Veterinario veterinario);

    /**
     * Remove um veterinário do banco de dados pelo seu ID.
     *
     * @param id identificador do veterinário a ser removido
     * @return true se a operação foi bem-sucedida, false caso contrário
     */
    boolean deletar(int id);
}