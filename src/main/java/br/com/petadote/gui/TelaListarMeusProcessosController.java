package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.ProcessoAdocao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

/**
 * Controller da TelaListarMeusProcessos.fxml (Meus processos).
 * Lista os processos do adotante logado e leva o selecionado para o parecer.
 * REQ05: histórico de processos do adotante (visão do próprio adotante).
 */
public class TelaListarMeusProcessosController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private final Contexto contexto;

    @FXML private TableView<ProcessoAdocao> tabelaMeusProcessos;
    @FXML private TableColumn<ProcessoAdocao, String> colunaId;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAnimal;
    @FXML private TableColumn<ProcessoAdocao, String> colunaData;
    @FXML private TableColumn<ProcessoAdocao, String> colunaStatus;
    @FXML private Label mensagem;

    public TelaListarMeusProcessosController(Navegador navegador, Contexto contexto) {
        this.navegador = navegador;
        this.contexto = contexto;
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colunaAnimal.setCellValueFactory(c -> texto(c.getValue().getAnimal().getNome()));
        colunaData.setCellValueFactory(c -> texto(
                c.getValue().getDataAbertura() == null ? null : c.getValue().getDataAbertura().format(FORMATO)));
        colunaStatus.setCellValueFactory(c -> texto(c.getValue().getStatus().getDescricao()));

        Optional<Adotante> adotante = contexto.adotanteLogado();
        if (adotante.isEmpty()) {
            mensagem.setText("Entre como adotante para ver os seus processos.");
            return;
        }
        tabelaMeusProcessos.setItems(FXCollections.observableArrayList(
                contexto.processoServico().listarPorAdotante(adotante.get().getId()).stream()
                        .sorted(Comparator.comparing(ProcessoAdocao::getId).reversed()) // mais recente primeiro
                        .toList()));
        tabelaMeusProcessos.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> mensagem.setText(""));
    }

    @FXML
    private void abrirConsultarParecer() {
        ProcessoAdocao processo = tabelaMeusProcessos.getSelectionModel().getSelectedItem();
        if (processo == null) {
            mensagem.setText("Selecione um processo na tabela.");
            return;
        }
        navegador.ir("TelaConsultarParecer.fxml", processo);
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
