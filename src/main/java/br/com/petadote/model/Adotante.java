package br.com.petadote.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * REQ04: adotante com nome, CPF, endereço e quantidade de animais que já possui.
 * Também guarda o histórico de processos (REQ05) e o limite de ativos (REQ16).
 */
public class Adotante extends Entidade {
    private static final long serialVersionUID = 1L;

    private static final int LIMITE_PROCESSOS_ATIVOS = 3;

    private String nome;
    private String cpf;
    private String endereco;
    private int quantAnimal;
    private String email;
    private String senha;
    private List<ProcessoAdocao> processos;

    public Adotante() {
        this.processos = new ArrayList<>();
    }

    public Adotante(String nome, String cpf, String endereco, int quantAnimal) {
        this();
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.quantAnimal = quantAnimal;
    }

    /** Método do diagrama: total de processos já concluídos. */
    public long contarProcessosConcluidos() {
        return processos.stream()
                .filter(p -> p.getStatus() == StatusProcesso.CONCLUIDO)
                .count();
    }

    /**
     * REQ16: limita a 3 os processos ativos do adotante, isto é, em avaliação,
     * aprovados ou concluídos. Só os negados deixam de contar.
     */
    public boolean podeIniciarNovoProcesso() {
        long ativos = processos.stream()
                .filter(p -> p.getStatus() != StatusProcesso.NEGADO)
                .count();
        return ativos < LIMITE_PROCESSOS_ATIVOS;
    }

    public void adicionarProcesso(ProcessoAdocao processo) {
        this.processos.add(processo);
    }

    /** REQ05: histórico de processos de adoção do adotante. */
    public List<ProcessoAdocao> getHistoricoProcessos() {
        return Collections.unmodifiableList(processos);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getQuantAnimal() {
        return quantAnimal;
    }

    public void setQuantAnimal(int quantAnimal) {
        this.quantAnimal = quantAnimal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
