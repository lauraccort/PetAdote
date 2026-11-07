package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaRelatorios.fxml (Relatórios).
 */
public class TelaRelatoriosController {

    private final Navegador navegador;

    public TelaRelatoriosController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirRelatorioAnimaisDisponiveis() {
        navegador.ir("TelaRelatorioAnimaisDisponiveis.fxml");
    }

    @FXML
    private void abrirRelatorioAdocoesConcluidas() {
        navegador.ir("TelaRelatorioAdocoesConcluidas.fxml");
    }

    @FXML
    private void abrirRelatorioProcessosPendentes() {
        navegador.ir("TelaRelatorioProcessosPendentes.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
