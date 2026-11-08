package br.com.petadote;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.Animal;
import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.Cao;
import br.com.petadote.model.Gato;
import br.com.petadote.model.Parecer;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.repositorio.Persistencia;
import br.com.petadote.servico.AbrigoServicoImpl;
import br.com.petadote.servico.AdotanteServicoImpl;
import br.com.petadote.servico.AnimalServicoImpl;
import br.com.petadote.servico.IAbrigoServico;
import br.com.petadote.servico.IAdotanteServico;
import br.com.petadote.servico.IAnimalServico;
import br.com.petadote.servico.IProcessoAdocaoServico;
import br.com.petadote.servico.ProcessoAdocaoServicoImpl;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Único lugar que expõe os serviços. Todos são singletons (getInstance) e já
 * buscam o próprio repositório, então todas as telas enxergam os mesmos dados.
 *
 * Na primeira execução (sem dados/petadote.dat) semeia dados de exemplo e grava.
 * Nas seguintes, carrega o arquivo e continua de onde parou.
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

    // Quem fez login (no máximo um dos dois; administrador não tem cadastro)
    private Adotante adotanteLogado;
    private Abrigo abrigoLogado;

    private Contexto() {
        if (!Persistencia.carregar()) {
            Persistencia.marcarCarregando(true);
            try {
                semear();
            } finally {
                Persistencia.marcarCarregando(false);
            }
            Persistencia.salvar();
        }
    }

    /**
     * Dados de exemplo da primeira execução. Os processos nascem com data de
     * abertura antiga para o relatório de pendentes (REQ13) não ficar vazio.
     */
    private void semear() {
        Abrigo lar = new Abrigo("Lar dos Bichos", "Rua das Flores, 120 - Recife/PE", 20);
        abrigoServico.cadastrar(lar);

        Cao thor = new Cao("Thor", LocalDate.now().minusYears(3), "Labrador");
        Cao mel = new Cao("Mel", LocalDate.now().minusMonths(8), "SRD");
        Gato nina = new Gato("Nina", LocalDate.now().minusYears(2), "Siamês");
        Gato luna = new Gato("Luna", LocalDate.now().minusYears(1), "SRD");

        for (Animal a : new Animal[]{thor, mel, nina, luna}) {
            lar.adicionarAnimal(a);
            animalServico.cadastrar(a);
        }

        Adotante ana = new Adotante("Ana Clara", "12345678900", "Rua A, 10 - Recife/PE", 0);
        Adotante bruno = new Adotante("Bruno Lima", "98765432100", "Rua B, 20 - Olinda/PE", 1);
        Adotante carla = new Adotante("Carla Souza", "11122233344", "Rua C, 30 - Recife/PE", 0);
        adotanteServico.cadastrar(ana);
        adotanteServico.cadastrar(bruno);
        adotanteServico.cadastrar(carla);

        // Pendente há 30 dias — EM_AVALIACAO
        ProcessoAdocao p1 = processoServico.iniciar(thor.getId(), ana.getId());
        p1.setDataAbertura(LocalDate.now().minusDays(30));

        // Pendente há 20 dias — EM_AVALIACAO
        ProcessoAdocao p2 = processoServico.iniciar(mel.getId(), bruno.getId());
        p2.setDataAbertura(LocalDate.now().minusDays(20));

        // Pendente há 25 dias — APROVADO, aguardando conclusão
        ProcessoAdocao p3 = processoServico.iniciar(nina.getId(), carla.getId());
        p3.setDataAbertura(LocalDate.now().minusDays(25));
        processoServico.registrarAvaliacao(p3.getId(),
                new Avaliacao(LocalDate.now().minusDays(22), "Responsável do abrigo",
                        Parecer.POSITIVO, "Ambiente adequado."));
        processoServico.aprovar(p3.getId());
    }

    public void entrarComoAdotante(Adotante adotante) {
        adotanteLogado = adotante;
        abrigoLogado = null;
    }

    public void entrarComoAbrigo(Abrigo abrigo) {
        abrigoLogado = abrigo;
        adotanteLogado = null;
    }

    /** Usado no login do administrador e ao sair. */
    public void encerrarSessao() {
        adotanteLogado = null;
        abrigoLogado = null;
    }

    public Optional<Adotante> adotanteLogado() {
        return Optional.ofNullable(adotanteLogado);
    }

    public Optional<Abrigo> abrigoLogado() {
        return Optional.ofNullable(abrigoLogado);
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