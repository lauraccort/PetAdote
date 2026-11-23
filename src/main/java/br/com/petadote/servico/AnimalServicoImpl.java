package br.com.petadote.servico;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Animal;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.model.StatusAnimal;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.repositorio.IRepositorio;
import br.com.petadote.repositorio.AnimalRepositorio;
import br.com.petadote.repositorio.AbrigoRepositorio;
import br.com.petadote.repositorio.ProcessoAdocaoRepositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnimalServicoImpl implements IAnimalServico {

    private static final AnimalServicoImpl INSTANCIA = new AnimalServicoImpl();

    private final IRepositorio<Animal> repositorio =
            AnimalRepositorio.getInstance();

    private AnimalServicoImpl() {
    }

    public static AnimalServicoImpl getInstance() {
        return INSTANCIA;
    }

    // REQ02 e REQ19: grava o cadastro e o vínculo no mesmo arquivo.
    @Override
    public synchronized Animal cadastrar(Long idAbrigo, Animal animal) {
        if (animal.getNome() == null || animal.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do animal é obrigatório.");
        }

        if (animal.getDataNascimento() != null
                && animal.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Data de nascimento não pode ser futura.");
        }

        if (animal.getId() != null || animal.getAbrigo() != null) {
            throw new IllegalArgumentException("O animal já foi cadastrado ou vinculado a um abrigo.");
        }

        Abrigo abrigo = AbrigoRepositorio.getInstance().buscarPorId(idAbrigo)
                .orElseThrow(() -> new IllegalArgumentException("Abrigo não encontrado."));
        if (abrigo.getStatus() != StatusAbrigo.APROVADO) {
            throw new IllegalArgumentException("O cadastro exige um abrigo aprovado.");
        }
        if (!abrigo.adicionarAnimal(animal)) {
            throw new IllegalArgumentException("O abrigo atingiu sua capacidade.");
        }

        try {
            return repositorio.salvar(animal);
        } catch (RuntimeException e) {
            abrigo.removerAnimal(animal);
            throw e;
        }
    }

    @Override
    public Optional<Animal> buscarPorId(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Animal> listarTodos() {
        return repositorio.listarTodos();
    }

    // REQ11: base do relatório de animais disponíveis.
    @Override
    public List<Animal> listarDisponiveis() {
        return repositorio.listarTodos().stream()
                .filter(Animal::podeIniciarProcesso)
                .collect(Collectors.toList());
    }

    @Override
    public Animal atualizar(Long id, String nome, LocalDate dataNascimento) {
        Animal animal = repositorio.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Animal não encontrado."));

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do animal.");
        }
        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Informe uma data de nascimento válida.");
        }

        animal.setNome(nome);
        animal.setDataNascimento(dataNascimento);

        return repositorio.salvar(animal);
    }

    @Override
    public boolean excluir(Long id) {
        Animal animal = repositorio.buscarPorId(id).orElse(null);
        if (animal == null || animal.getStatus() != StatusAnimal.DISPONIVEL) {
            return false;
        }

        boolean temHistorico = ProcessoAdocaoRepositorio.getInstance().listarTodos().stream()
                .anyMatch(processo -> processo.getAnimal() != null
                        && id.equals(processo.getAnimal().getId()));
        if (temHistorico) {
            return false;
        }

        Abrigo abrigo = animal.getAbrigo();
        if (abrigo != null && !abrigo.removerAnimal(animal)) {
            return false;
        }

        return repositorio.excluirPorId(id);
    }

    /**
     * REQ17: envia um animal disponível ou em avaliação
     * para tratamento veterinário.
     */
    @Override
    public boolean enviarParaTratamento(Long id) {
        Optional<Animal> resultado = repositorio.buscarPorId(id);

        if (resultado.isEmpty()) {
            return false;
        }

        Animal animal = resultado.get();

        if (!animal.enviarParaTratamento()) {
            return false;
        }

        repositorio.salvar(animal);
        return true;
    }

    /**
     * REQ17: registra a alta do tratamento veterinário.
     * Se houver processo ativo, o animal retorna para avaliação.
     * Caso contrário, retorna para disponível.
     */
    @Override
    public boolean receberAlta(Long id) {
        Optional<Animal> resultado = repositorio.buscarPorId(id);

        if (resultado.isEmpty()) {
            return false;
        }

        Animal animal = resultado.get();

        boolean temProcessoAtivo =
                ProcessoAdocaoRepositorio.getInstance()
                        .listarTodos()
                        .stream()
                        .anyMatch(processo ->
                                processo.getAnimal() != null
                                        && id.equals(processo.getAnimal().getId())
                                        && (processo.getStatus() == StatusProcesso.EM_AVALIACAO
                                        || processo.getStatus() == StatusProcesso.APROVADO)
                        );

        if (!animal.receberAlta(temProcessoAtivo)) {
            return false;
        }

        repositorio.salvar(animal);
        return true;
    }
}
