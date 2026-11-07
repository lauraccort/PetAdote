package br.com.petadote.gui;

import br.com.petadote.model.Adotante;
import br.com.petadote.servico.IAdotanteServico;
import br.com.petadote.servico.AdotanteServicoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TelaListarAdotantesController {
    private final Navegador navegador;
    private final IAdotanteServico adotanteServico;

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

    public TelaListarAdotantesController(Navegador navegador) {
        this.navegador = navegador;
        this.adotanteServico = AdotanteServicoImpl.getInstance();
    }

    @FXML
    private void initialize() {
        // Vincula as colunas aos atributos da classe Adotante (getId, getNome, getEmail, getCpf)
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        // Some com o aviso "Selecione um adotante" assim que alguém é selecionado
        tabelaAdotantes.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> mensagem.setText(""));

        carregarAdotantes();
    }

    private void carregarAdotantes() {
        try {
            if (adotanteServico != null) {
                ObservableList<Adotante> lista = FXCollections.observableArrayList(adotanteServico.listarTodos());
                tabelaAdotantes.setItems(lista);
            }
        } catch (Exception e) {
            if (mensagem != null) {
                mensagem.setText("Erro ao carregar adotantes: " + e.getMessage());
            }
        }
    }

    @FXML
    private void abrirHistoricoAdotante() {
        Adotante selecionado = tabelaAdotantes.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            if (mensagem != null) mensagem.setText("");
            this.navegador.ir("TelaHistoricoAdotante.fxml");
        } else {
            if (mensagem != null) {
                mensagem.setText("Selecione um adotante na tabela.");
            }
        }
    }

    @FXML
    private void voltar() {
        this.navegador.voltar();
    }
}
