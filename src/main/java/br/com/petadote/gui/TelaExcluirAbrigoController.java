package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.servico.IAbrigoServico;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.util.Optional;

/**
 * Controller da TelaExcluirAbrigo.fxml (Excluir abrigo).
 * Exclui pelo ID; REQ20 bloqueia a exclusão de abrigo que ainda tem animais.
 */
public class TelaExcluirAbrigoController {

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final IAbrigoServico abrigoServico;

    @FXML private TextField campoIdAbrigo;
    @FXML private Label infoAbrigo;
    @FXML private Label mensagem;

    public TelaExcluirAbrigoController(Navegador navegador, IAbrigoServico abrigoServico) {
        this.navegador = navegador;
        this.abrigoServico = abrigoServico;
    }

    @FXML
    private void initialize() {
        campoIdAbrigo.textProperty().addListener((obs, anterior, texto) -> {
            mensagem.setText(""); // mensagem antiga não vale para o novo ID
            mostrarInfo();
        });
        // Vindo da lista de abrigos com um abrigo selecionado, o ID já vem preenchido
        navegador.selecionado(Abrigo.class).ifPresent(a -> campoIdAbrigo.setText(String.valueOf(a.getId())));
        mostrarInfo();
    }

    @FXML
    private void excluirAbrigo() {
        Optional<Abrigo> encontrado = buscarAbrigo();
        if (encontrado.isEmpty()) {
            erro("Informe o ID de um abrigo existente.");
            return;
        }
        Abrigo abrigo = encontrado.get();
        if (!abrigo.podeExcluir()) { // REQ20
            int n = abrigo.getQuantidadeAnimais();
            erro("Não é possível excluir " + abrigo.getNome() + ": ainda há " + n
                    + (n == 1 ? " animal vinculado" : " animais vinculados") + " a ele (REQ20).");
            return;
        }
        if (!abrigoServico.excluir(abrigo.getId())) {
            erro("Não foi possível excluir este abrigo.");
            return;
        }
        campoIdAbrigo.clear();
        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText("Abrigo " + abrigo.getNome() + " excluído.");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void mostrarInfo() {
        Optional<Abrigo> abrigo = buscarAbrigo();
        if (campoIdAbrigo.getText().isBlank()) {
            infoAbrigo.setText("");
        } else if (abrigo.isEmpty()) {
            infoAbrigo.setText("Abrigo não encontrado.");
        } else {
            Abrigo a = abrigo.get();
            int n = a.getQuantidadeAnimais();
            infoAbrigo.setText(a.getNome() + "  ·  " + a.getEndereco() + "  ·  "
                    + (n == 0 ? "nenhum animal vinculado" : n + (n == 1 ? " animal vinculado" : " animais vinculados")));
        }
    }

    private Optional<Abrigo> buscarAbrigo() {
        try {
            return abrigoServico.buscarPorId(Long.parseLong(campoIdAbrigo.getText().trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}
