package br.com.petadote.servico;

import br.com.petadote.model.Animal;
import br.com.petadote.model.Abrigo;
import br.com.petadote.repositorio.IRepositorio;
import br.com.petadote.repositorio.AbrigoRepositorio;
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
        ValidadorAcesso.validar(abrigo.getEmail(), abrigo.getSenha(),
                repositorio.listarTodos().stream().map(Abrigo::getEmail));
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
        // Encontra o abrigo e aplica a regra REQ19 (capacidade + não duplicar vínculo)
        return repositorio.buscarPorId(idAbrigo)
                .map(abrigo -> abrigo.adicionarAnimal(animal))
                .orElse(false);
    }

    @Override
    public boolean excluir(Long id) {
        Optional<Abrigo> abrigo = repositorio.buscarPorId(id);
        // REQ20: só exclui se existir E não tiver animais vinculados
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
}
