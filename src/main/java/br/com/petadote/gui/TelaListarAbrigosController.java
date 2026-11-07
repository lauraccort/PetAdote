package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.servico.IAbrigoServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.Comparator;
import java.util.List;

/**
 * Controller da TelaListarAbrigos.fxml (Abrigos).
 * Lista os abrigos aprovados com ocupação; os pedidos pendentes ficam na tela de aprovação.
 */
public class TelaListarAbrigosController {

    private final Navegador navegador;
    private final IAbrigoServico abrigoServico;

    @FXML private TableView<Abrigo> tabela;
    @FXML private TableColumn<Abrigo, String> colId;
    @FXML private TableColumn<Abrigo, String> colNome;
    @FXML private TableColumn<Abrigo, String> colEndereco;
    @FXML private TableColumn<Abrigo, String> colCapacidade;
    @FXML private TableColumn<Abrigo, String> colAnimais;
    @FXML private TableColumn<Abrigo, String> colVagas;
    @FXML private Label total;

    public TelaListarAbrigosController(Navegador navegador, IAbrigoServico abrigoServico) {
        this.navegador = navegador;
        this.abrigoServico = abrigoServico;
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colNome.setCellValueFactory(c -> texto(c.getValue().getNome()));
        colEndereco.setCellValueFactory(c -> texto(c.getValue().getEndereco()));
        colCapacidade.setCellValueFactory(c -> texto(c.getValue().getCapacidade()));
        colAnimais.setCellValueFactory(c -> texto(c.getValue().getQuantidadeAnimais()));
        colVagas.setCellValueFactory(c -> texto(c.getValue().getCapacidade() - c.getValue().getQuantidadeAnimais()));

        List<Abrigo> aprovados = abrigoServico.listarTodos().stream()
                .filter(a -> a.getStatus() == StatusAbrigo.APROVADO)
                .sorted(Comparator.comparing(Abrigo::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
        tabela.setItems(FXCollections.observableArrayList(aprovados));
        int pendentes = abrigoServico.listarPendentes().size();
        total.setText((aprovados.size() == 1 ? "1 abrigo aprovado" : aprovados.size() + " abrigos aprovados")
                + (pendentes == 0 ? "" : "  ·  " + pendentes + (pendentes == 1 ? " pedido pendente" : " pedidos pendentes")));
    }

    @FXML
    private void abrirAprovarCadastroAbrigo() {
        navegador.ir("TelaAprovarCadastroAbrigo.fxml");
    }

    /** Leva o abrigo selecionado (se houver) para a tela de exclusão já preenchida. */
    @FXML
    private void abrirExcluirAbrigo() {
        navegador.ir("TelaExcluirAbrigo.fxml", tabela.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
