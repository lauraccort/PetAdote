
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

    boolean adicionarAnimal(Long idAbrigo, Animal animal);

    boolean excluir(Long id);

    Optional<Abrigo> autenticar(String email, String senha);

    List<Abrigo> listarPendentes();

    boolean aprovar(Long id);

    boolean negar(Long id);
}
