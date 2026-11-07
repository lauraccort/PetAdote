package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaAcompanharPosAdocao.fxml (Acompanhamento pós-adoção).
 */
public class TelaAcompanharPosAdocaoController {

    private final Navegador navegador;

    public TelaAcompanharPosAdocaoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirListarVisitas() {
        navegador.ir("TelaListarVisitas.fxml");
    }

    @FXML
    private void abrirPrincipalAbrigo() {
        navegador.ir("TelaPrincipalAbrigo.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
