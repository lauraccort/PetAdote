package br.com.petadote.repositorio;

import br.com.petadote.model.Entidade;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RepositorioBase<T extends Entidade> implements IRepositorio<T> {

    private final Map<Long, T> banco = new HashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public T salvar(T entidade) {
        if (entidade.getId() == null) {
            entidade.setId(sequencia.getAndIncrement());
        }
        banco.put(entidade.getId(), entidade);
        Persistencia.salvar();          // NOVO: grava o arquivo
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
        Persistencia.salvar();          // NOVO: grava o arquivo
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