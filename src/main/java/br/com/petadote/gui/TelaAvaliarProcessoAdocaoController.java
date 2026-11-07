package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaAvaliarProcessoAdocao.fxml (Avaliar processo de adoção).
 */
public class TelaAvaliarProcessoAdocaoController {

    private final Navegador navegador;

    public TelaAvaliarProcessoAdocaoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
