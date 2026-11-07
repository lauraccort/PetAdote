package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaIniciarProcessoAdocao.fxml (Iniciar processo de adoção).
 */
public class TelaIniciarProcessoAdocaoController {

    private final Navegador navegador;

    public TelaIniciarProcessoAdocaoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
