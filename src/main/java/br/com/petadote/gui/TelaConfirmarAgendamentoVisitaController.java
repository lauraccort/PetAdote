package br.com.petadote.gui;

import javafx.fxml.FXML;

/**
 * Controller da TelaConfirmarAgendamentoVisita.fxml (Confirmar agendamento de visita).
 */
public class TelaConfirmarAgendamentoVisitaController {

    private final Navegador navegador;

    public TelaConfirmarAgendamentoVisitaController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
