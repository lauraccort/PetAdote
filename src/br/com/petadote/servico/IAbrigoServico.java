package br.com.petadote.servico;

import br.com.petadote.model.Animal; 
import br.com.petadote.model.Abrigo;
import java.util.List;
import java.util.Optional;

public interface IAbrigoServico {

    Abrigo cadastrar(Abrigo abrigo);           

    Optional<Abrigo> buscarPorId(Long id);    

    List<Abrigo> listarTodos();               

    Abrigo atualizar(Long id, String nome, String endereco, int capacidade); 

    boolean adicionarAnimal(Long idAbrigo, Animal animal); // vincula animal (REQ19)

    boolean excluir(Long id);                  // D: exclui com regra (REQ20)
}
