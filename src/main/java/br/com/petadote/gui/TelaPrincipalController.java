package br.com.petadote.gui;

import br.com.petadote.model.Animal;
import br.com.petadote.model.Cao;
import br.com.petadote.servico.IAnimalServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.time.format.DateTimeFormatter;

/**
 * Controller da TelaPrincipal.fxml. Só cuida de entrada e saída;
 * as regras de negócio ficam no serviço.
 */
public class TelaPrincipalController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final IAnimalServico animalServico;

    @FXML private TextField campoNome;
    @FXML private DatePicker campoNascimento;
    @FXML private TextField campoRaca;
    @FXML private Label mensagem;

    @FXML private TableView<Animal> tabelaAnimais;
    @FXML private TableColumn<Animal, String> colunaId;
    @FXML private TableColumn<Animal, String> colunaNome;
    @FXML private TableColumn<Animal, String> colunaNascimento;
    @FXML private TableColumn<Animal, String> colunaStatus;

    public TelaPrincipalController(IAnimalServico animalServico) {
        this.animalServico = animalServico;
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colunaNome.setCellValueFactory(c -> texto(c.getValue().getNome()));
        colunaNascimento.setCellValueFactory(c -> texto(
                c.getValue().getDataNascimento() == null ? null : c.getValue().getDataNascimento().format(FORMATO)));
        colunaStatus.setCellValueFactory(c -> texto(c.getValue().getStatus()));
        atualizarTabela();
    }

    @FXML
    private void cadastrarCao() {
        if (campoNome.getText().isBlank() || campoNascimento.getValue() == null) {
            mensagem.setText("Informe o nome e a data de nascimento.");
            return;
        }
        try {
            Animal cao = animalServico.cadastrar(
                    new Cao(campoNome.getText().trim(), campoNascimento.getValue(), campoRaca.getText().trim()));
            mensagem.setText("Cão cadastrado: " + cao.getNome() + " (id " + cao.getId() + ")");
            campoNome.clear();
            campoNascimento.setValue(null);
            campoRaca.clear();
            atualizarTabela();
        } catch (RuntimeException e) {
            mensagem.setText("Erro: " + e.getMessage());
        }
    }

    private void atualizarTabela() {
        tabelaAnimais.setItems(FXCollections.observableArrayList(animalServico.listarTodos()));
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
