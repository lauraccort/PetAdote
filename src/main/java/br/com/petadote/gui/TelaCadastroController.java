package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaCadastro.fxml (Cadastro).
 */
public class TelaCadastroController {

    private final Navegador navegador;

    public TelaCadastroController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
