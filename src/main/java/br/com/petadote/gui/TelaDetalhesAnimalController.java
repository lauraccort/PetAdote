package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaDetalhesAnimal.fxml (Detalhes do animal).
 */
public class TelaDetalhesAnimalController {

    private final Navegador navegador;

    public TelaDetalhesAnimalController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirIniciarProcessoAdocao() {
        navegador.ir("TelaIniciarProcessoAdocao.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
