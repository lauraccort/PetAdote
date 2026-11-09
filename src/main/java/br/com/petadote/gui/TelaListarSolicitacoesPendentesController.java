package br.com.petadote.gui;

import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.servico.IProcessoAdocaoServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * Controller da TelaListarSolicitacoesPendentes.fxml (Solicitações pendentes).
 * Lista os processos "Em avaliação", do mais antigo ao mais novo,
 * e leva o escolhido para a avaliação prévia (REQ07).
 */
public class TelaListarSolicitacoesPendentesController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML private TableView<ProcessoAdocao> tabelaSolicitacoes;
    @FXML private TableColumn<ProcessoAdocao, String> colunaId;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAnimal;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAdotante;
    @FXML private TableColumn<ProcessoAdocao, String> colunaData;
    @FXML private Label mensagem;

    public TelaListarSolicitacoesPendentesController(Navegador navegador, IProcessoAdocaoServico processoServico) {
        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colunaAnimal.setCellValueFactory(c -> texto(c.getValue().getAnimal().getNome()));
        colunaAdotante.setCellValueFactory(c -> texto(c.getValue().getAdotante().getNome()));
        colunaData.setCellValueFactory(c -> texto(
                c.getValue().getDataAbertura() == null ? null : c.getValue().getDataAbertura().format(FORMATO)));

        tabelaSolicitacoes.setItems(FXCollections.observableArrayList(
                processoServico.listarTodos().stream()
                        .filter(p -> p.getStatus() == StatusProcesso.EM_AVALIACAO)
                        .filter(AbrigoLogado::pode)
                        .sorted(Comparator.comparing(ProcessoAdocao::getDataAbertura,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList()));

        // Some com o aviso "Selecione uma solicitação" assim que algo é selecionado
        tabelaSolicitacoes.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> mensagem.setText(""));
    }

    @FXML
    private void abrirHistoricoAdotante() {
        if (temSelecao()) {
            // A tela de histórico espera o adotante da solicitação
            navegador.ir("TelaHistoricoAdotante.fxml",
                    tabelaSolicitacoes.getSelectionModel().getSelectedItem().getAdotante());
        }
    }

    @FXML
    private void abrirAvaliarProcessoAdocao() {
        if (temSelecao()) {
            navegador.ir("TelaAvaliarProcessoAdocao.fxml",
                    tabelaSolicitacoes.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private boolean temSelecao() {
        if (tabelaSolicitacoes.getSelectionModel().getSelectedItem() == null) {
            mensagem.setText("Selecione uma solicitação na tabela.");
            return false;
        }
        return true;
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
