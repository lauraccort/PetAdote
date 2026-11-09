package br.com.petadote.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * REQ01: abrigo/lar temporário com nome, endereço e capacidade de animais.
 * REQ19 (capacidade) e REQ20 (exclusão) são verificados nos métodos abaixo.
 */
public class Abrigo extends Entidade {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String endereco;
    private int capacidade;
    private String email;
    private String senha;
    private StatusAbrigo status;
    private LocalDate dataPedido;
    private List<Animal> animais;

    public Abrigo() {
        this.animais = new ArrayList<>();
        this.status = StatusAbrigo.PENDENTE; // novo cadastro aguarda aprovação do administrador
    }

    public Abrigo(String nome, String endereco, int capacidade) {
        this();
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidade;
    }

    /** Aprova o pedido de cadastro; só vale para pedidos pendentes. */
    public boolean aprovar() {
        if (status != StatusAbrigo.PENDENTE) {
            return false;
        }
        status = StatusAbrigo.APROVADO;
        return true;
    }

    /** Nega o pedido de cadastro; só vale para pedidos pendentes. */
    public boolean negar() {
        if (status != StatusAbrigo.PENDENTE) {
            return false;
        }
        status = StatusAbrigo.NEGADO;
        return true;
    }

    /**
     * REQ19: animais adotados continuam ligados ao abrigo (histórico e
     * acompanhamento pós-adoção), mas já saíram e não ocupam vaga.
     */
    public boolean temVaga() {
        return getQuantidadeAnimais() < capacidade;
    }

    /**
     * REQ19: valida a capacidade do abrigo antes de registrar
     * um novo animal. Também impede que um animal já vinculado
     * a outro abrigo seja duplicado.
     */
    public boolean adicionarAnimal(Animal animal) {
        if (!temVaga()) {
            return false;
        }
        if (animal.getAbrigo() != null && animal.getAbrigo() != this) {
            return false;
        }
        animais.add(animal);
        animal.setAbrigo(this);
        return true;
    }

    public boolean removerAnimal(Animal animal) {
        boolean removido = animais.remove(animal);
        if (removido) {
            animal.setAbrigo(null);
        }
        return removido;
    }

    /**
     * REQ20: abrigo só pode ser excluído se não possuir
     * animais vinculados (os já adotados não contam).
     */
    public boolean podeExcluir() {
        return getQuantidadeAnimais() == 0;
    }

    /** Animais que ainda estão no abrigo (todos, menos os adotados). */
    public int getQuantidadeAnimais() {
        return (int) animais.stream()
                .filter(a -> a.getStatus() != StatusAnimal.ADOTADO)
                .count();
    }

    public List<Animal> getAnimais() {
        return Collections.unmodifiableList(animais);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
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

    public StatusAbrigo getStatus() {
        return status;
    }

    public void setStatus(StatusAbrigo status) {
        this.status = status;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }
}
