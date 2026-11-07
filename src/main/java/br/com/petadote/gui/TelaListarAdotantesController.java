package br.com.petadote.gui;

import br.com.petadote.model.Adotante;
import br.com.petadote.servico.IAdotanteServico;
import br.com.petadote.servico.AdotanteServicoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
    private TableColumn<Adotante, String> colunaTelefone;
    @FXML
    private TableColumn<Adotante, String> colunaCpf;

    public TelaListarAdotantesController(Navegador navegador) {
        this.navegador = navegador;
        // Obtém a instância única pelo Singleton (se o seu método for em português, altere para getInstancia())
        this.adotanteServico = AdotanteServicoImpl.getInstance();
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        carregarAdotantes();
    }

    private void carregarAdotantes() {
        try {
            if (adotanteServico != null) {
                ObservableList<Adotante> lista = FXCollections.observableArrayList(adotanteServico.listarTodos());
                tabelaAdotantes.setItems(lista);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar adotantes: " + e.getMessage());
        }
    }

    @FXML
    private void abrirHistoricoAdotante() {
        Adotante selecionado = tabelaAdotantes.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            this.navegador.ir("TelaHistoricoAdotante.fxml");
        }
    }

    @FXML
    private void voltar() {
        this.navegador.voltar();
    }
}