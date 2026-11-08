package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;
import br.com.petadote.servico.IProcessoAdocaoServico;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TelaAgendarVisitaController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int DIAS_LIMITE = 90;

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico = Contexto.get().processoServico();

    @FXML private DatePicker campoData;
    @FXML private Label mensagem;

    public TelaAgendarVisitaController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        mensagem.setText("");
        if (campoData != null) {
            campoData.setValue(LocalDate.now().plusDays(7));
        }
    }

    /**
     * REQ09 + REQ18: agenda visita de acompanhamento no processo
     * concluido que ainda nao tenha as duas visitas exigidas.
     */
    @FXML
    private void agendar() {
        try {
            LocalDate data = campoData.getValue();
            validarData(data);

            Optional<ProcessoAdocao> alvo = processoQuePrecisaDeVisita();
            if (alvo.isEmpty()) {
                throw new IllegalArgumentException(
                        "Nenhum processo concluído precisa de visita no momento.");
            }

            ProcessoAdocao processo = alvo.get();
            Visita nova = new Visita(data);

            processoServico.agendarVisita(processo.getId(), nova);

            mensagem.setStyle("-fx-text-fill: #2e7d32;");
            mensagem.setText("Visita agendada para " + data.format(FORMATO)
                    + " no processo do animal " + processo.getAnimal().getNome() + ".");
            campoData.setValue(data.plusDays(7));

        } catch (IllegalArgumentException | IllegalStateException e) {
            mensagem.setStyle("-fx-text-fill: #c0392b;");
            mensagem.setText(e.getMessage());
        }
    }

    private void validarData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("Selecione a data da visita.");
        }
        if (data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data da visita não pode estar no passado.");
        }
        if (data.isAfter(LocalDate.now().plusDays(DIAS_LIMITE))) {
            throw new IllegalArgumentException(
                    "A visita deve ocorrer dentro dos " + DIAS_LIMITE + " dias seguintes.");
        }
    }


    private Optional<ProcessoAdocao> processoQuePrecisaDeVisita() {
        List<ProcessoAdocao> concluidos = processoServico.listarConcluidos();
        return concluidos.stream()
                .filter(p -> !p.temVisitasSuficientes())
                .findFirst();
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}