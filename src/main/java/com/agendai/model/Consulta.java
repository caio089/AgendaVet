package com.agendai.model;

/**
 * Entidade Consulta — agendamento que relaciona um animal a um veterinário.
 */
public class Consulta extends EntidadeBase {

    private int animalId;
    private int veterinarioId;
    private String dataConsulta;
    private String status;

    public Consulta() {
    }

    public Consulta(int animalId, int veterinarioId, String dataConsulta, String status) {
        this.animalId = animalId;
        this.veterinarioId = veterinarioId;
        this.dataConsulta = dataConsulta;
        this.status = status;
    }

    public Consulta(int id, int animalId, int veterinarioId, String dataConsulta, String status) {
        this.id = id;
        this.animalId = animalId;
        this.veterinarioId = veterinarioId;
        this.dataConsulta = dataConsulta;
        this.status = status;
    }

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public int getVeterinarioId() {
        return veterinarioId;
    }

    public void setVeterinarioId(int veterinarioId) {
        this.veterinarioId = veterinarioId;
    }

    public String getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String resumo() {
        return "Consulta #" + id + " - status: " + status;
    }

    @Override
    public String toString() {
        return "Consulta{id=" + id + ", animalId=" + animalId + ", status='" + status + "'}";
    }
}
