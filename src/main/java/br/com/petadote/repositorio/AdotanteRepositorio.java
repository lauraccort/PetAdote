package br.com.petadote.repositorio;

import br.com.petadote.model.Adotante;

public class AdotanteRepositorio extends RepositorioBase<Adotante> {

    private static final AdotanteRepositorio INSTANCIA = new AdotanteRepositorio();

    private AdotanteRepositorio() {
    }

    public static AdotanteRepositorio getInstance() {
        return INSTANCIA;
    }
}
