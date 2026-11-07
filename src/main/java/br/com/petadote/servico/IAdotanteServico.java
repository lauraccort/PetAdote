package br.com.petadote.servico;

import br.com.petadote.model.Adotante;
import java.util.List;
import java.util.Optional;

public interface IAdotanteServico {

    Adotante cadastrar(Adotante adotante);   

    Optional<Adotante> buscarPorId(Long id); 

    List<Adotante> listarTodos();            

    Adotante atualizar(Long id, String nome, String endereco); 

    boolean excluir(Long id);                
}
