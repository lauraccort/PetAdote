
package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Adotante;
import br.com.petadote.servico.PetAdoteFachada;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class TelaListarAdotantesController {

    private final Navegador navegador;
    private final PetAdoteFachada fachada;

    @FXML
    private TableView<Adotante> tabelaAdotantes;

    @FXML
    private TableColumn<Adotante, Long> colunaId;

    @FXML
    private TableColumn<Adotante, String> colunaNome;

    @FXML
    private TableColumn<Adotante, String> colunaEmail;

    @FXML
    private TableColumn<Adotante, String> colunaCpf;

    @FXML
    private Label mensagem;

    @FXML private Button botaoEditar;
    @FXML private Button botaoExcluir;

    public TelaListarAdotantesController(Navegador navegador) {
        this.navegador = navegador;
        this.fachada = Contexto.get().fachada();
    }

    @FXML
    private void initialize() {

        colunaId.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        colunaNome.setCellValueFactory(
                new PropertyValueFactory<>("nome"));

        colunaEmail.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        colunaCpf.setCellValueFactory(
                new PropertyValueFactory<>("cpf"));

        tabelaAdotantes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, atual) ->
                        mensagem.setText(""));

        boolean administrador = podeGerenciarCadastros();
        botaoEditar.setVisible(administrador);
        botaoEditar.setManaged(administrador);
        botaoExcluir.setVisible(administrador);
        botaoExcluir.setManaged(administrador);
        carregarAdotantes();
    }

    private boolean podeGerenciarCadastros() {
        Contexto contexto = Contexto.get();
        return contexto.abrigoLogado().isEmpty() && contexto.adotanteLogado().isEmpty();
    }

    private void carregarAdotantes() {
        try {
            tabelaAdotantes.setItems(FXCollections.observableArrayList(fachada.listarAdotantes()));
        } catch (Exception e) {
            mensagem.setText(
                    "Erro ao carregar adotantes: " + e.getMessage());
        }
    }

    @FXML
    private void abrirHistoricoAdotante() {

        Adotante selecionado = tabelaAdotantes
                .getSelectionModel()
                .getSelectedItem();

        if (selecionado != null) {

            mensagem.setText("");

            // Envia o adotante selecionado para a próxima tela.
            navegador.ir("TelaHistoricoAdotante.fxml", selecionado);

        } else {
            mensagem.setText("Selecione um adotante na tabela.");
        }
    }

    @FXML
    private void editarAdotante() {
        if (!podeGerenciarCadastros()) {
            return;
        }
        Adotante adotante = tabelaAdotantes.getSelectionModel().getSelectedItem();
        if (adotante == null) {
            mensagem.setText("Selecione um adotante na tabela.");
            return;
        }

        TextField nome = new TextField(adotante.getNome());
        TextField endereco = new TextField(adotante.getEndereco());
        DialogoEdicao dialogo = new DialogoEdicao(
                tabelaAdotantes.getScene().getWindow(), "Editar adotante");
        dialogo.adicionarCampo("Nome", nome);
        dialogo.adicionarCampo("Endereço", endereco);

        if (dialogo.editar(() -> fachada.atualizarAdotante(
                adotante.getId(), nome.getText().trim(), endereco.getText().trim()))) {
            carregarAdotantes();
            mensagem.setText("Adotante atualizado com sucesso.");
        }
    }

    @FXML
    private void excluirAdotante() {
        if (!podeGerenciarCadastros()) {
            return;
        }
        Adotante adotante = tabelaAdotantes.getSelectionModel().getSelectedItem();
        if (adotante == null) {
            mensagem.setText("Selecione um adotante na tabela.");
            return;
        }

        ButtonType excluir = new ButtonType("Excluir", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja excluir o cadastro de " + adotante.getNome() + "?", cancelar, excluir);
        confirmacao.initOwner(tabelaAdotantes.getScene().getWindow());
        confirmacao.setTitle("Excluir adotante");
        confirmacao.setHeaderText(null);
        if (confirmacao.showAndWait().orElse(cancelar) != excluir) {
            return;
        }

        if (!fachada.excluirAdotante(adotante.getId())) {
            mensagem.setText("Só é possível excluir adotantes sem histórico de adoção.");
            return;
        }
        carregarAdotantes();
        mensagem.setText("Adotante excluído com sucesso.");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}

