package br.com.petadote.model;

public enum StatusProcesso {
    EM_AVALIACAO("Em avaliação"),
    APROVADO("Aprovado"),
    NEGADO("Negado"),
    CONCLUIDO("Concluído");

    private final String descricao;

    StatusProcesso(String descricao) {
        this.descricao = descricao;
    }

    /** Nome para mostrar nas telas. */
    public String getDescricao() {
        return descricao;
    }
}
