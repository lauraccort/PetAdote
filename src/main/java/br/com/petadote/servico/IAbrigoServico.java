package br.com.petadote.servico;

import br.com.petadote.model.Animal; 
import br.com.petadote.model.Abrigo;
import java.util.List;
import java.util.Optional;

public interface IAbrigoServico {

    Abrigo cadastrar(Abrigo abrigo);           

    Optional<Abrigo> buscarPorId(Long id);    

    List<Abrigo> listarTodos();               

    Abrigo atualizar(Long id, String nome, String endereco, int capacidade); 

    boolean adicionarAnimal(Long idAbrigo, Animal animal); // vincula animal (REQ19)

    boolean excluir(Long id);

    /** Login: devolve o abrigo se o e-mail e a senha conferem. */
    Optional<Abrigo> autenticar(String email, String senha);

    /** Pedidos de cadastro aguardando o administrador, do mais antigo ao mais novo. */
    List<Abrigo> listarPendentes();

    /** Administrador aprova o pedido; o abrigo passa a poder entrar no sistema. */
    boolean aprovar(Long id);

    /** Administrador nega o pedido. */
    boolean negar(Long id);                  // D: exclui com regra (REQ20)
}
