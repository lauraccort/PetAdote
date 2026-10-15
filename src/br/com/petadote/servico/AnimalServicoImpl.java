package br.com.petadote.servico;

import br.com.petadote.model.Animal;
import br.com.petadote.model.StatusAnimal;
import br.com.petadote.repositorio.IRepositorio;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnimalServicoImpl implements IAnimalServico {
    private final IRepositorio<Animal> repositorio; 

    public AnimalServicoImpl(IRepositorio<Animal> repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Animal cadastrar(Animal animal) {
        if (animal.getNome() == null || animal.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do animal é obrigatório.");
        }
        if (animal.getDataNascimento() != null && animal.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser futura.");
        }
        return repositorio.salvar(animal);
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
        return repositorio.listarTodos().stream()
                .filter(a -> a.getStatus() == StatusAnimal.DISPONIVEL)
                .collect(Collectors.toList());
    }

    @Override
    public Animal atualizar(Long id, String nome, LocalDate dataNascimento) {
        Animal animal = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado."));
        animal.setNome(nome);                     
        animal.setDataNascimento(dataNascimento); 
        return repository.salvar(animal);         
    }

    @Override
    public boolean excluir(Long id) {
        Optional<Animal> animal = repository.buscarPorId(id);
        if (animal.isEmpty()
                || animal.get().getStatus() != StatusAnimal.DISPONIVEL
                || animal.get().getAbrigo() != null) {
            return false;
        }
        return repositorio.excluirPorId(id);
    }
}
