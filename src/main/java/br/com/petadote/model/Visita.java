package br.com.petadote.model;

import java.time.LocalDate;

public class Visita {
    private LocalDate data;
    private String situacaoAnimal;
    private String observacoes;
    private boolean concluida;

    public Visita() {
    }

    public Visita(LocalDate data) {
        this.data = data;
        this.concluida = false;
    }

    /** REQ10: registra o resultado da visita pós-adoção. */
    public void registrarResultado(String situacao, String observacoes) {
        this.situacaoAnimal = situacao;
        this.observacoes = observacoes;
        this.concluida = true;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getSituacaoAnimal() {
        return situacaoAnimal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public boolean isConcluida() {
        return concluida;
    }
}