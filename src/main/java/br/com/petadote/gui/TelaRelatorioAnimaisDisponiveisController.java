package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaRelatorioAnimaisDisponiveis.fxml (Relatório de animais disponíveis).
 */
public class TelaRelatorioAnimaisDisponiveisController {

    private final Navegador navegador;

    public TelaRelatorioAnimaisDisponiveisController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
