
package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;
import br.com.petadote.servico.IProcessoAdocaoServico;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TelaConfirmarAgendamentoVisitaController {

    private static final String CONFIRMADA = "Confirmada";
    private static final String REAGENDADA = "Reagendada";
    private static final String CANCELADA = "Cancelada";

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML private TextField campoIdVisita;
    @FXML private Label infoVisita;
    @FXML private ComboBox<String> campoStatusVisita;
    @FXML private Node grupoNovaData;
    @FXML private DatePicker campoNovaData;
    @FXML private TextField campoObservacoes;
    @FXML private Label mensagem;

    // Visita escolhida no painel; tem prioridade sobre a busca pelo número
    private VisitaDoProcesso visitaSelecionada;

    public TelaConfirmarAgendamentoVisitaController(
            Navegador navegador,
            IProcessoAdocaoServico processoServico) {

        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        campoStatusVisita.getItems().addAll(
                CONFIRMADA,
                REAGENDADA,
                CANCELADA
        );

        Combos.mostrarDicaQuandoVazio(campoStatusVisita);

        grupoNovaData.managedProperty().bind(grupoNovaData.visibleProperty());

        grupoNovaData.visibleProperty().bind(
                campoStatusVisita.valueProperty().isEqualTo(REAGENDADA)
        );

        campoIdVisita.textProperty().addListener(
                (obs, anterior, texto) -> mostrarInfo()
        );

        visitaSelecionada = navegador.selecionado(VisitaDoProcesso.class)
                .filter(vp -> vp.visita().getId() != null)
                .orElse(null);

        if (visitaSelecionada != null) {
            campoIdVisita.setText(String.valueOf(visitaSelecionada.visita().getId()));
        }

        mostrarInfo();
    }

    @FXML
    private void confirmarAgendamento() {
        Optional<VisitaDoProcesso> encontrada = buscarVisita();

        if (encontrada.isEmpty()) {
            erro("Informe o número de uma visita existente (coluna Nº do painel de visitas).");
            return;
        }

        Visita visita = encontrada.get().visita();

        if (!visita.podeAlterarAgendamento()) {
            erro(visita.isConcluida()
                    ? "Esta visita já foi realizada."
                    : "Esta visita foi cancelada.");
            return;
        }

        String decisao = campoStatusVisita.getValue();

        if (decisao == null) {
            erro("Selecione o status da confirmação.");
            return;
        }

        String observacoes = campoObservacoes.getText().trim();
        String obs = observacoes.isEmpty() ? null : observacoes;

        String resultado;

        switch (decisao) {
            case REAGENDADA -> {
                LocalDate novaData = campoNovaData.getValue();

                if (novaData == null || novaData.isBefore(LocalDate.now())) {
                    erro("Informe uma nova data a partir de hoje.");
                    return;
                }

                if (!processoServico.reagendarVisita(
                        encontrada.get().processo().getId(),
                        visita.getId(),
                        novaData,
                        obs)) {

                    erro("Não foi possível reagendar esta visita.");
                    return;
                }

                resultado = "Visita " + visita.getId()
                        + " reagendada para " + novaData.format(FORMATO) + ".";
            }

            case CANCELADA -> {
                if (obs == null) {
                    erro("Explique o motivo do cancelamento em \"Observações para o Adotante\".");
                    return;
                }

                if (!processoServico.cancelarVisita(
                        encontrada.get().processo().getId(),
                        visita.getId(),
                        obs)) {

                    erro("Não foi possível cancelar esta visita.");
                    return;
                }

                resultado = "Visita " + visita.getId()
                        + " cancelada. Ela não conta mais para as 2 visitas obrigatórias;"
                        + " o adotante poderá pedir outra data.";
            }

            default -> {
                if (!processoServico.confirmarVisita(
                        encontrada.get().processo().getId(),
                        visita.getId(),
                        obs)) {

                    erro("Não foi possível confirmar esta visita.");
                    return;
                }

                resultado = "Visita " + visita.getId()
                        + " confirmada para " + visita.getData().format(FORMATO) + ".";
            }
        }

        visitaSelecionada = null;
        campoIdVisita.clear();
        campoStatusVisita.setValue(null);
        campoNovaData.setValue(null);
        campoObservacoes.clear();

        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText(resultado);
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void mostrarInfo() {
        Optional<VisitaDoProcesso> encontrada = buscarVisita();

        if (campoIdVisita.getText().isBlank()) {
            infoVisita.setText("");
        } else if (encontrada.isEmpty()) {
            infoVisita.setText("Visita não encontrada.");
        } else {
            ProcessoAdocao processo = encontrada.get().processo();
            Visita visita = encontrada.get().visita();

            infoVisita.setText(
                    "Data: " + visita.getData().format(FORMATO)
                            + "  ·  Animal: " + processo.getAnimal().getNome()
                            + "  ·  Adotante: " + processo.getAdotante().getNome()
                            + "  ·  " + (visita.isConcluida()
                            ? "Realizada"
                            : visita.getStatusAgendamento().getDescricao())
            );
        }
    }

    private Optional<VisitaDoProcesso> buscarVisita() {
        long id;

        try {
            id = Long.parseLong(campoIdVisita.getText().trim());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        // Usa a própria visita escolhida no painel quando o número bate,
        // para não confundir com outra visita de mesmo número.
        if (visitaSelecionada != null
                && visitaSelecionada.visita().getId().equals(id)) {
            return Optional.of(visitaSelecionada);
        }

        return VisitaDoProcesso.de(processoServico.listarTodos())
                .filter(vp -> vp.visita().getId() != null
                        && vp.visita().getId().equals(id))
                .filter(AbrigoLogado::pode)
                .findFirst();
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}

