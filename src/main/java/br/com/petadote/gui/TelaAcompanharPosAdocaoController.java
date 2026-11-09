package br.com.petadote.gui;

import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.model.Visita;
import br.com.petadote.servico.IProcessoAdocaoServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * Controller da TelaAcompanharPosAdocao.fxml (Acompanhamento pós-adoção).
 * Lista os processos aprovados/concluídos, mostra as visitas do processo
 * selecionado e permite agendar novas visitas (REQ09), indicando se o
 * mínimo de visitas nos próximos 90 dias já foi atingido (REQ18).
 */
public class TelaAcompanharPosAdocaoController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML private TableView<ProcessoAdocao> tabelaProcessos;
    @FXML private TableColumn<ProcessoAdocao, String> colunaProcessoId;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAnimal;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAdotante;
    @FXML private TableColumn<ProcessoAdocao, String> colunaStatus;
    @FXML private TableColumn<ProcessoAdocao, String> colunaVisitasPeriodo;

    @FXML private Label tituloVisitas;
    @FXML private TableView<Visita> tabelaVisitas;
    @FXML private TableColumn<Visita, String> colunaData;
    @FXML private TableColumn<Visita, String> colunaRealizada;
    @FXML private TableColumn<Visita, String> colunaSituacao;
    @FXML private TableColumn<Visita, String> colunaObservacoes;

    @FXML private DatePicker campoDataVisita;
    @FXML private Button botaoAgendar;
    @FXML private Label situacaoMinimo;
    @FXML private Label mensagem;

    public TelaAcompanharPosAdocaoController(Navegador navegador, IProcessoAdocaoServico processoServico) {
        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        colunaProcessoId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colunaAnimal.setCellValueFactory(c -> texto(c.getValue().getAnimal().getNome()));
        colunaAdotante.setCellValueFactory(c -> texto(c.getValue().getAdotante().getNome()));
        colunaStatus.setCellValueFactory(c -> texto(c.getValue().getStatus().getDescricao()));
        colunaVisitasPeriodo.setCellValueFactory(c -> texto(
                c.getValue().contarVisitasNoPeriodo() + " de " + ProcessoAdocao.getMinVisitasPosAdocao()));

        colunaData.setCellValueFactory(c -> texto(c.getValue().getData().format(FORMATO)));
        colunaRealizada.setCellValueFactory(c -> texto(c.getValue().isConcluida() ? "Sim" : "Não"));
        colunaSituacao.setCellValueFactory(c -> texto(c.getValue().getSituacaoAnimal()));
        colunaObservacoes.setCellValueFactory(c -> texto(c.getValue().getObservacoes()));

        tabelaProcessos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, processo) -> mostrarVisitas(processo));

        atualizarProcessos();
        mostrarVisitas(null);
    }

    /** REQ09: agenda uma visita para o processo selecionado. */
    @FXML
    private void agendarVisita() {
        ProcessoAdocao processo = tabelaProcessos.getSelectionModel().getSelectedItem();
        if (processo == null) {
            erro("Selecione um processo.");
            return;
        }
        LocalDate data = campoDataVisita.getValue();
        try {
            Visita visita = new Visita(data);
            processoServico.agendarVisita(processo.getId(), visita);
            // Visita marcada pelo próprio abrigo já nasce aprovada
            processoServico.confirmarVisita(processo.getId(), visita.getId(), null);
            campoDataVisita.setValue(null);
            tabelaProcessos.refresh(); // atualiza a coluna "Visitas nos próx. 90 dias"
            mostrarVisitas(processo);
            sucesso("Visita agendada para " + data.format(FORMATO) + ".");
        } catch (IllegalArgumentException | IllegalStateException e) {
            erro(e.getMessage());
        }
    }

    private void atualizarProcessos() {
        tabelaProcessos.setItems(FXCollections.observableArrayList(
                processoServico.listarTodos().stream()
                        .filter(p -> p.getStatus() == StatusProcesso.APROVADO
                                || p.getStatus() == StatusProcesso.CONCLUIDO)
                        .filter(AbrigoLogado::pode)
                        .toList()));
    }

    private void mostrarVisitas(ProcessoAdocao processo) {
        mensagem.setText("");
        campoDataVisita.setDisable(processo == null);
        botaoAgendar.setDisable(processo == null);
        if (processo == null) {
            tituloVisitas.setText("Visitas do processo selecionado");
            tabelaVisitas.getItems().clear();
            situacaoMinimo.setText("");
            return;
        }
        tituloVisitas.setText("Visitas do processo " + processo.getId()
                + " (" + processo.getAnimal().getNome() + " → " + processo.getAdotante().getNome() + ")");
        tabelaVisitas.setItems(FXCollections.observableArrayList(
                processo.getVisitas().stream().sorted(Comparator.comparing(Visita::getData)).toList()));

        // REQ18: mínimo de visitas nos próximos 90 dias
        long noPeriodo = processo.contarVisitasNoPeriodo();
        int minimo = ProcessoAdocao.getMinVisitasPosAdocao();
        if (processo.temVisitasSuficientes()) {
            situacaoMinimo.setTextFill(COR_SUCESSO);
            situacaoMinimo.setText("Mínimo de " + minimo + " visitas em 90 dias atingido");
        } else {
            situacaoMinimo.setTextFill(COR_ERRO);
            situacaoMinimo.setText("Faltam " + (minimo - noPeriodo)
                    + " visita(s) aprovada(s) nos próximos 90 dias");
        }
    }

    @FXML
    private void abrirListarVisitas() {
        navegador.ir("TelaListarVisitas.fxml");
    }

    @FXML
    private void abrirPrincipalAbrigo() {
        navegador.ir("TelaPrincipalAbrigo.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }

    private void sucesso(String texto) {
        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText(texto);
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
