package br.com.petadote.servico;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.Animal;
import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Padrão Fachada (também singleton): a interface gráfica só conversa com esta classe.
 * Fluxo: gui -> PetAdoteFachada -> serviço -> repositório.
 */
public class PetAdoteFachada {
    private static final PetAdoteFachada INSTANCIA = new PetAdoteFachada();

    private final IAbrigoServico abrigoServico = AbrigoServicoImpl.getInstance();
    private final IAnimalServico animalServico = AnimalServicoImpl.getInstance();
    private final IAdotanteServico adotanteServico = AdotanteServicoImpl.getInstance();
    private final IProcessoAdocaoServico processoServico = ProcessoAdocaoServicoImpl.getInstance();

    private PetAdoteFachada() {
    }

    public static PetAdoteFachada getInstance() {
        return INSTANCIA;
    }

    // ---------- Abrigo ----------
    public Abrigo cadastrarAbrigo(String nome, String endereco, int capacidade) {
        return abrigoServico.cadastrar(new Abrigo(nome, endereco, capacidade));
    }

    public Optional<Abrigo> buscarAbrigo(Long id) {
        return abrigoServico.buscarPorId(id);
    }

    public List<Abrigo> listarAbrigos() {
        return abrigoServico.listarTodos();
    }

    public Abrigo atualizarAbrigo(Long id, String nome, String endereco, int capacidade) {
        return abrigoServico.atualizar(id, nome, endereco, capacidade);
    }

    public boolean adicionarAnimalAoAbrigo(Long idAbrigo, Animal animal) {
        return abrigoServico.adicionarAnimal(idAbrigo, animal);
    }

    public boolean excluirAbrigo(Long id) {
        return abrigoServico.excluir(id);
    }

    // ---------- Animal ----------
    public Animal cadastrarAnimal(Animal animal) {
        return animalServico.cadastrar(animal);
    }

    public Optional<Animal> buscarAnimal(Long id) {
        return animalServico.buscarPorId(id);
    }

    public List<Animal> listarAnimais() {
        return animalServico.listarTodos();
    }

    public List<Animal> listarAnimaisDisponiveis() {
        return animalServico.listarDisponiveis();
    }

    public Animal atualizarAnimal(Long id, String nome, LocalDate dataNascimento) {
        return animalServico.atualizar(id, nome, dataNascimento);
    }

    public boolean excluirAnimal(Long id) {
        return animalServico.excluir(id);
    }

    // ---------- Adotante ----------
    public Adotante cadastrarAdotante(Adotante adotante) {
        return adotanteServico.cadastrar(adotante);
    }

    public Optional<Adotante> buscarAdotante(Long id) {
        return adotanteServico.buscarPorId(id);
    }

    public List<Adotante> listarAdotantes() {
        return adotanteServico.listarTodos();
    }

    public Adotante atualizarAdotante(Long id, String nome, String endereco) {
        return adotanteServico.atualizar(id, nome, endereco);
    }

    public boolean excluirAdotante(Long id) {
        return adotanteServico.excluir(id);
    }

    // ---------- Processo de adoção ----------
    public ProcessoAdocao iniciarProcesso(Long idAnimal, Long idAdotante) {
        return processoServico.iniciar(idAnimal, idAdotante);
    }

    public Optional<ProcessoAdocao> buscarProcesso(Long id) {
        return processoServico.buscarPorId(id);
    }

    public List<ProcessoAdocao> listarProcessos() {
        return processoServico.listarTodos();
    }

    public List<ProcessoAdocao> listarProcessosDoAdotante(Long idAdotante) {
        return processoServico.listarPorAdotante(idAdotante);
    }

    public List<ProcessoAdocao> listarProcessosConcluidos() {
        return processoServico.listarConcluidos();
    }

    public List<ProcessoAdocao> listarProcessosPendentes() {
        return processoServico.listarPendentesHa15Dias();
    }

    public void registrarAvaliacao(Long idProcesso, Avaliacao avaliacao) {
        processoServico.registrarAvaliacao(idProcesso, avaliacao);
    }

    public void agendarVisita(Long idProcesso, Visita visita) {
        processoServico.agendarVisita(idProcesso, visita);
    }

    public boolean aprovarProcesso(Long idProcesso) {
        return processoServico.aprovar(idProcesso);
    }

    public boolean concluirProcesso(Long idProcesso) {
        return processoServico.concluir(idProcesso);
    }
}
