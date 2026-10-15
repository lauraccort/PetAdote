package br.com.petadote.controle;

import br.com.petadote.model.Animal;
import br.com.petadote.model.Cao;
import br.com.petadote.model.StatusAnimal;
import br.com.petadote.servico.IAnimalServico;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AnimalControle {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy"); 

    private final IAnimalServico servico;

    public AnimalControle(IAnimalServico servico) {
        this.servico = servico;
    }

    public void criarCao(String nome, String dataNascimento, String raca) {
        try {
            LocalDate nascimento = LocalDate.parse(dataNascimento, FORMATO);
            Animal cao = servico.cadastrar(new Cao(nome, nascimento, raca));
            System.out.println("Cão cadastrado: " + cao.getNome() + " (id " + cao.getId() + ")");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void listar() {
        List<Animal> animais = servico.listarTodos();
        if (animais.isEmpty()) {
            System.out.println("Nenhum animal cadastrado.");
            return;
        }
        animais.forEach(a -> System.out.println(
                a.getId() + " - " + a.getNome()
                        + " [" + a.getStatus() + "]"
                        + (a.getAbrigo() != null ? " @ " + a.getAbrigo().getNome() : "")));
    }

    public void listarDisponiveis() {
        servico.listarDisponiveis().forEach(a ->
                System.out.println(a.getId() + " - " + a.getNome() + " [" + StatusAnimal.DISPONIVEL + "]"));
    }

    public void atualizar(Long id, String nome, String dataNascimento) {
        try {
            servico.atualizar(id, nome, LocalDate.parse(dataNascimento, FORMATO));
            System.out.println("Animal atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void excluir(Long id) {
        System.out.println(service.excluir(id)
                ? "Animal excluído com sucesso."
                : "Não foi possível excluir: inexistente, sem status DISPONIVEL ou vinculado a abrigo.");
    }
}
