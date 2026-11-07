package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarProcessosAdocao.fxml (Processos de adoção).
 */
public class TelaListarProcessosAdocaoController {

    private final Navegador navegador;

    public TelaListarProcessosAdocaoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirListarSolicitacoesPendentes() {
        navegador.ir("TelaListarSolicitacoesPendentes.fxml");
    }

    @FXML
    private void abrirFinalizarProcessoAdocao() {
        navegador.ir("TelaFinalizarProcessoAdocao.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
