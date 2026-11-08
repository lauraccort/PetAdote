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
        // carregar() devolve false quando não existe dados/petadote.dat (primeira execução)
        if (!Persistencia.carregar()) {
            // Enquanto semeia, os repositórios não gravam; o arquivo é escrito uma vez no fim.
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
     *
     * Contas de teste (administrador: admin@petadote.com / admin123):
     *   abrigo   -> lar@petadote.com   / abrigo123
     *   adotante -> ana@petadote.com   / 123456
     *              bruno@petadote.com  / 123456
     *              carla@petadote.com  / 123456
     */
    private void semear() {
        // O serviço exige e-mail/senha válidos e sempre grava o abrigo como PENDENTE;
        // por isso o abrigo de exemplo é aprovado logo em seguida, para já poder entrar.
        Abrigo lar = new Abrigo("Lar dos Bichos", "Rua das Flores, 120 - Recife/PE", 20);
        lar.setEmail("lar@petadote.com");
        lar.setSenha("abrigo123");
        abrigoServico.cadastrar(lar);
        abrigoServico.aprovar(lar.getId());

        Cao thor = new Cao("Thor", LocalDate.now().minusYears(3), "Labrador");
        Cao mel = new Cao("Mel", LocalDate.now().minusMonths(8), "SRD");
        Gato nina = new Gato("Nina", LocalDate.now().minusYears(2), "Siamês");
        Gato luna = new Gato("Luna", LocalDate.now().minusYears(1), "SRD");

        for (Animal a : new Animal[]{thor, mel, nina, luna}) {
            animalServico.cadastrar(a);
            abrigoServico.adicionarAnimal(lar.getId(), a);
        }

        Adotante ana = novoAdotante("Ana Clara", "12345678900", "Rua A, 10 - Recife/PE", 0, "ana@petadote.com");
        Adotante bruno = novoAdotante("Bruno Lima", "98765432100", "Rua B, 20 - Olinda/PE", 1, "bruno@petadote.com");
        Adotante carla = novoAdotante("Carla Souza", "11122233344", "Rua C, 30 - Recife/PE", 0, "carla@petadote.com");

        // Pendente há 30 dias — EM_AVALIACAO
        ProcessoAdocao p1 = processoServico.iniciar(thor.getId(), ana.getId());
        p1.setDataAbertura(LocalDate.now().minusDays(30));

        // Pendente há 20 dias — EM_AVALIACAO
        ProcessoAdocao p2 = processoServico.iniciar(mel.getId(), bruno.getId());
        p2.setDataAbertura(LocalDate.now().minusDays(20));

        // Aberto há 25 dias — APROVADO, aguardando conclusão
        ProcessoAdocao p3 = processoServico.iniciar(nina.getId(), carla.getId());
        p3.setDataAbertura(LocalDate.now().minusDays(25));
        processoServico.registrarAvaliacao(p3.getId(),
                new Avaliacao(LocalDate.now().minusDays(22), "Responsável do abrigo",
                        Parecer.POSITIVO, "Ambiente adequado."));
        processoServico.aprovar(p3.getId());
    }

    private Adotante novoAdotante(String nome, String cpf, String endereco, int quantAnimal, String email) {
        Adotante adotante = new Adotante(nome, cpf, endereco, quantAnimal);
        adotante.setEmail(email);
        adotante.setSenha("123456");
        return adotanteServico.cadastrar(adotante);
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