package br.com.petadote.gui;

import br.com.petadote.model.Visita;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller da TelaConsultarResultadoVisita.fxml (REQ10: resultado da visita).
 * Mostra ao adotante a visita escolhida em "Minhas visitas": a situação do animal e
 * as observações registradas pelo abrigo, ou o que falta se a visita ainda não aconteceu.
 */
public class TelaConsultarResultadoVisitaController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;

    @FXML private TextField campoStatus;
    @FXML private TextField campoResultado;
    @FXML private Label infoVisita;
    @FXML private Label mensagem;

    public TelaConsultarResultadoVisitaController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        Optional<VisitaDoProcesso> escolhida = navegador.selecionado(VisitaDoProcesso.class);
        if (escolhida.isEmpty()) {
            mensagem.setText("Nenhuma visita selecionada. Volte e escolha uma visita em \"Minhas visitas\".");
            infoVisita.setText("");
            return;
        }
        VisitaDoProcesso vp = escolhida.get();
        Visita visita = vp.visita();
        String data = visita.getData().format(FORMATO);
        campoStatus.setText(vp.status() + " — visita " + (visita.getId() == null ? "" : visita.getId() + " ")
                + "de " + data + " (" + vp.processo().getAnimal().getNome() + ")");

        if (visita.isConcluida()) {
            campoResultado.setText("Situação do animal: " + textoOuTraco(visita.getSituacaoAnimal()));
            infoVisita.setText("Observações do abrigo: " + textoOuTraco(visita.getObservacoes()));
        } else if (visita.isCancelada()) {
            campoResultado.setText("A visita foi cancelada pelo abrigo.");
            infoVisita.setText("Motivo: " + textoOuTraco(visita.getObservacaoAgendamento()));
        } else {
            campoResultado.setText("A visita ainda não foi realizada; o resultado aparece aqui depois dela.");
            String instrucoes = visita.getObservacaoAgendamento();
            infoVisita.setText("Data marcada: " + data
                    + (instrucoes == null || instrucoes.isBlank() ? "" : "  ·  Instruções do abrigo: " + instrucoes));
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private static String textoOuTraco(String texto) {
        return texto == null || texto.isBlank() ? "—" : texto;
    }
}
