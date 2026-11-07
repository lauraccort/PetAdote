package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarAbrigos.fxml (Abrigos).
 */
public class TelaListarAbrigosController {

    private final Navegador navegador;

    public TelaListarAbrigosController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirAprovarCadastroAbrigo() {
        navegador.ir("TelaAprovarCadastroAbrigo.fxml");
    }

    @FXML
    private void abrirExcluirAbrigo() {
        navegador.ir("TelaExcluirAbrigo.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
