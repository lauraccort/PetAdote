package br.com.petadote.model;

import java.time.LocalDate;

public class Cao extends Animal {
    private String raca;
    private InfoRaca infoRaca;

    public Cao() {
    }

    public Cao(String nome, LocalDate dataNascimento, String raca) {
        super(nome, dataNascimento);
        this.raca = raca;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public InfoRaca getInfoRaca() {
        return infoRaca;
    }

    public void setInfoRaca(InfoRaca infoRaca) {
        this.infoRaca = infoRaca;
    }
}