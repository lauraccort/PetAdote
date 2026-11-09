package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Adotante;
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
 * Controller da TelaListarMinhasVisitas.fxml (Minhas visitas).
 * Lista as visitas de acompanhamento (REQ09) dos processos do adotante logado
 * e leva a escolhida para a consulta do resultado (REQ10).
 */
public class TelaListarMinhasVisitasController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private final Contexto contexto;

    @FXML private TableView<VisitaDoProcesso> tabelaMinhasVisitas;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaId;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaAnimal;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaData;
    @FXML private TableColumn<VisitaDoProcesso, String> colunaStatus;
    @FXML private Label mensagem;

    public TelaListarMinhasVisitasController(Navegador navegador, Contexto contexto) {
        this.navegador = navegador;
        this.contexto = contexto;
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> texto(c.getValue().visita().getId()));
        colunaAnimal.setCellValueFactory(c -> texto(c.getValue().processo().getAnimal().getNome()));
        colunaData.setCellValueFactory(c -> texto(c.getValue().visita().getData().format(FORMATO)));
        colunaStatus.setCellValueFactory(c -> texto(c.getValue().status()));

        Optional<Adotante> adotante = contexto.adotanteLogado();
        if (adotante.isEmpty()) {
            mensagem.setText("Entre como adotante para ver as suas visitas.");
            return;
        }
        tabelaMinhasVisitas.setItems(FXCollections.observableArrayList(
                VisitaDoProcesso.de(contexto.processoServico().listarPorAdotante(adotante.get().getId()))
                        .sorted(Comparator.comparing(v -> v.visita().getData()))
                        .toList()));
        tabelaMinhasVisitas.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> mensagem.setText(""));
    }

    @FXML
    private void abrirAgendarVisita() {
        // Leva a visita selecionada (se houver), para a tela saber de qual processo se trata
        navegador.ir("TelaAgendarVisita.fxml", tabelaMinhasVisitas.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void abrirConsultarResultadoVisita() {
        VisitaDoProcesso escolhida = tabelaMinhasVisitas.getSelectionModel().getSelectedItem();
        if (escolhida == null) {
            mensagem.setText("Selecione uma visita na tabela.");
            return;
        }
        navegador.ir("TelaConsultarResultadoVisita.fxml", escolhida);
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
