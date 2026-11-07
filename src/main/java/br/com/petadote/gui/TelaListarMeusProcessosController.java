package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarMeusProcessos.fxml (Meus processos).
 */
public class TelaListarMeusProcessosController {

    private final Navegador navegador;

    public TelaListarMeusProcessosController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirConsultarParecer() {
        navegador.ir("TelaConsultarParecer.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
