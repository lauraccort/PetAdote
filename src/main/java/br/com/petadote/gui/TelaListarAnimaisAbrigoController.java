package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Animal;
import br.com.petadote.model.Cao;
import br.com.petadote.model.Gato;
import br.com.petadote.model.OutraEspecie;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.model.StatusAnimal;
import br.com.petadote.servico.IAbrigoServico;
import br.com.petadote.servico.IAnimalServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Controller da TelaListarAnimaisAbrigo.fxml (Animais do abrigo).
 * Lista os animais com filtros por abrigo, espécie, status e busca por nome.
 */
public class TelaListarAnimaisAbrigoController {

    private static final String CAO = "Cão";
    private static final String GATO = "Gato";
    private static final String OUTRA = "Outra";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_INFO = Color.web("#4a2a1a");

    private final Navegador navegador;
    private final IAnimalServico animalServico;
    private final IAbrigoServico abrigoServico;

    @FXML private ComboBox<Abrigo> filtroAbrigo;
    @FXML private ComboBox<String> filtroEspecie;
    @FXML private ComboBox<StatusAnimal> filtroStatus;
    @FXML private TextField campoBusca;
    @FXML private TableView<Animal> tabelaAnimais;
    @FXML private TableColumn<Animal, String> colunaId;
    @FXML private TableColumn<Animal, String> colunaNome;
    @FXML private TableColumn<Animal, String> colunaEspecie;
    @FXML private TableColumn<Animal, String> colunaRaca;
    @FXML private TableColumn<Animal, String> colunaIdade;
    @FXML private TableColumn<Animal, String> colunaStatus;
    @FXML private Label mensagem;

    public TelaListarAnimaisAbrigoController(Navegador navegador, IAnimalServico animalServico, IAbrigoServico abrigoServico) {
        this.navegador = navegador;
        this.animalServico = animalServico;
        this.abrigoServico = abrigoServico;
    }

    @FXML
    private void initialize() {
        coluna(colunaId, Animal::getId);
        coluna(colunaNome, Animal::getNome);
        coluna(colunaEspecie, TelaListarAnimaisAbrigoController::especie);
        coluna(colunaRaca, TelaListarAnimaisAbrigoController::raca);
        coluna(colunaIdade, a -> idade(a.getDataNascimento()));
        coluna(colunaStatus, a -> a.getStatus().getDescricao());

        filtroAbrigo.getItems().setAll(abrigoServico.listarTodos().stream()
                .filter(a -> a.getStatus() == StatusAbrigo.APROVADO)
                .sorted(Comparator.comparing(Abrigo::getNome))
                .toList());
        filtroAbrigo.setConverter(conversor(Abrigo::getNome));
        filtroEspecie.getItems().addAll(CAO, GATO, OUTRA);
        filtroStatus.getItems().addAll(StatusAnimal.values());
        filtroStatus.setConverter(conversor(StatusAnimal::getDescricao));

        // Qualquer filtro ou a busca atualiza a tabela na hora
        filtroAbrigo.valueProperty().addListener((obs, a, b) -> filtrar());
        filtroEspecie.valueProperty().addListener((obs, a, b) -> filtrar());
        filtroStatus.valueProperty().addListener((obs, a, b) -> filtrar());
        campoBusca.textProperty().addListener((obs, a, b) -> filtrar());
        tabelaAnimais.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> mensagem.setText(""));

        Combos.mostrarDicaQuandoVazio(filtroAbrigo);
        Combos.mostrarDicaQuandoVazio(filtroEspecie);
        Combos.mostrarDicaQuandoVazio(filtroStatus);

        filtrar();
    }

    @FXML
    private void limparFiltros() {
        filtroAbrigo.setValue(null);
        filtroEspecie.setValue(null);
        filtroStatus.setValue(null);
        campoBusca.clear();
    }

    /** Mostra os dados completos do animal selecionado embaixo da tabela. */
    @FXML
    private void verDetalhes() {
        Animal animal = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (animal == null) {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText("Selecione um animal na tabela.");
            return;
        }
        String raca = raca(animal);
        mensagem.setTextFill(COR_INFO);
        mensagem.setText(animal.getNome() + " · " + especie(animal)
                + (raca == null || raca.isBlank() ? "" : " (" + raca + ")")
                + " · nascido em " + (animal.getDataNascimento() == null ? "?" : animal.getDataNascimento().format(FORMATO))
                + " (" + idade(animal.getDataNascimento()) + ")"
                + " · abrigo " + (animal.getAbrigo() == null ? "sem abrigo" : animal.getAbrigo().getNome())
                + " · " + animal.getStatus().getDescricao());
    }

    @FXML
    private void abrirCadastroAnimal() {
        navegador.ir("TelaCadastroAnimal.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void filtrar() {
        Abrigo abrigo = filtroAbrigo.getValue();
        String especie = filtroEspecie.getValue();
        StatusAnimal status = filtroStatus.getValue();
        String busca = campoBusca.getText().trim().toLowerCase();

        tabelaAnimais.setItems(FXCollections.observableArrayList(animalServico.listarTodos().stream()
                .filter(a -> abrigo == null || a.getAbrigo() == abrigo)
                .filter(a -> especie == null || especie.equals(grupoEspecie(a)))
                .filter(a -> status == null || a.getStatus() == status)
                .filter(a -> busca.isEmpty() || (a.getNome() != null && a.getNome().toLowerCase().contains(busca)))
                .sorted(Comparator.comparing(Animal::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList()));
        mensagem.setText("");
    }

    /** Cão, Gato ou Outra (para o filtro). */
    private static String grupoEspecie(Animal animal) {
        if (animal instanceof Cao) {
            return CAO;
        }
        return animal instanceof Gato ? GATO : OUTRA;
    }

    /** Para "Outra", mostra a espécie cadastrada (ex.: Coelho). */
    private static String especie(Animal animal) {
        return animal instanceof OutraEspecie outra ? outra.getEspecie() : grupoEspecie(animal);
    }

    private static String raca(Animal animal) {
        if (animal instanceof Cao cao) {
            return cao.getRaca();
        }
        return animal instanceof Gato gato ? gato.getRaca() : null;
    }

    /** "3 anos", "1 ano" ou "5 meses". */
    private static String idade(LocalDate nascimento) {
        if (nascimento == null) {
            return "";
        }
        Period p = Period.between(nascimento, LocalDate.now());
        if (p.getYears() > 0) {
            return p.getYears() + (p.getYears() == 1 ? " ano" : " anos");
        }
        return p.getMonths() + (p.getMonths() == 1 ? " mês" : " meses");
    }

    private static void coluna(TableColumn<Animal, String> coluna, Function<Animal, Object> valor) {
        coluna.setCellValueFactory(c -> {
            Object v = valor.apply(c.getValue());
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
    }

    private static <T> StringConverter<T> conversor(Function<T, String> texto) {
        return new StringConverter<>() {
            @Override
            public String toString(T valor) {
                return valor == null ? "" : texto.apply(valor);
            }

            @Override
            public T fromString(String s) {
                return null; // combos não editáveis
            }
        };
    }
}
