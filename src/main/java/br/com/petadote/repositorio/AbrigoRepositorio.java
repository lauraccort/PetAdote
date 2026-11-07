package br.com.petadote.repositorio;

import br.com.petadote.model.Abrigo;

public class AbrigoRepositorio extends RepositorioBase<Abrigo> {

    private static final AbrigoRepositorio INSTANCIA = new AbrigoRepositorio();

    private AbrigoRepositorio() {
    }

    public static AbrigoRepositorio getInstance() {
        return INSTANCIA;
    }
}
