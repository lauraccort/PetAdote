package br.com.petadote.model;

import java.time.LocalDate;

public class Avaliacao {
    private LocalDate data;
    private String responsavel;
    private Parecer parecer;
    private String observacao;

    public Avaliacao() {
    }

    public Avaliacao(LocalDate data, String responsavel, Parecer parecer, String observacao) {
        this.data = data;
        this.responsavel = responsavel;
        this.parecer = parecer;
        this.observacao = observacao;
    }

    /** REQ15: parecer favorável = POSITIVO. */
    public boolean isFavoravel() {
        return parecer == Parecer.POSITIVO;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Parecer getParecer() {
        return parecer;
    }

    public void setParecer(Parecer parecer) {
        this.parecer = parecer;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}