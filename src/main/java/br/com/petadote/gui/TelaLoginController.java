package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaLogin.fxml (Login).
 */
public class TelaLoginController {

    private final Navegador navegador;

    public TelaLoginController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirPrincipalAbrigo() {
        navegador.ir("TelaPrincipalAbrigo.fxml");
    }

    @FXML
    private void abrirPrincipalAdotante() {
        navegador.ir("TelaPrincipalAdotante.fxml");
    }

    @FXML
    private void abrirPrincipalAdministrador() {
        navegador.ir("TelaPrincipalAdministrador.fxml");
    }

    @FXML
    private void abrirCadastro() {
        navegador.ir("TelaCadastro.fxml");
    }
}
