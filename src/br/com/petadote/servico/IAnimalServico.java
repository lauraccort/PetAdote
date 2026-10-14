package br.com.petadote.servico;

import br.com.petadote.model.Animal;
import java.time.LocalDate; // tipo da data de nascimento
import java.util.List;
import java.util.Optional;

// Contrato de negócio da entidade Animal
public interface IAnimalServico {

    Animal cadastrar(Animal animal);           // C: recebe Cao/Gato/OutraEspecie pronto

    Optional<Animal> buscarPorId(Long id);     // R: um animal

    List<Animal> listarTodos();                // R: todos

    List<Animal> listarDisponiveis();          // R: só os DISPONIVEL (útil p/ adoção)

    Animal atualizar(Long id, String nome, LocalDate dataNascimento); // U

    boolean excluir(Long id);                  // D com regra de negócio
}
