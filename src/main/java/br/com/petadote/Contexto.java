package br.com.petadote;

import br.com.petadote.servico.AbrigoServicoImpl;
import br.com.petadote.servico.AdotanteServicoImpl;
import br.com.petadote.servico.AnimalServicoImpl;
import br.com.petadote.servico.IAbrigoServico;
import br.com.petadote.servico.IAdotanteServico;
import br.com.petadote.servico.IAnimalServico;
import br.com.petadote.servico.IProcessoAdocaoServico;
import br.com.petadote.servico.ProcessoAdocaoServicoImpl;

/**
 * Ponto de acesso aos serviços. Cada serviço é singleton (getInstance) e já busca
 * o próprio repositório singleton, então todas as telas enxergam os mesmos dados.
 */
public final class Contexto {

    private static final Contexto INSTANCIA = new Contexto();

    public static Contexto get() {
        return INSTANCIA;
    }

    private final IAbrigoServico abrigoServico = AbrigoServicoImpl.getInstance();
    private final IAnimalServico animalServico = AnimalServicoImpl.getInstance();
    private final IAdotanteServico adotanteServico = AdotanteServicoImpl.getInstance();
    private final IProcessoAdocaoServico processoServico = ProcessoAdocaoServicoImpl.getInstance();

    private Contexto() {
    }

    public IAbrigoServico abrigoServico() {
        return abrigoServico;
    }

    public IAnimalServico animalServico() {
        return animalServico;
    }

    public IAdotanteServico adotanteServico() {
        return adotanteServico;
    }

    public IProcessoAdocaoServico processoServico() {
        return processoServico;
    }
}

