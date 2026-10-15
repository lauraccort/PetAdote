package br.com.petadote.repositorio; 

import br.com.petadote.model.Entidade; 
import java.util.List;     
import java.util.Optional; 

public interface IRepositorio<T extends Entidade> {

    T salvar(T entidade);              

    Optional<T> buscarPorId(Long id);  

    List<T> listarTodos();             

    boolean excluirPorId(Long id);    
}
