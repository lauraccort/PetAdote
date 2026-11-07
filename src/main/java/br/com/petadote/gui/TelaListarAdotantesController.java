package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarAdotantes.fxml (Adotantes).
 */
public class TelaListarAdotantesController {

    private final Navegador navegador;

    public TelaListarAdotantesController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirHistoricoAdotante() {
        navegador.ir("TelaHistoricoAdotante.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
