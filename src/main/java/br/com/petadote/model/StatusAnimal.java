package br.com.petadote.model;

/**
 * EM_AVALIACAO e ADOTADO bloqueiam novo processo (REQ14);
 * ADOTADO é atribuído ao concluir (REQ08);
 * EM_TRATAMENTO_VETERINARIO bloqueia a conclusão (REQ17).
 */
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
