
package br.com.petadote.servico;

import br.com.petadote.model.Animal;
import br.com.petadote.model.Abrigo;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.repositorio.IRepositorio;
import br.com.petadote.repositorio.AbrigoRepositorio;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AbrigoServicoImpl implements IAbrigoServico {

    private static final AbrigoServicoImpl INSTANCIA = new AbrigoServicoImpl();

    private final IRepositorio<Abrigo> repositorio = AbrigoRepositorio.getInstance();

    private AbrigoServicoImpl() {
    }

    public static AbrigoServicoImpl getInstance() {
        return INSTANCIA;
    }

    @Override
    public Abrigo cadastrar(Abrigo abrigo) {
        if (abrigo.getNome() == null || abrigo.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do abrigo é obrigatório.");
        }

        if (abrigo.getCapacidade() <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }

        ValidadorAcesso.validar(
                abrigo.getEmail(),
                abrigo.getSenha(),
                repositorio.listarTodos().stream().map(Abrigo::getEmail)
        );

        // Todo cadastro novo entra como pedido pendente de aprovação
        abrigo.setStatus(StatusAbrigo.PENDENTE);
        abrigo.setDataPedido(LocalDate.now());

        return repositorio.salvar(abrigo);
    }

    @Override
    public Optional<Abrigo> buscarPorId(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Abrigo> listarTodos() {
        return repositorio.listarTodos();
    }

    @Override
    public Abrigo atualizar(Long id, String nome, String endereco, int capacidade) {
        Abrigo abrigo = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Abrigo não encontrado."));

        abrigo.setNome(nome);
        abrigo.setEndereco(endereco);
        abrigo.setCapacidade(capacidade);

        return repositorio.salvar(abrigo);
    }

    @Override
    public boolean adicionarAnimal(Long idAbrigo, Animal animal) {
        Optional<Abrigo> resultado = repositorio.buscarPorId(idAbrigo);

        if (resultado.isEmpty()) {
            return false;
        }

        Abrigo abrigo = resultado.get();

        boolean adicionado = abrigo.adicionarAnimal(animal);

        if (!adicionado) {
            return false;
        }

        repositorio.salvar(abrigo);

        return true;
    }

    @Override
    public boolean excluir(Long id) {
        Optional<Abrigo> abrigo = repositorio.buscarPorId(id);

        // Só exclui se existir e não tiver animais vinculados
        if (abrigo.isEmpty() || !abrigo.get().podeExcluir()) {
            return false;
        }

        return repositorio.excluirPorId(id);
    }

    @Override
    public Optional<Abrigo> autenticar(String email, String senha) {
        return repositorio.listarTodos().stream()
                .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email))
                .filter(c -> c.getSenha() != null && c.getSenha().equals(senha))
                .findFirst();
    }

    @Override
    public List<Abrigo> listarPendentes() {
        return repositorio.listarTodos().stream()
                .filter(a -> a.getStatus() == StatusAbrigo.PENDENTE)
                .sorted(Comparator.comparing(
                        Abrigo::getDataPedido,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }

    @Override
    public boolean aprovar(Long id) {
        Optional<Abrigo> resultado = repositorio.buscarPorId(id);

        if (resultado.isEmpty()) {
            return false;
        }

        Abrigo abrigo = resultado.get();

        boolean aprovado = abrigo.aprovar();

        if (!aprovado) {
            return false;
        }

        repositorio.salvar(abrigo);

        return true;
    }

    @Override
    public boolean negar(Long id) {
        Optional<Abrigo> resultado = repositorio.buscarPorId(id);

        if (resultado.isEmpty()) {
            return false;
        }

        Abrigo abrigo = resultado.get();

        boolean negado = abrigo.negar();

        if (!negado) {
            return false;
        }

        repositorio.salvar(abrigo);

        return true;
    }
}

