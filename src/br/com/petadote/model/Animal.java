package br.com.petadote.model;

import java.time.LocalDate;

public abstract class Animal extends Entidade {
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
     * O status EM_AVALIACAO já impede novo processo concorrente
     * e ADOTADO impede reabertura.
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
