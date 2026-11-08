package br.com.petadote.model;

import java.time.LocalDate;

public class Visita implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private LocalDate data;
    private String situacaoAnimal;
    private String observacoes;
    private boolean concluida;
    private StatusAgendamento statusAgendamento = StatusAgendamento.PENDENTE;
    private String observacaoAgendamento;

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

    /** O abrigo confirma a visita na data marcada. */
    public boolean confirmar(String observacao) {
        if (!podeAlterarAgendamento()) {
            return false;
        }
        statusAgendamento = StatusAgendamento.CONFIRMADA;
        observacaoAgendamento = observacao;
        return true;
    }

    /** O abrigo muda a data da visita. */
    public boolean reagendar(LocalDate novaData, String observacao) {
        if (!podeAlterarAgendamento() || novaData == null || novaData.isBefore(LocalDate.now())) {
            return false;
        }
        data = novaData;
        statusAgendamento = StatusAgendamento.REAGENDADA;
        observacaoAgendamento = observacao;
        return true;
    }

    /** O abrigo cancela a visita; ela deixa de contar para o REQ18. */
    public boolean cancelar(String observacao) {
        if (!podeAlterarAgendamento()) {
            return false;
        }
        statusAgendamento = StatusAgendamento.CANCELADA;
        observacaoAgendamento = observacao;
        return true;
    }

    /** Visita já realizada ou cancelada não muda mais de agendamento. */
    public boolean podeAlterarAgendamento() {
        return !concluida && statusAgendamento != StatusAgendamento.CANCELADA;
    }

    public boolean isCancelada() {
        return statusAgendamento == StatusAgendamento.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusAgendamento getStatusAgendamento() {
        return statusAgendamento;
    }

    public String getObservacaoAgendamento() {
        return observacaoAgendamento;
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