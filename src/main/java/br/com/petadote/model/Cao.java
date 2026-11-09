package br.com.petadote.model;

import java.time.LocalDate;

/**
 * REQ02: subclasse de Animal com atributo próprio (raça).
 * REQ03: infoRaca guarda porte, peso e temperamento vindos da TheDogAPI.
 */
public class Cao extends Animal {
    private static final long serialVersionUID = 1L;

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