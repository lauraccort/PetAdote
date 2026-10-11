package br.com.petadote.model;

import java.time.LocalDate;

public class OutraEspecie extends Animal {
    private String especie;

    public OutraEspecie() {
    }

    public OutraEspecie(String nome, LocalDate dataNascimento, String especie) {
        super(nome, dataNascimento);
        this.especie = especie;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }
}
