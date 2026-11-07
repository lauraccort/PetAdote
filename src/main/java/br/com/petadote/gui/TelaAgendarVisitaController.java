package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaAgendarVisita.fxml (Agendar visita).
 */
public class TelaAgendarVisitaController {

    private final Navegador navegador;

    public TelaAgendarVisitaController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
