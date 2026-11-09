package br.com.petadote.model;

import java.time.LocalDate;

/** REQ02: subclasse "Outro" de Animal, com atributo próprio (nome da espécie). */
public class OutraEspecie extends Animal {
    private static final long serialVersionUID = 1L;

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
