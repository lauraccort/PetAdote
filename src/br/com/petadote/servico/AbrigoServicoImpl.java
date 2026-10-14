package br.com.petadote.servico;

import br.com.petadote.model.Animal;
import br.com.petadote.model.Abrigo;
import br.com.petadote.repositorio.IRepositorio; // depende da INTERFACE, não da concreta
import java.util.List;
import java.util.Optional;

// Implementa o contrato; recebe o repositório pronto pelo construtor
public class AbrigoServicoImpl implements IAbrigoServico {

    private final IRepositorio<Abrigo> repositorio; // dependência injetada

    // Quem criar o service decide qual repositório usar
    public AbrigoServicoImpl(IRepositorio<Abrigo> repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Abrigo cadastrar(Abrigo abrigo) {
        // Validação: nome é obrigatório
        if (abrigo.getNome() == null || abrigo.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do abrigo é obrigatório.");
        }
        // Validação: capacidade deve ser positiva
        if (abrigo.getCapacidade() <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
        return repositorio.salvar(abrigo); // validou? persiste (id nasce aqui)
    }

    @Override
    public Optional<Abrigo> buscarPorId(Long id) {
        return repositorio.buscarPorId(id); // consulta direta
    }

    @Override
    public List<Abrigo> listarTodos() {
        return repositorio.listarTodos();
    }

    @Override
    public Abrigo atualizar(Long id, String nome, String endereco, int capacidade) {
        // Localiza o abrigo; erro se não existir
        Abrigo abrigo = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Abrigo não encontrado."));
        abrigo.setNome(nome);             // atualiza o nome
        abrigo.setEndereco(endereco);     // atualiza o endereço
        abrigo.setCapacidade(capacidade); // atualiza a capacidade
        return repositorio.salvar(abrigo); // salvar com id existente = UPDATE
    }

    @Override
    public boolean adicionarAnimal(Long idAbrigo, Animal animal) {
        // Encontra o abrigo e aplica a regra REQ19 (capacidade + não duplicar vínculo)
        return repositorio.buscarPorId(idAbrigo)
                .map(abrigo -> abrigo.adicionarAnimal(animal))
                .orElse(false); // abrigo inexistente = falha
    }

    @Override
    public boolean excluir(Long id) {
        Optional<Abrigo> abrigo = repositorio.buscarPorId(id);
        // REQ20: só exclui se existir E não tiver animais vinculados
        if (abrigo.isEmpty() || !abrigo.get().podeExcluir()) {
            return false; // bloqueia a exclusão
        }
        return repositorio.excluirPorId(id); // liberado, exclui
    }
}
