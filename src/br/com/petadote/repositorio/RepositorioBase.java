package br.com.petadote.repositorio;

import br.com.petadote.model.Entidade; // as entidades guardadas herdam daqui
import java.util.*;                            // Map, HashMap, List, ArrayList, Optional
import java.util.concurrent.atomic.AtomicLong; // gerador de ids seguro

// Classe ABSTRATA: implementa a interface UMA vez, com armazenamento em memória.
// Todas as concretas herdam daqui e NÃO repetem nenhuma linha deste código.
public abstract class RepositorioBase<T extends Entidade> implements IRepositorio<T> {

    // "banco" em memória: chave = id, valor = a entidade
    private final Map<Long, T> banco = new HashMap<>();

    // Sequência de ids: começa em 1 e incrementa a cada cadastro novo
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public T salvar(T entidade) {
        if (entidade.getId() == null) { // id nulo = cadastro novo
            entidade.setId(sequencia.getAndIncrement()); // gera o id e prepara o próximo
        }
        banco.put(entidade.getId(), entidade); // insere ou sobrescreve no mapa
        return entidade; // devolve a entidade salva, já com id
    }

    @Override
    public Optional<T> buscarPorId(Long id) {
        return Optional.ofNullable(banco.get(id)); // embrulha o resultado, mesmo se null
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(banco.values()); // devolve cópia, protegendo o mapa interno
    }

    @Override
    public boolean excluirPorId(Long id) {
        return banco.remove(id) != null; // remove() devolve o objeto; se veio algo, excluiu
    }
}
