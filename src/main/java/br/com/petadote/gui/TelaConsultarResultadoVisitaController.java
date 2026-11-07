package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaConsultarResultadoVisita.fxml (Resultado da visita).
 */
public class TelaConsultarResultadoVisitaController {

    private final Navegador navegador;

    public TelaConsultarResultadoVisitaController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
