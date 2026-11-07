package br.com.petadote;

import br.com.petadote.repositorio.AbrigoRepositorio;
import br.com.petadote.repositorio.AdotanteRepositorio;
import br.com.petadote.repositorio.AnimalRepositorio;
import br.com.petadote.repositorio.ProcessoAdocaoRepositorio;
import br.com.petadote.servico.AbrigoServicoImpl;
import br.com.petadote.servico.AdotanteServicoImpl;
import br.com.petadote.servico.AnimalServicoImpl;
import br.com.petadote.servico.IAbrigoServico;
import br.com.petadote.servico.IAdotanteServico;
import br.com.petadote.servico.IAnimalServico;
import br.com.petadote.servico.IProcessoAdocaoServico;
import br.com.petadote.servico.ProcessoAdocaoServicoImpl;

/**
 * Único lugar que cria repositórios e serviços. Existe uma só instância (singleton),
 * então todas as telas usam os mesmos repositórios e enxergam os mesmos dados.
 */
public final class Contexto {

    private static final Contexto INSTANCIA = new Contexto();

    public static Contexto get() {
        return INSTANCIA;
    }

    private final AbrigoRepositorio abrigoRepo = new AbrigoRepositorio();
    private final AnimalRepositorio animalRepo = new AnimalRepositorio();
    private final AdotanteRepositorio adotanteRepo = new AdotanteRepositorio();
    private final ProcessoAdocaoRepositorio processoRepo = new ProcessoAdocaoRepositorio();

    private final IAbrigoServico abrigoServico = new AbrigoServicoImpl(abrigoRepo);
    private final IAnimalServico animalServico = new AnimalServicoImpl(animalRepo);
    private final IAdotanteServico adotanteServico = new AdotanteServicoImpl(adotanteRepo);
    private final IProcessoAdocaoServico processoServico =
            new ProcessoAdocaoServicoImpl(processoRepo, animalRepo, adotanteRepo);

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
