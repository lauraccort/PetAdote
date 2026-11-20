package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.Animal;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusAnimal;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.Optional;

/**
 * Controller da TelaIniciarProcessoAdocao.fxml.
 *
 * REQ06: abrir processo de adoção.
 * REQ14: impedir solicitação para animal indisponível.
 * REQ16: respeitar o limite de processos ativos do adotante.
 *
 * O animal vem sempre do Navegador (tela de detalhes). O ID exibido é
 * somente leitura e nunca é usado como fonte de verdade: o processo é
 * aberto a partir do objeto Animal selecionado, impedindo que o adotante
 * troque o animal digitando outro ID.
 */
public class TelaIniciarProcessoAdocaoController {

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final Contexto contexto;

    /** Animal realmente selecionado na tela anterior. Fonte de verdade. */
    private Animal animalSelecionado;

    @FXML
    private TextField campoIdAnimal;

    @FXML
    private Label infoAnimal;

    @FXML
    private TextField campoMotivo;

    @FXML
    private Label mensagem;

    @FXML
    private Button btnSalvarProcesso;

    public TelaIniciarProcessoAdocaoController(
            Navegador navegador, Contexto contexto) {
        this.navegador = navegador;
        this.contexto = contexto;
    }

    @FXML
    private void initialize() {

        // O ID do animal vem preenchido da tela de detalhes e é somente leitura.
        navegador.selecionado(Animal.class).ifPresent(a -> {
            animalSelecionado = a;
            campoIdAnimal.setText(String.valueOf(a.getId()));
        });

        mostrarInfo();

        if (animalSelecionado == null) {
            erro("Nenhum animal selecionado. Volte e escolha um animal na lista.");
            btnSalvarProcesso.setDisable(true);
            return;
        }

        if (contexto.adotanteLogado().isEmpty()) {
            erro("Entre como adotante para pedir uma adoção.");
            btnSalvarProcesso.setDisable(true);
        }
    }

    @FXML
    private void iniciarProcesso() {

        Optional<Adotante> adotante = contexto.adotanteLogado();

        if (adotante.isEmpty()) {
            erro("Entre como adotante para pedir uma adoção.");
            return;
        }

        Optional<Animal> encontrado = buscarAnimal();

        if (encontrado.isEmpty()) {
            erro("Nenhum animal selecionado. Volte e escolha um animal na lista.");
            return;
        }

        Animal animal = encontrado.get();

        // REQ14: verifica se o animal está disponível.
        if (animal.getStatus() != StatusAnimal.DISPONIVEL) {
            erro(animal.getNome() + " não está disponível para adoção ("
                    + animal.getStatus().getDescricao() + ").");
            return;
        }

        // REQ16: verifica o limite de processos ativos.
        if (!adotante.get().podeIniciarNovoProcesso()) {
            erro("Você já tem 3 processos ativos (em avaliação ou aprovados). "
                    + "Aguarde a conclusão de algum deles.");
            return;
        }

        String motivo = campoMotivo.getText().trim();

        if (motivo.isEmpty()) {
            erro("Conte um pouco sobre o ambiente e a sua motivação para adotar.");
            return;
        }

        try {
            // REQ06: inicia o processo de adoção.
            ProcessoAdocao processo = contexto.fachada().iniciarProcesso(animal.getId(), adotante.get().getId());

            // Apoio ao REQ06/REQ07: registra a justificativa pelo serviço,
            // garantindo que ela seja persistida.
            contexto.fachada().registrarJustificativa(
                    processo.getId(), motivo
            );

            campoMotivo.clear();

            mensagem.setTextFill(COR_SUCESSO);
            mensagem.setText("Pedido enviado! Processo " + processo.getId()
                    + " para adotar " + animal.getNome()
                    + " está em avaliação. Acompanhe em \"Meus processos\".");

        } catch (IllegalArgumentException | IllegalStateException e) {
            erro(e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    /** Exibe informações do animal selecionado. */
    private void mostrarInfo() {

        if (animalSelecionado == null) {
            infoAnimal.setText("");
            return;
        }

        Animal a = animalSelecionado;

        infoAnimal.setText(a.getNome() + "  ·  "
                + TextoAnimal.especieERaca(a)
                + "  ·  " + TextoAnimal.idade(a.getDataNascimento())
                + "  ·  " + a.getStatus().getDescricao());
    }

    /**
     * Retorna o animal selecionado. O texto do campo NÃO é lido:
     * o ID exibido é apenas informativo.
     */
    private Optional<Animal> buscarAnimal() {
        return Optional.ofNullable(animalSelecionado);
    }

    /** Exibe mensagens de erro na tela. */
    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}