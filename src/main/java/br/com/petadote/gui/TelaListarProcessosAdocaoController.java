package br.com.petadote.gui;

import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.servico.IProcessoAdocaoServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.Comparator;

/**
 * Controller da TelaListarProcessosAdocao.fxml (Processos de adoção).
 * Lista todos os processos e leva às telas de solicitações pendentes,
 * finalizar processo e acompanhamento pós-adoção.
 */
public class TelaListarProcessosAdocaoController {

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML private TableView<ProcessoAdocao> tabelaProcessos;
    @FXML private TableColumn<ProcessoAdocao, String> colunaId;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAnimal;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAdotante;
    @FXML private TableColumn<ProcessoAdocao, String> colunaStatus;
    @FXML private Label mensagem;

    public TelaListarProcessosAdocaoController(Navegador navegador, IProcessoAdocaoServico processoServico) {
        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colunaAnimal.setCellValueFactory(c -> texto(c.getValue().getAnimal().getNome()));
        colunaAdotante.setCellValueFactory(c -> texto(c.getValue().getAdotante().getNome()));
        colunaStatus.setCellValueFactory(c -> texto(c.getValue().getStatus().getDescricao()));

        tabelaProcessos.setItems(FXCollections.observableArrayList(
                processoServico.listarTodos().stream()
                        .sorted(Comparator.comparing(ProcessoAdocao::getId))
                        .toList()));
    }

    @FXML
    private void abrirListarSolicitacoesPendentes() {
        navegador.ir("TelaListarSolicitacoesPendentes.fxml");
    }

    @FXML
    private void abrirFinalizarProcessoAdocao() {
        navegador.ir("TelaFinalizarProcessoAdocao.fxml");
    }

    @FXML
    private void abrirAcompanharPosAdocao() {
        navegador.ir("TelaAcompanharPosAdocao.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
