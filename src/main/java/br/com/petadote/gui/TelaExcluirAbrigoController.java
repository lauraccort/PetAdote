package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaExcluirAbrigo.fxml (Excluir abrigo).
 */
public class TelaExcluirAbrigoController {

    private final Navegador navegador;

    public TelaExcluirAbrigoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
