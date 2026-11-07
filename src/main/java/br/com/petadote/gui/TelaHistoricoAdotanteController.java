package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaHistoricoAdotante.fxml (Histórico do adotante).
 */
public class TelaHistoricoAdotanteController {

    private final Navegador navegador;

    public TelaHistoricoAdotanteController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirListarSolicitacoesPendentes() {
        navegador.ir("TelaListarSolicitacoesPendentes.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
