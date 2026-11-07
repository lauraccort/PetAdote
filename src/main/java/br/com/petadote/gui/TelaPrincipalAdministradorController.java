package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaPrincipalAdministrador.fxml (Principal - Administrador).
 */
public class TelaPrincipalAdministradorController {

    private final Navegador navegador;

    public TelaPrincipalAdministradorController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirRelatorios() {
        navegador.ir("TelaRelatorios.fxml");
    }

    @FXML
    private void abrirListarAbrigos() {
        navegador.ir("TelaListarAbrigos.fxml");
    }

    @FXML
    private void sair() {
        navegador.sair();
    }
}
