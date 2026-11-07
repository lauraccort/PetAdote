package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarSolicitacoesPendentes.fxml (Solicitações pendentes).
 */
public class TelaListarSolicitacoesPendentesController {

    private final Navegador navegador;

    public TelaListarSolicitacoesPendentesController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirHistoricoAdotante() {
        navegador.ir("TelaHistoricoAdotante.fxml");
    }

    @FXML
    private void abrirAvaliarProcessoAdocao() {
        navegador.ir("TelaAvaliarProcessoAdocao.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
