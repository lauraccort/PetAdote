package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaCadastroAnimal.fxml (Cadastro e edição de animal).
 */
public class TelaCadastroAnimalController {

    private final Navegador navegador;

    public TelaCadastroAnimalController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
