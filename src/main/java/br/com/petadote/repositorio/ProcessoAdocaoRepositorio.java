package br.com.petadote.repositorio;

import br.com.petadote.model.ProcessoAdocao;

public class ProcessoAdocaoRepositorio extends RepositorioBase<ProcessoAdocao> {

    private static final ProcessoAdocaoRepositorio INSTANCIA = new ProcessoAdocaoRepositorio();

    private ProcessoAdocaoRepositorio() {
    }

    public static ProcessoAdocaoRepositorio getInstance() {
        return INSTANCIA;
    }
}