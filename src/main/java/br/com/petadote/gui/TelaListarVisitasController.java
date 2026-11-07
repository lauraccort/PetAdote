package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarVisitas.fxml (Visitas).
 */
public class TelaListarVisitasController {

    private final Navegador navegador;

    public TelaListarVisitasController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirAvaliarVisita() {
        navegador.ir("TelaAvaliarVisita.fxml");
    }

    @FXML
    private void abrirConfirmarAgendamentoVisita() {
        navegador.ir("TelaConfirmarAgendamentoVisita.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
