package br.com.petadote.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Abrigo extends Entidade {
    private String nome;
    private String endereco;
    private int capacidade;
    private List<Animal> animais;

    public Abrigo() {
        this.animais = new ArrayList<>();
    }

    public Abrigo(String nome, String endereco, int capacidade) {
        this();
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidade;
    }

    public boolean temVaga() {
        return animais.size() < capacidade;
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
     * animais vinculados.
     */
    public boolean podeExcluir() {
        return animais.isEmpty();
    }

    public int getQuantidadeAnimais() {
        return animais.size();
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

    @Override
    public String toString() {
        return nome;
    }
}
