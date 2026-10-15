package br.com.petadote.controle;

import br.com.petadote.model.Adotante;
import br.com.petadote.servico.IAdotanteServico;
import java.util.List;

public class AdotanteControle {

    private final IAdotanteServico servico;

    public AdotanteControle(IAdotanteServico servico) {
        this.servico = servico;
    }

    public void criar(String nome, String cpf, String endereco, int quantAnimal) {
        try {
            Adotante adotante = servico.cadastrar(new Adotante(nome, cpf, endereco, quantAnimal));
            System.out.println("Adotante cadastrado: " + adotante.getNome() + " (id " + adotante.getId() + ")");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void listar() {
        List<Adotante> adotantes = servico.listarTodos();
        if (adotantes.isEmpty()) {
            System.out.println("Nenhum adotante cadastrado.");
            return;
        }
        adotantes.forEach(a -> System.out.println(
                a.getId() + " - " + a.getNome() + " | CPF: " + a.getCpf()));
    }

    public void atualizar(Long id, String nome, String endereco) {
        try {
            servico.atualizar(id, nome, endereco);
            System.out.println("Adotante atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void excluir(Long id) {
        System.out.println(servico.excluir(id)
                ? "Adotante excluído com sucesso."
                : "Não foi possível excluir: inexistente ou possui histórico de processos.");
    }
}
