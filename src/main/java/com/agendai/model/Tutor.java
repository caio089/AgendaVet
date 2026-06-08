package com.agendai.model;

/**
 * Entidade Tutor — responsável legal pelos animais cadastrados na clínica.
 */
public class Tutor extends EntidadeBase {

    private String nome;
    private String cpf;
    private String telefone;
    private String endereco;

    public Tutor() {
    }

    public Tutor(String nome, String cpf, String telefone, String endereco) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public Tutor(int id, String nome, String cpf, String telefone, String endereco) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String resumo() {
        return "Tutor: " + nome + " (CPF " + cpf + ")";
    }

    @Override
    public String toString() {
        return "Tutor{id=" + id + ", nome='" + nome + "', cpf='" + cpf + "'}";
    }
}
