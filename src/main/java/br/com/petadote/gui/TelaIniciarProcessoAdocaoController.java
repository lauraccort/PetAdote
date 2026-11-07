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
 * Controller da TelaIniciarProcessoAdocao.fxml (REQ06: abrir processo de adoção).
 * O animal vem preenchido da tela de detalhes; o adotante é quem está logado.
 * Explica os bloqueios do REQ14 (animal indisponível) e do REQ16 (limite de processos ativos).
 */
public class TelaIniciarProcessoAdocaoController {

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final Contexto contexto;

    @FXML private TextField campoIdAnimal;
    @FXML private Label infoAnimal;
    @FXML private TextField campoMotivo;
    @FXML private Label mensagem;
    @FXML private Button btnSalvarProcesso;

    public TelaIniciarProcessoAdocaoController(Navegador navegador, Contexto contexto) {
        this.navegador = navegador;
        this.contexto = contexto;
    }

    @FXML
    private void initialize() {
        campoIdAnimal.textProperty().addListener((obs, anterior, texto) -> {
            mensagem.setText("");
            mostrarInfo();
        });
        // Vindo da tela de detalhes, o ID do animal já vem preenchido
        navegador.selecionado(Animal.class).ifPresent(a -> campoIdAnimal.setText(String.valueOf(a.getId())));
        mostrarInfo();

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
            erro("Informe o ID de um animal existente.");
            return;
        }
        Animal animal = encontrado.get();
        if (animal.getStatus() != StatusAnimal.DISPONIVEL) { // REQ14
            erro(animal.getNome() + " não está disponível para adoção (" + animal.getStatus().getDescricao() + ").");
            return;
        }
        if (!adotante.get().podeIniciarNovoProcesso()) { // REQ16
            erro("Você já tem 3 processos ativos (em avaliação ou aprovados). Aguarde a conclusão de algum deles.");
            return;
        }
        String motivo = campoMotivo.getText().trim();
        if (motivo.isEmpty()) {
            erro("Conte um pouco sobre o ambiente e a sua motivação para adotar.");
            return;
        }

        try {
            ProcessoAdocao processo = contexto.processoServico().iniciar(animal.getId(), adotante.get().getId());
            processo.setJustificativaAdotante(motivo);
            campoIdAnimal.clear();
            campoMotivo.clear();
            mensagem.setTextFill(COR_SUCESSO);
            mensagem.setText("Pedido enviado! Processo " + processo.getId() + " para adotar " + animal.getNome()
                    + " está em avaliação. Acompanhe em \"Meus processos\".");
        } catch (IllegalArgumentException | IllegalStateException e) {
            erro(e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void mostrarInfo() {
        Optional<Animal> animal = buscarAnimal();
        if (campoIdAnimal.getText().isBlank()) {
            infoAnimal.setText("");
        } else if (animal.isEmpty()) {
            infoAnimal.setText("Animal não encontrado.");
        } else {
            Animal a = animal.get();
            infoAnimal.setText(a.getNome() + "  ·  " + TextoAnimal.especieERaca(a)
                    + "  ·  " + TextoAnimal.idade(a.getDataNascimento())
                    + "  ·  " + a.getStatus().getDescricao());
        }
    }

    private Optional<Animal> buscarAnimal() {
        try {
            return contexto.animalServico().buscarPorId(Long.parseLong(campoIdAnimal.getText().trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}
