package br.com.petadote.repositorio;

import br.com.petadote.model.Animal;

public class AnimalRepositorio extends RepositorioBase<Animal> {

    private static final AnimalRepositorio INSTANCIA = new AnimalRepositorio();

    private AnimalRepositorio() { }

    public static AnimalRepositorio getInstance() {
        return INSTANCIA;
    }
}
