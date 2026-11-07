package br.com.petadote.model;

public enum StatusAnimal {
    DISPONIVEL("Disponível"),
    EM_TRATAMENTO_VETERINARIO("Em tratamento veterinário"),
    EM_AVALIACAO("Em avaliação"),
    ADOTADO("Adotado");

    private final String descricao;

    StatusAnimal(String descricao) {
        this.descricao = descricao;
    }

    /** Nome para mostrar nas telas. */
    public String getDescricao() {
        return descricao;
    }
}
