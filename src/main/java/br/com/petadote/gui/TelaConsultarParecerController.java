package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaConsultarParecer.fxml (Parecer).
 */
public class TelaConsultarParecerController {

    private final Navegador navegador;

    public TelaConsultarParecerController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
