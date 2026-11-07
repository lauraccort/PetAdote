package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaPrincipalAdotante.fxml (Principal - Adotante).
 */
public class TelaPrincipalAdotanteController {

    private final Navegador navegador;

    public TelaPrincipalAdotanteController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirListarAnimaisDisponiveis() {
        navegador.ir("TelaListarAnimaisDisponiveis.fxml");
    }

    @FXML
    private void abrirListarMeusProcessos() {
        navegador.ir("TelaListarMeusProcessos.fxml");
    }

    @FXML
    private void abrirListarMinhasVisitas() {
        navegador.ir("TelaListarMinhasVisitas.fxml");
    }

    @FXML
    private void abrirResponderAcompanhamento() {
        navegador.ir("TelaResponderAcompanhamento.fxml");
    }

    @FXML
    private void sair() {
        navegador.sair();
    }
}
