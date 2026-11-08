package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Animal;
import br.com.petadote.model.Cao;
import br.com.petadote.model.Gato;
import br.com.petadote.model.InfoRaca;
import br.com.petadote.servico.IAnimalServico;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class TelaRelatorioAnimaisDisponiveisController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private final IAnimalServico animalServico;

    @FXML private TableView<Animal> tabela;
    @FXML private TableColumn<Animal, String> colId;
    @FXML private TableColumn<Animal, String> colNome;
    @FXML private TableColumn<Animal, String> colEspecie;
    @FXML private TableColumn<Animal, String> colRaca;
    @FXML private TableColumn<Animal, String> colNascimento;
    @FXML private TableColumn<Animal, String> colIdade;
    @FXML private TableColumn<Animal, String> colStatus;
    @FXML private TableColumn<Animal, String> colAbrigo;
    @FXML private TableColumn<Animal, String> colEnderecoAbrigo;
    @FXML private TableColumn<Animal, String> colPorte;
    @FXML private TableColumn<Animal, String> colPeso;
    @FXML private TableColumn<Animal, String> colTemperamento;
    @FXML private Label total;

    public TelaRelatorioAnimaisDisponiveisController(Navegador navegador) {
        this(navegador, Contexto.get().animalServico());
    }

    public TelaRelatorioAnimaisDisponiveisController(Navegador navegador, IAnimalServico animalServico) {
        this.navegador = navegador;
        this.animalServico = animalServico;
    }

    @FXML
    private void initialize() {
        coluna(colId, Animal::getId);
        coluna(colNome, Animal::getNome);
        coluna(colEspecie, TextoAnimal::especie);
        coluna(colRaca, a -> {
            String raca = TextoAnimal.raca(a);
            return raca == null || raca.isBlank() ? "-" : raca;
        });
        coluna(colNascimento, a -> data(a.getDataNascimento()));
        coluna(colIdade, a -> TextoAnimal.idade(a.getDataNascimento()));
        coluna(colStatus, a -> a.getStatus() == null ? "" : a.getStatus().getDescricao());
        coluna(colAbrigo, a -> a.getAbrigo() == null ? "sem abrigo" : a.getAbrigo().getNome());
        coluna(colEnderecoAbrigo, a -> a.getAbrigo() == null ? "-" : a.getAbrigo().getEndereco());
        coluna(colPorte, a -> infoRaca(a) == null ? "-" : infoRaca(a).getPorte());
        coluna(colPeso, a -> infoRaca(a) == null ? "-"
                : String.format("%.1f", infoRaca(a).getPesoMedio()));
        coluna(colTemperamento, a -> infoRaca(a) == null ? "-" : infoRaca(a).getTemperamento());

        Platform.runLater(() -> {
            if (tabela.getScene() == null) {
                return;
            }
            tabela.getScene().getRoot().lookupAll(".hyperlink").stream()
                    .filter(n -> n instanceof Hyperlink h && "Voltar".equals(h.getText()))
                    .map(Hyperlink.class::cast)
                    .forEach(h -> h.setOnAction(e -> voltar()));
        });

        carregar();
    }

    private void carregar() {
        List<Animal> disponiveis = animalServico.listarDisponiveis().stream()
                .sorted(Comparator.comparing(Animal::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();

        tabela.setItems(FXCollections.observableArrayList(disponiveis));
        total.setText(disponiveis.size() == 1
                ? "1 animal disponível"
                : disponiveis.size() + " animais disponíveis");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private static InfoRaca infoRaca(Animal animal) {
        if (animal instanceof Cao cao) {
            return cao.getInfoRaca();
        }
        if (animal instanceof Gato gato) {
            return gato.getInfoRaca();
        }
        return null;
    }

    private static String data(LocalDate valor) {
        return valor == null ? "" : valor.format(FORMATO);
    }

    private static void coluna(TableColumn<Animal, String> coluna, Function<Animal, Object> valor) {
        coluna.setCellValueFactory(c -> {
            Object v = valor.apply(c.getValue());
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
    }
}