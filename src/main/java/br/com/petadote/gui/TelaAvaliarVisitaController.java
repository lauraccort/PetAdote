
package br.com.petadote.gui;

import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;
import br.com.petadote.servico.IProcessoAdocaoServico;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TelaAvaliarVisitaController {

    private static final String REALIZADA = "Realizada";
    private static final String CONFIRMADA = "Confirmada";
    private static final String CANCELADA = "Cancelada";

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML private TextField campoIdProcesso;
    @FXML private TextField campoIdVisita;
    @FXML private Label infoVisita;
    @FXML private ComboBox<String> campoSituacaoAnimal;
    @FXML private ComboBox<String> campoStatusVisita;
    @FXML private TextField campoObservacoes;
    @FXML private Label mensagem;

    public TelaAvaliarVisitaController(
            Navegador navegador,
            IProcessoAdocaoServico processoServico) {

        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        campoSituacaoAnimal.getItems().addAll(
                "Apto para adoção",
                "Requer cuidados"
        );

        campoStatusVisita.getItems().addAll(
                REALIZADA,
                CONFIRMADA,
                CANCELADA
        );

        Combos.mostrarDicaQuandoVazio(campoSituacaoAnimal);
        Combos.mostrarDicaQuandoVazio(campoStatusVisita);

        campoSituacaoAnimal.disableProperty().bind(
                campoStatusVisita.valueProperty().isNotEqualTo(REALIZADA)
        );

        infoVisita.managedProperty().bind(
                infoVisita.textProperty().isNotEmpty()
        );

        campoIdProcesso.textProperty().addListener(
                (obs, a, b) -> atualizar()
        );

        campoIdVisita.textProperty().addListener(
                (obs, a, b) -> atualizar()
        );

        navegador.selecionado(VisitaDoProcesso.class).ifPresent(vp -> {
            campoIdProcesso.setText(
                    String.valueOf(vp.processo().getId())
            );

            campoIdVisita.setText(
                    vp.visita().getId() == null
                            ? ""
                            : String.valueOf(vp.visita().getId())
            );
        });

        atualizar();
    }

    @FXML
    private void salvarAvaliacao() {
        Optional<VisitaDoProcesso> encontrada = buscarVisita();

        if (encontrada.isEmpty()) {
            erro(campoIdVisita.getText().isBlank()
                    ? "Informe o ID da visita."
                    : "Visita não encontrada"
                    + (campoIdProcesso.getText().isBlank()
                    ? "."
                    : " neste processo."));
            return;
        }

        Visita visita = encontrada.get().visita();

        if (!visita.podeAlterarAgendamento()) {
            erro(visita.isConcluida()
                    ? "O resultado desta visita já foi registrado."
                    : "Esta visita foi cancelada.");
            return;
        }

        String status = campoStatusVisita.getValue();

        if (status == null) {
            erro("Selecione o status da visita.");
            return;
        }

        String observacoes = campoObservacoes.getText().trim();
        String resultado;

        switch (status) {
            case REALIZADA -> {
                String situacao = campoSituacaoAnimal.getValue();

                if (situacao == null) {
                    erro("Selecione a situação do animal.");
                    return;
                }

                processoServico.registrarResultadoVisita(
                        encontrada.get().processo().getId(),
                        visita.getId(),
                        situacao,
                        observacoes.isEmpty() ? null : observacoes
                );

                resultado = "Resultado da visita " + visita.getId()
                        + " registrado: " + situacao + ".";
            }

            case CANCELADA -> {
                if (observacoes.isEmpty()) {
                    erro("Explique o motivo do cancelamento em \"Observações e Avaliação\".");
                    return;
                }

                if (!processoServico.cancelarVisita(
                        encontrada.get().processo().getId(),
                        visita.getId(),
                        observacoes)) {

                    erro("Não foi possível cancelar esta visita.");
                    return;
                }

                resultado = "Visita " + visita.getId() + " cancelada.";
            }

            default -> {
                if (!processoServico.confirmarVisita(
                        encontrada.get().processo().getId(),
                        visita.getId(),
                        observacoes.isEmpty() ? null : observacoes)) {

                    erro("Não foi possível confirmar esta visita.");
                    return;
                }

                resultado = "Visita " + visita.getId()
                        + " confirmada para " + visita.getData().format(FORMATO) + ".";
            }
        }

        campoIdProcesso.clear();
        campoIdVisita.clear();
        campoStatusVisita.setValue(null);
        campoSituacaoAnimal.setValue(null);
        campoObservacoes.clear();

        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText(resultado);
    }

    @FXML
    private void gerarRelatorioAnimaisDisponiveis() {
        navegador.ir("TelaRelatorioAnimaisDisponiveis.fxml");
    }

    @FXML
    private void gerarRelatorioAdocoesConcluidas() {
        navegador.ir("TelaRelatorioAdocoesConcluidas.fxml");
    }

    @FXML
    private void gerarRelatorioProcessosPendentes() {
        navegador.ir("TelaRelatorioProcessosPendentes.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void atualizar() {
        mensagem.setText("");

        Optional<VisitaDoProcesso> vp = buscarVisita();

        if (campoIdVisita.getText().isBlank()) {
            infoVisita.setText("");
        } else if (vp.isEmpty()) {
            infoVisita.setText(
                    "Visita não encontrada"
                            + (campoIdProcesso.getText().isBlank()
                            ? "."
                            : " neste processo.")
            );
        } else {
            ProcessoAdocao processo = vp.get().processo();

            infoVisita.setText(
                    "Data: " + vp.get().visita().getData().format(FORMATO)
                            + "  ·  " + processo.getAnimal().getNome()
                            + " → " + processo.getAdotante().getNome()
                            + "  ·  " + vp.get().status()
            );
        }
    }

    private Optional<VisitaDoProcesso> buscarVisita() {
        Long idVisita = numero(campoIdVisita.getText());

        if (idVisita == null) {
            return Optional.empty();
        }

        Long idProcesso = numero(campoIdProcesso.getText());

        return VisitaDoProcesso.de(processoServico.listarTodos())
                .filter(vp -> idVisita.equals(vp.visita().getId()))
                .filter(vp -> idProcesso == null
                        || idProcesso.equals(vp.processo().getId()))
                .findFirst();
    }

    private static Long numero(String texto) {
        try {
            return Long.parseLong(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}
