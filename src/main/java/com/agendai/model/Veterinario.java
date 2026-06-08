package com.agendai.model;

/**
 * Entidade Veterinario — profissionais da clínica.
 */
public class Veterinario extends EntidadeBase {

    private String nome;
    private String crmv;
    private String especialidade;
    private String telefone;

    public Veterinario() {
    }

    public Veterinario(int id, String nome, String crmv, String especialidade, String telefone) {
        this.id = id;
        this.nome = nome;
        this.crmv = crmv;
        this.especialidade = especialidade;
        this.telefone = telefone;
    }

    public Veterinario(String nome, String crmv, String especialidade, String telefone) {
        this.nome = nome;
        this.crmv = crmv;
        this.especialidade = especialidade;
        this.telefone = telefone;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCrmv() { return crmv; }
    public void setCrmv(String crmv) { this.crmv = crmv; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String resumo() {
        return "Veterinario: " + nome + " - " + especialidade;
    }

    @Override
    public String toString() {
        return "Veterinario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", crmv='" + crmv + '\'' +
                ", especialidade='" + especialidade + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
