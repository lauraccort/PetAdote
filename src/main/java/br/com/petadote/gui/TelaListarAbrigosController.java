package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.servico.PetAdoteFachada;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.util.Comparator;
import java.util.List;

/**
 * Controller da TelaListarAbrigos.fxml (Abrigos).
 * Lista os abrigos aprovados com ocupação; os pedidos pendentes ficam na tela de aprovação.
 */
public class TelaListarAbrigosController {

    private final Navegador navegador;
    private final PetAdoteFachada fachada;

    @FXML private TableView<Abrigo> tabela;
    @FXML private TableColumn<Abrigo, String> colId;
    @FXML private TableColumn<Abrigo, String> colNome;
    @FXML private TableColumn<Abrigo, String> colEndereco;
    @FXML private TableColumn<Abrigo, String> colCapacidade;
    @FXML private TableColumn<Abrigo, String> colAnimais;
    @FXML private TableColumn<Abrigo, String> colVagas;
    @FXML private Label total;
    @FXML private Label mensagem;

    public TelaListarAbrigosController(Navegador navegador, PetAdoteFachada fachada) {
        this.navegador = navegador;
        this.fachada = fachada;
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colNome.setCellValueFactory(c -> texto(c.getValue().getNome()));
        colEndereco.setCellValueFactory(c -> texto(c.getValue().getEndereco()));
        colCapacidade.setCellValueFactory(c -> texto(c.getValue().getCapacidade()));
        colAnimais.setCellValueFactory(c -> texto(c.getValue().getQuantidadeAnimais()));
        colVagas.setCellValueFactory(c -> texto(c.getValue().getCapacidade() - c.getValue().getQuantidadeAnimais()));

        carregarAbrigos();
    }

    private void carregarAbrigos() {
        List<Abrigo> aprovados = fachada.listarAbrigos().stream()
                .filter(a -> a.getStatus() == StatusAbrigo.APROVADO)
                .sorted(Comparator.comparing(Abrigo::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
        tabela.setItems(FXCollections.observableArrayList(aprovados));
        int pendentes = fachada.listarAbrigosPendentes().size();
        total.setText((aprovados.size() == 1 ? "1 abrigo aprovado" : aprovados.size() + " abrigos aprovados")
                + (pendentes == 0 ? "" : "  ·  " + pendentes + (pendentes == 1 ? " pedido pendente" : " pedidos pendentes")));
    }

    @FXML
    private void editarAbrigo() {
        Abrigo abrigo = tabela.getSelectionModel().getSelectedItem();
        if (abrigo == null) {
            mensagem.setText("Selecione um abrigo na tabela.");
            return;
        }

        TextField nome = new TextField(abrigo.getNome());
        TextField endereco = new TextField(abrigo.getEndereco());
        TextField capacidade = new TextField(Integer.toString(abrigo.getCapacidade()));
        DialogoEdicao dialogo = new DialogoEdicao(tabela.getScene().getWindow(), "Editar abrigo");
        dialogo.adicionarCampo("Nome", nome);
        dialogo.adicionarCampo("Endereço", endereco);
        dialogo.adicionarCampo("Capacidade", capacidade);

        if (dialogo.editar(() -> {
            int vagas;
            try {
                vagas = Integer.parseInt(capacidade.getText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Informe uma capacidade inteira.", e);
            }
            fachada.atualizarAbrigo(
                    abrigo.getId(), nome.getText().trim(), endereco.getText().trim(), vagas);
        })) {
            carregarAbrigos();
            mensagem.setText("Abrigo atualizado com sucesso.");
        }
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
