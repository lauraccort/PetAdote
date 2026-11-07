package br.com.petadote.servico;

import br.com.petadote.model.Animal;
import java.time.LocalDate; 
import java.util.List;
import java.util.Optional;


public interface IAnimalServico {

    Animal cadastrar(Animal animal);           

    Optional<Animal> buscarPorId(Long id);     

    List<Animal> listarTodos();                

    List<Animal> listarDisponiveis();          

    Animal atualizar(Long id, String nome, LocalDate dataNascimento); 

    boolean excluir(Long id);                  
}
