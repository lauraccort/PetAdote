package br.com.petadote.repositorio;

import br.com.petadote.model.Entidade;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RepositorioBase<T extends Entidade> implements IRepositorio<T> {

    private final Map<Long, T> banco = new HashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public T salvar(T entidade) {
        boolean nova = entidade.getId() == null;
        if (nova) {
            entidade.setId(sequencia.getAndIncrement());
        }
        Long id = entidade.getId();
        T anterior = banco.put(id, entidade);
        try {
            Persistencia.salvar();
        } catch (RuntimeException e) {
            if (anterior == null) {
                banco.remove(id);
            } else {
                banco.put(id, anterior);
            }
            if (nova) {
                entidade.setId(null);
            }
            throw e;
        }
        return entidade;
    }

    @Override
    public Optional<T> buscarPorId(Long id) {
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(banco.values());
    }

    @Override
    public boolean excluirPorId(Long id) {
        boolean removeu = banco.remove(id) != null;
        Persistencia.salvar();
        return removeu;
    }

    Object[] exportar() {
        return new Object[]{banco, sequencia.get()};
    }

    @SuppressWarnings("unchecked")
    void restaurar(Object[] estado) {
        banco.clear();
        banco.putAll((Map<Long, T>) estado[0]);
        sequencia.set((Long) estado[1]);
    }
}
