package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.servico.PetAdoteFachada;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.util.Optional;


//REQ20 bloqueia a exclusão de abrigo que ainda tem animais.

public class TelaExcluirAbrigoController {

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final PetAdoteFachada fachada;

    @FXML private TextField campoIdAbrigo;
    @FXML private Label infoAbrigo;
    @FXML private Label mensagem;

    private Abrigo abrigoSelecionado;

    public TelaExcluirAbrigoController(Navegador navegador, PetAdoteFachada fachada) {
        this.navegador = navegador;
        this.fachada = fachada;
    }

    @FXML
    private void initialize() {

        abrigoSelecionado = navegador.selecionado(Abrigo.class)
                .orElse(null);

        if (abrigoSelecionado != null) {
            campoIdAbrigo.setText(String.valueOf(abrigoSelecionado.getId()));
        }

        mostrarInfo();
    }

    @FXML
    private void excluirAbrigo() {
        Optional<Abrigo> encontrado = buscarAbrigo();
        if (encontrado.isEmpty()) {
            erro("Nenhum abrigo selecionado. Volte e escolha um abrigo na lista.");
            return;
        }
        Abrigo abrigo = encontrado.get();
        if (!abrigo.podeExcluir()) { // REQ20
            int n = abrigo.getQuantidadeAnimais();
            erro("Não é possível excluir " + abrigo.getNome() + ": ainda há " + n
                    + (n == 1 ? " animal vinculado" : " animais vinculados") + " a ele (REQ20).");
            return;
        }
        if (!fachada.excluirAbrigo(abrigo.getId())) {
            erro("Não foi possível excluir este abrigo.");
            return;
        }
        abrigoSelecionado = null;
        campoIdAbrigo.clear();
        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText("Abrigo " + abrigo.getNome() + " excluído.");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void mostrarInfo() {
        if (abrigoSelecionado == null) {
            infoAbrigo.setText("");
            return;
        }

        Abrigo a = abrigoSelecionado;
        int n = a.getQuantidadeAnimais();
        infoAbrigo.setText(a.getNome() + "  ·  " + a.getEndereco() + "  ·  "
                + (n == 0 ? "nenhum animal vinculado" : n + (n == 1 ? " animal vinculado" : " animais vinculados")));
    }

    private Optional<Abrigo> buscarAbrigo() {
        return Optional.ofNullable(abrigoSelecionado);
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}