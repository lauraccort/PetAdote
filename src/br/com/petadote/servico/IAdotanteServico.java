package br.com.petadote.servico;

import br.com.petadote.model.Adotante;
import java.util.List;
import java.util.Optional;

// Contrato de negócio da entidade Adotante
public interface IAdotanteServico {

    Adotante cadastrar(Adotante adotante);   // C

    Optional<Adotante> buscarPorId(Long id); // R

    List<Adotante> listarTodos();            // R

    Adotante atualizar(Long id, String nome, String endereco); // U

    boolean excluir(Long id);                // D
}
