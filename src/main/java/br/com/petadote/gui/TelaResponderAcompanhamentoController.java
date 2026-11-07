package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaResponderAcompanhamento.fxml (Responder acompanhamento).
 */
public class TelaResponderAcompanhamentoController {

    private final Navegador navegador;

    public TelaResponderAcompanhamentoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
