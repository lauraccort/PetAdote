package br.com.petadote.gui;

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
 * Controller da TelaListarVisitas.fxml (Painel de visitas).
 * Lista as visitas de acompanhamento de todos os processos, por data.
 */
public class TelaListarVisitasController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML private TableView<VisitaDoProcesso> tabelaVisitas;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaNumero;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaDataHora;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaAnimal;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaAdotante;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaStatus;
    @FXML private Label mensagem;

    public TelaListarVisitasController(Navegador navegador, IProcessoAdocaoServico processoServico) {
        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        colunaNumero.setCellValueFactory(c -> texto(c.getValue().visita().getId()));
        colunaDataHora.setCellValueFactory(c -> texto(c.getValue().visita().getData().format(FORMATO)));
        colunaAnimal.setCellValueFactory(c -> texto(c.getValue().processo().getAnimal().getNome()));
        colunaAdotante.setCellValueFactory(c -> texto(c.getValue().processo().getAdotante().getNome()));
        colunaStatus.setCellValueFactory(c -> texto(c.getValue().status()));

        tabelaVisitas.setItems(FXCollections.observableArrayList(
                VisitaDoProcesso.de(processoServico.listarTodos())
                        .sorted(Comparator.comparing(l -> l.visita().getData()))
                        .toList()));

        // Some com o aviso "Selecione uma visita" assim que algo é selecionado
        tabelaVisitas.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> mensagem.setText(""));
    }

    @FXML
    private void abrirAvaliarVisita() {
        if (temSelecao()) {
            navegador.ir("TelaAvaliarVisita.fxml");
        }
    }

    @FXML
    private void abrirConfirmarAgendamentoVisita() {
        if (temSelecao()) {
            navegador.ir("TelaConfirmarAgendamentoVisita.fxml");
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private boolean temSelecao() {
        if (tabelaVisitas.getSelectionModel().getSelectedItem() == null) {
            mensagem.setText("Selecione uma visita na tabela.");
            return false;
        }
        return true;
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
