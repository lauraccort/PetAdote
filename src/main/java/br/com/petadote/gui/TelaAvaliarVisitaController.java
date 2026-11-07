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

/**
 * Controller da TelaAvaliarVisita.fxml (Avaliar visita).
 * "Realizada" registra o resultado da visita (REQ10: situação do animal e observações);
 * "Confirmada" e "Cancelada" mudam o agendamento.
 */
public class TelaAvaliarVisitaController {

    private static final String REALIZADA = "Realizada";
    private static final String CONFIRMADA = "Confirmada";
    private static final String CANCELADA = "Cancelada";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

    public TelaAvaliarVisitaController(Navegador navegador, IProcessoAdocaoServico processoServico) {
        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        campoSituacaoAnimal.getItems().addAll("Apto para adoção", "Requer cuidados");
        campoStatusVisita.getItems().addAll(REALIZADA, CONFIRMADA, CANCELADA);
        Combos.mostrarDicaQuandoVazio(campoSituacaoAnimal);
        Combos.mostrarDicaQuandoVazio(campoStatusVisita);
        // A situação do animal só faz sentido quando a visita foi realizada
        campoSituacaoAnimal.disableProperty().bind(campoStatusVisita.valueProperty().isNotEqualTo(REALIZADA));

        // A linha com os dados da visita só ocupa espaço quando tem texto
        infoVisita.managedProperty().bind(infoVisita.textProperty().isNotEmpty());
        campoIdProcesso.textProperty().addListener((obs, a, b) -> atualizar());
        campoIdVisita.textProperty().addListener((obs, a, b) -> atualizar());
        // Vindo do painel de visitas com uma visita selecionada, os IDs já vêm preenchidos
        navegador.selecionado(VisitaDoProcesso.class).ifPresent(vp -> {
            campoIdProcesso.setText(String.valueOf(vp.processo().getId()));
            campoIdVisita.setText(vp.visita().getId() == null ? "" : String.valueOf(vp.visita().getId()));
        });
        atualizar();
    }

    @FXML
    private void salvarAvaliacao() {
        Optional<VisitaDoProcesso> encontrada = buscarVisita();
        if (encontrada.isEmpty()) {
            erro(campoIdVisita.getText().isBlank() ? "Informe o ID da visita."
                    : "Visita não encontrada" + (campoIdProcesso.getText().isBlank() ? "." : " neste processo."));
            return;
        }
        Visita visita = encontrada.get().visita();
        if (!visita.podeAlterarAgendamento()) {
            erro(visita.isConcluida() ? "O resultado desta visita já foi registrado." : "Esta visita foi cancelada.");
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
                visita.registrarResultado(situacao, observacoes.isEmpty() ? null : observacoes); // REQ10
                resultado = "Resultado da visita " + visita.getId() + " registrado: " + situacao + ".";
            }
            case CANCELADA -> {
                if (observacoes.isEmpty()) {
                    erro("Explique o motivo do cancelamento em \"Observações e Avaliação\".");
                    return;
                }
                visita.cancelar(observacoes);
                resultado = "Visita " + visita.getId() + " cancelada.";
            }
            default -> {
                visita.confirmar(observacoes.isEmpty() ? null : observacoes);
                resultado = "Visita " + visita.getId() + " confirmada para " + visita.getData().format(FORMATO) + ".";
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

    /** Mostra de qual visita se trata e limpa mensagens antigas. */
    private void atualizar() {
        mensagem.setText("");
        Optional<VisitaDoProcesso> vp = buscarVisita();
        if (campoIdVisita.getText().isBlank()) {
            infoVisita.setText("");
        } else if (vp.isEmpty()) {
            infoVisita.setText("Visita não encontrada" + (campoIdProcesso.getText().isBlank() ? "." : " neste processo."));
        } else {
            ProcessoAdocao p = vp.get().processo();
            infoVisita.setText("Data: " + vp.get().visita().getData().format(FORMATO)
                    + "  ·  " + p.getAnimal().getNome() + " → " + p.getAdotante().getNome()
                    + "  ·  " + vp.get().status());
        }
    }

    /** Procura a visita pelo número; se o ID do processo foi informado, ela precisa ser dele. */
    private Optional<VisitaDoProcesso> buscarVisita() {
        Long idVisita = numero(campoIdVisita.getText());
        if (idVisita == null) {
            return Optional.empty();
        }
        Long idProcesso = numero(campoIdProcesso.getText());
        return VisitaDoProcesso.de(processoServico.listarTodos())
                .filter(vp -> idVisita.equals(vp.visita().getId()))
                .filter(vp -> idProcesso == null || idProcesso.equals(vp.processo().getId()))
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
