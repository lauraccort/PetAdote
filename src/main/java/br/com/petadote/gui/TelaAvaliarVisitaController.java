package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaAvaliarVisita.fxml (Avaliar visita).
 */
public class TelaAvaliarVisitaController {

    private final Navegador navegador;

    public TelaAvaliarVisitaController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
