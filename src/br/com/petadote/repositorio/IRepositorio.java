package br.com.petadote.repositorio; // pacote da camada de persistência

import br.com.petadote.model.Entidade; // restrição: só aceita entidades
import java.util.List;     // retorno da listagem
import java.util.Optional; // evita retornar null nas buscas

// Interface GENÉRICA: <T extends Entidade> aceita Abrigo, Animal, Adotante etc.
public interface IRepositorio<T extends Entidade> {

    T salvar(T entidade);              // insere (id nulo) ou atualiza (id preenchido)

    Optional<T> buscarPorId(Long id);  // busca pelo id; Optional indica que pode não achar

    List<T> listarTodos();             // retorna todos os registros

    boolean excluirPorId(Long id);     // exclui pelo id; true se excluiu algo
}
