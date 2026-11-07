package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.servico.IAbrigoServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import java.time.format.DateTimeFormatter;

/**
 * Controller da TelaAprovarCadastroAbrigo.fxml (Aprovar cadastro de abrigo).
 * O administrador escolhe um pedido pendente (na tabela ou no combo) e aprova ou nega.
 */
public class TelaAprovarCadastroAbrigoController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final IAbrigoServico abrigoServico;

    @FXML private TableView<Abrigo> tabelaPedidos;
    @FXML private TableColumn<Abrigo, String> colunaNome;
    @FXML private TableColumn<Abrigo, String> colunaEndereco;
    @FXML private TableColumn<Abrigo, String> colunaCapacidade;
    @FXML private TableColumn<Abrigo, String> colunaDataPedido;
    @FXML private ComboBox<Abrigo> campoAbrigo;
    @FXML private Button botaoAprovar;
    @FXML private Button botaoNegar;
    @FXML private Label mensagem;

    private final ObservableList<Abrigo> pendentes = FXCollections.observableArrayList();

    public TelaAprovarCadastroAbrigoController(Navegador navegador, IAbrigoServico abrigoServico) {
        this.navegador = navegador;
        this.abrigoServico = abrigoServico;
    }

    @FXML
    private void initialize() {
        colunaNome.setCellValueFactory(c -> texto(c.getValue().getNome()));
        colunaEndereco.setCellValueFactory(c -> texto(c.getValue().getEndereco()));
        colunaCapacidade.setCellValueFactory(c -> texto(c.getValue().getCapacidade()));
        colunaDataPedido.setCellValueFactory(c -> texto(
                c.getValue().getDataPedido() == null ? null : c.getValue().getDataPedido().format(FORMATO)));

        tabelaPedidos.setItems(pendentes);
        campoAbrigo.setItems(pendentes);
        campoAbrigo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Abrigo abrigo) {
                return abrigo == null ? "" : abrigo.getNome();
            }

            @Override
            public Abrigo fromString(String texto) {
                return null; // combo não é editável
            }
        });

        // Tabela e combo mostram o mesmo abrigo selecionado
        tabelaPedidos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, abrigo) -> {
            if (abrigo != campoAbrigo.getValue()) {
                campoAbrigo.setValue(abrigo);
            }
        });
        campoAbrigo.valueProperty().addListener((obs, anterior, abrigo) -> {
            if (abrigo != tabelaPedidos.getSelectionModel().getSelectedItem()) {
                tabelaPedidos.getSelectionModel().select(abrigo);
            }
            atualizarBotoes();
            if (abrigo != null) {
                mensagem.setText("");
            }
        });

        Combos.mostrarDicaQuandoVazio(campoAbrigo);
        carregarPendentes();
    }

    @FXML
    private void aprovar() {
        Abrigo abrigo = campoAbrigo.getValue();
        if (abrigo != null && abrigoServico.aprovar(abrigo.getId())) {
            concluir("Abrigo " + abrigo.getNome() + " aprovado. Ele já pode entrar no sistema.", COR_SUCESSO);
        } else {
            concluir("Não foi possível aprovar este pedido.", COR_ERRO);
        }
    }

    @FXML
    private void negar() {
        Abrigo abrigo = campoAbrigo.getValue();
        if (abrigo != null && abrigoServico.negar(abrigo.getId())) {
            concluir("Pedido do abrigo " + abrigo.getNome() + " negado.", COR_SUCESSO);
        } else {
            concluir("Não foi possível negar este pedido.", COR_ERRO);
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void carregarPendentes() {
        pendentes.setAll(abrigoServico.listarPendentes());
        campoAbrigo.setValue(null);
        atualizarBotoes();
    }

    /** Recarrega a lista (o pedido decidido sai dela) e mostra o resultado. */
    private void concluir(String texto, Color cor) {
        carregarPendentes();
        mensagem.setTextFill(cor);
        mensagem.setText(texto);
    }

    private void atualizarBotoes() {
        boolean semSelecao = campoAbrigo.getValue() == null;
        botaoAprovar.setDisable(semSelecao);
        botaoNegar.setDisable(semSelecao);
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
