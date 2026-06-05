package com.agendai.app;

/**
 * Classe base abstrata para as entidades do domínio AgendaVet.
 * Centraliza o atributo "id" (e seus getter/setter) e define um método
 * abstrato "resumo()" que cada entidade implementa do seu jeito.
 *
 * Demonstra: classe abstrata, método abstrato, herança e reuso de código.
 */
public abstract class EntidadeBase {

    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** Retorna uma descrição curta e legível da entidade. */
    public abstract String resumo();
}