package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaFinalizarProcessoAdocao.fxml (Finalizar processo de adoção).
 */
public class TelaFinalizarProcessoAdocaoController {

    private final Navegador navegador;

    public TelaFinalizarProcessoAdocaoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirAcompanharPosAdocao() {
        navegador.ir("TelaAcompanharPosAdocao.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
