package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaListarAnimaisAbrigo.fxml (Animais do abrigo).
 */
public class TelaListarAnimaisAbrigoController {

    private final Navegador navegador;

    public TelaListarAnimaisAbrigoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirCadastroAnimal() {
        navegador.ir("TelaCadastroAnimal.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
