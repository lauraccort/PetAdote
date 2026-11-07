package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarMinhasVisitas.fxml (Minhas visitas).
 */
public class TelaListarMinhasVisitasController {

    private final Navegador navegador;

    public TelaListarMinhasVisitasController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirAgendarVisita() {
        navegador.ir("TelaAgendarVisita.fxml");
    }

    @FXML
    private void abrirConsultarResultadoVisita() {
        navegador.ir("TelaConsultarResultadoVisita.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
