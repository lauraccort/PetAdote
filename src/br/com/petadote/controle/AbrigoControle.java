package br.com.petadote.controle;

import br.com.petadote.model.Abrigo;
import br.com.petadote.servico.IAbrigoServico;
import java.util.List;

public class AbrigoControle {

    private final IAbrigoServico servico;

    public AbrigoControle(IAbrigoServico servico) {
        this.servico = servico;
    }

    public void criar(String nome, String endereco, int capacidade) {
        try {
            Abrigo abrigo = servico.cadastrar(new Abrigo(nome, endereco, capacidade));
            System.out.println("Abrigo cadastrado: " + abrigo.getNome() + " (id " + abrigo.getId() + ")");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void listar() {
        List<Abrigo> abrigos = servico.listarTodos();
        if (abrigos.isEmpty()) {
            System.out.println("Nenhum abrigo cadastrado.");
            return;
        }
        abrigos.forEach(a -> System.out.println(
                a.getId() + " - " + a.getNome() + " (" + a.getQuantidadeAnimais() + "/" + a.getCapacidade() + ")"));
    }

    public void atualizar(Long id, String nome, String endereco, int capacidade) {
        try {
            servico.atualizar(id, nome, endereco, capacidade); // delega ao service
            System.out.println("Abrigo atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void excluir(Long id) {
        System.out.println(servico.excluir(id)
                ? "Abrigo excluído com sucesso."
                : "Não foi possível excluir: abrigo inexistente ou possui animais vinculados.");
    }
}
