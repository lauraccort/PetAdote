package br.com.petadote.model;

/**
 * EM_AVALIACAO é o status inicial (REQ06) e o filtro do REQ13;
 * EM_AVALIACAO + APROVADO contam como ativos no REQ16; CONCLUIDO vem do REQ08.
 */
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
