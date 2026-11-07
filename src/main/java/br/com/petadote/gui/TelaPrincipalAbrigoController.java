package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaPrincipalAbrigo.fxml (Principal - Abrigo).
 */
public class TelaPrincipalAbrigoController {

    private final Navegador navegador;

    public TelaPrincipalAbrigoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirListarAnimaisAbrigo() {
        navegador.ir("TelaListarAnimaisAbrigo.fxml");
    }

    @FXML
    private void abrirListarAdotantes() {
        navegador.ir("TelaListarAdotantes.fxml");
    }

    @FXML
    private void abrirListarProcessosAdocao() {
        navegador.ir("TelaListarProcessosAdocao.fxml");
    }

    @FXML
    private void abrirListarVisitas() {
        navegador.ir("TelaListarVisitas.fxml");
    }

    @FXML
    private void sair() {
        navegador.sair();
    }
}
