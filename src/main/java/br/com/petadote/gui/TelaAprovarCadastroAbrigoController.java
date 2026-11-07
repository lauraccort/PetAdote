package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaAprovarCadastroAbrigo.fxml (Aprovar cadastro de abrigo).
 */
public class TelaAprovarCadastroAbrigoController {

    private final Navegador navegador;

    public TelaAprovarCadastroAbrigoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
