package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaRelatorioAdocoesConcluidas.fxml (Relatório de adoções concluídas).
 */
public class TelaRelatorioAdocoesConcluidasController {

    private final Navegador navegador;

    public TelaRelatorioAdocoesConcluidasController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
