
package br.com.petadote.servico;

import br.com.petadote.model.Animal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REQ02: define as operações de gerenciamento dos animais.
 * REQ17: define operações de tratamento veterinário e alta.
 */
public interface IAnimalServico {

    /** REQ02: cadastra um animal. */
    Animal cadastrar(Animal animal);

    /** REQ02: busca um animal pelo identificador. */
    Optional<Animal> buscarPorId(Long id);

    /** REQ02: lista todos os animais cadastrados. */
    List<Animal> listarTodos();

    /** REQ14: lista somente os animais disponíveis para adoção. */
    List<Animal> listarDisponiveis();

    /** REQ02: atualiza os dados de um animal. */
    Animal atualizar(Long id, String nome, LocalDate dataNascimento);

    /** REQ02: exclui um animal, respeitando as regras de exclusão. */
    boolean excluir(Long id);

    /**
     * REQ17: envia um animal disponível ou em avaliação
     * para tratamento veterinário.
     */
    boolean enviarParaTratamento(Long id);

    /**
     * REQ17: registra a alta do tratamento veterinário.
     * O animal volta a disponível ou em avaliação,
     * dependendo da existência de processo ativo.
     */
    boolean receberAlta(Long id);
}

