package br.com.petadote.servico;

import br.com.petadote.model.Animal;
import br.com.petadote.model.StatusAnimal; // enum de status do animal
import br.com.petadote.repositorio.IRepositorio;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // usado no filtro de disponíveis

public class AnimalServicoImpl implements IAnimalServico {

    private final IRepositorio<Animal> repositorio; // dependência injetada

    public AnimalServicoImpl(IRepositorio<Animal> repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Animal cadastrar(Animal animal) {
        // Validação: nome obrigatório
        if (animal.getNome() == null || animal.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do animal é obrigatório.");
        }
        // Validação: data de nascimento não pode ser no futuro
        if (animal.getDataNascimento() != null && animal.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser futura.");
        }
        return repositorio.salvar(animal); // persiste (id nasce aqui)
    }

    @Override
    public Optional<Animal> buscarPorId(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Animal> listarTodos() {
        return repositorio.listarTodos();
    }

    @Override
    public List<Animal> listarDisponiveis() {
        // Filtra apenas animais com status DISPONIVEL
        return repositorio.listarTodos().stream()
                .filter(a -> a.getStatus() == StatusAnimal.DISPONIVEL)
                .collect(Collectors.toList());
    }

    @Override
    public Animal atualizar(Long id, String nome, LocalDate dataNascimento) {
        // Localiza o animal; erro se não existir
        Animal animal = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado."));
        animal.setNome(nome);                     // atualiza o nome
        animal.setDataNascimento(dataNascimento); // atualiza a data
        return repository.salvar(animal);         // UPDATE
    }

    @Override
    public boolean excluir(Long id) {
        Optional<Animal> animal = repository.buscarPorId(id);
        // Regra: só exclui se estiver DISPONIVEL e sem abrigo vinculado
        // (em avaliação/adotado faz parte do histórico; com abrigo, remova o vínculo antes)
        if (animal.isEmpty()
                || animal.get().getStatus() != StatusAnimal.DISPONIVEL
                || animal.get().getAbrigo() != null) {
            return false;
        }
        return repositorio.excluirPorId(id);
    }
}
