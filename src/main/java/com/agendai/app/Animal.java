package com.agendai.app;
public class Animal {

    private int id;
    private String nome;
    private String especie;
    private String raca;
    private double peso;
    private int tutorId;
 
    public Animal() {}
 
    public Animal(String nome, String especie, String raca, double peso, int tutorId) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.peso = peso;
        this.tutorId = tutorId;
    }
 
    public Animal(int id, String nome, String especie, String raca, double peso, int tutorId) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.peso = peso;
        this.tutorId = tutorId;
    }
 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
 
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
 
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
 
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
 
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
 
    public int getTutorId() { return tutorId; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }
 
    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especie='" + especie + '\'' +
                ", raca='" + raca + '\'' +
                ", peso=" + peso +
                ", tutorId=" + tutorId +
                '}';
    }
}