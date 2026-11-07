package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarAnimaisDisponiveis.fxml (Animais disponíveis).
 */
public class TelaListarAnimaisDisponiveisController {

    private final Navegador navegador;

    public TelaListarAnimaisDisponiveisController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirDetalhesAnimal() {
        navegador.ir("TelaDetalhesAnimal.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
