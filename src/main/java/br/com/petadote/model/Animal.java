
package br.com.petadote.model;

import java.time.LocalDate;

/**
 * REQ02: superclasse da herança Cão / Gato / Outra espécie.
 * Cada animal fica vinculado a um abrigo (atributo abrigo).
 */
public abstract class Animal extends Entidade {
    private static final long serialVersionUID = 1L;

    private String nome;
    private LocalDate dataNascimento;
    private StatusAnimal status;
    private Abrigo abrigo;

    public Animal() {
        this.status = StatusAnimal.DISPONIVEL;
    }

    public Animal(String nome, LocalDate dataNascimento) {
        this();
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    /**
     * REQ14: só pode iniciar processo se estiver DISPONIVEL.
     */
    public boolean podeIniciarProcesso() {
        return status == StatusAnimal.DISPONIVEL;
    }

    /**
     * REQ17: bloqueia conclusão de adoção de animal
     * em tratamento veterinário.
     */
    public boolean podeConcluirAdocao() {
        return status != StatusAnimal.EM_TRATAMENTO_VETERINARIO;
    }

    /**
     * Envia animais disponíveis ou em avaliação
     * para tratamento veterinário.
     */
    public boolean enviarParaTratamento() {
        if (status != StatusAnimal.DISPONIVEL
                && status != StatusAnimal.EM_AVALIACAO) {
            return false;
        }

        status = StatusAnimal.EM_TRATAMENTO_VETERINARIO;
        return true;
    }

    /**
     * Após a alta, o animal volta a ficar disponível
     * ou em avaliação, caso tenha processo ativo.
     */
    public boolean receberAlta(boolean temProcessoAtivo) {
        if (status != StatusAnimal.EM_TRATAMENTO_VETERINARIO) {
            return false;
        }

        status = temProcessoAtivo
                ? StatusAnimal.EM_AVALIACAO
                : StatusAnimal.DISPONIVEL;

        return true;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public StatusAnimal getStatus() {
        return status;
    }

    public void setStatus(StatusAnimal status) {
        this.status = status;
    }

    public Abrigo getAbrigo() {
        return abrigo;
    }

    public void setAbrigo(Abrigo abrigo) {
        this.abrigo = abrigo;
    }
}

