package br.com.petadote.model;

/** Situação do agendamento de uma visita, decidida pelo abrigo. */
public enum StatusAgendamento {
    PENDENTE("Aguardando confirmação"),
    CONFIRMADA("Confirmada"),
    REAGENDADA("Reagendada"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusAgendamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
