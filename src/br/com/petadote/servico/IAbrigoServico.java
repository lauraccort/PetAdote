package br.com.petadote.servico;

import br.com.petadote.model.Animal; // usado ao vincular animal ao abrigo
import br.com.petadote.model.Abrigo;
import java.util.List;
import java.util.Optional;

// Contrato de NEGÓCIO do Abrigo — a Controller só enxerga esta interface
public interface IAbrigoServico {

    Abrigo cadastrar(Abrigo abrigo);           // C: cria com validações

    Optional<Abrigo> buscarPorId(Long id);     // R: consulta um

    List<Abrigo> listarTodos();                // R: consulta todos

    Abrigo atualizar(Long id, String nome, String endereco, int capacidade); // U: edita

    boolean adicionarAnimal(Long idAbrigo, Animal animal); // vincula animal (REQ19)

    boolean excluir(Long id);                  // D: exclui com regra (REQ20)
}
