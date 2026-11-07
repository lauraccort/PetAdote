package br.com.petadote.gui;

import br.com.petadote.model.Animal;
import br.com.petadote.servico.IAnimalServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.Comparator;

/**
 * Controller da TelaListarAnimaisDisponiveis.fxml (Animais disponíveis para adoção).
 * Leva o animal selecionado para a tela de detalhes.
 */
public class TelaListarAnimaisDisponiveisController {

    private final Navegador navegador;
    private final IAnimalServico animalServico;

    @FXML private TableView<Animal> tabelaAnimais;
    @FXML private TableColumn<Animal, String> colunaId;
    @FXML private TableColumn<Animal, String> colunaNome;
    @FXML private TableColumn<Animal, String> colunaEspecie;
    @FXML private TableColumn<Animal, String> colunaIdade;
    @FXML private Label mensagem;

    public TelaListarAnimaisDisponiveisController(Navegador navegador, IAnimalServico animalServico) {
        this.navegador = navegador;
        this.animalServico = animalServico;
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colunaNome.setCellValueFactory(c -> texto(c.getValue().getNome()));
        colunaEspecie.setCellValueFactory(c -> texto(TextoAnimal.especieERaca(c.getValue())));
        colunaIdade.setCellValueFactory(c -> texto(TextoAnimal.idade(c.getValue().getDataNascimento())));

        tabelaAnimais.setItems(FXCollections.observableArrayList(animalServico.listarDisponiveis().stream()
                .sorted(Comparator.comparing(Animal::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList()));
        tabelaAnimais.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> mensagem.setText(""));
    }

    @FXML
    private void abrirDetalhesAnimal() {
        Animal animal = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (animal == null) {
            mensagem.setText("Selecione um animal na tabela.");
            return;
        }
        navegador.ir("TelaDetalhesAnimal.fxml", animal);
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}
