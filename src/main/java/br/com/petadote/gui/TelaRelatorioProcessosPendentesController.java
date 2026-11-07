package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaRelatorioProcessosPendentes.fxml (Relatório de processos pendentes (há 15 dias)).
 */
public class TelaRelatorioProcessosPendentesController {

    private final Navegador navegador;

    public TelaRelatorioProcessosPendentesController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
