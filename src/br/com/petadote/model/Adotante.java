package br.com.petadote.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Adotante extends Entidade {
    private String nome;
    private String cpf;
    private String endereco;
    private int quantAnimal;
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
     * REQ16: limita a 3 os processos ATIVOS simultâneos
     * (Em Avaliação + Aprovado).
     */
    public boolean podeIniciarNovoProcesso() {
        long ativos = processos.stream()
                .filter(p -> p.getStatus() == StatusProcesso.EM_AVALIACAO
                        || p.getStatus() == StatusProcesso.APROVADO)
                .count();
        return ativos < 3;
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
}
