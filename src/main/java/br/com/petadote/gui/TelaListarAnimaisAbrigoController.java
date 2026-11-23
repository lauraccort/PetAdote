
package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Animal;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.model.StatusAnimal;
import br.com.petadote.servico.PetAdoteFachada;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Controller da TelaListarAnimaisAbrigo.fxml (Animais do abrigo).
 * Lista os animais com filtros por abrigo, espécie, status e busca por nome.
 * Com o filtro de status "Disponível" atende também à consulta do REQ11.
 */
public class TelaListarAnimaisAbrigoController {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_INFO = Color.web("#4a2a1a");

    private final Navegador navegador;
    private final PetAdoteFachada fachada;

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

    public TelaListarAnimaisAbrigoController(
            Navegador navegador,
            PetAdoteFachada fachada) {

        this.navegador = navegador;
        this.fachada = fachada;
    }

    @FXML
    private void initialize() {

        coluna(colunaId, Animal::getId);
        coluna(colunaNome, Animal::getNome);
        coluna(colunaEspecie, TextoAnimal::especie);
        coluna(colunaRaca, TextoAnimal::raca);
        coluna(colunaIdade,
                a -> TextoAnimal.idade(a.getDataNascimento()));
        coluna(colunaStatus,
                a -> a.getStatus().getDescricao());

        filtroAbrigo.getItems().setAll(
                fachada.listarAbrigos().stream()
                        .filter(a -> a.getStatus() == StatusAbrigo.APROVADO)
                        .filter(AbrigoLogado::pode)
                        .sorted(Comparator.comparing(Abrigo::getNome))
                        .toList()
        );

        filtroAbrigo.setConverter(conversor(Abrigo::getNome));

        filtroEspecie.getItems().addAll(
                TextoAnimal.CAO,
                TextoAnimal.GATO,
                TextoAnimal.OUTRA
        );

        filtroStatus.getItems().addAll(StatusAnimal.values());
        filtroStatus.setConverter(
                conversor(StatusAnimal::getDescricao)
        );

        // Qualquer filtro ou busca atualiza a tabela na hora.
        filtroAbrigo.valueProperty().addListener(
                (obs, a, b) -> filtrar()
        );

        filtroEspecie.valueProperty().addListener(
                (obs, a, b) -> filtrar()
        );

        filtroStatus.valueProperty().addListener(
                (obs, a, b) -> filtrar()
        );

        campoBusca.textProperty().addListener(
                (obs, a, b) -> filtrar()
        );

        tabelaAnimais.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, a, b) -> mensagem.setText(""));

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

    /**
     * Mostra os dados completos do animal selecionado embaixo da tabela.
     */
    @FXML
    private void verDetalhes() {

        Animal animal = tabelaAnimais.getSelectionModel()
                .getSelectedItem();

        if (animal == null) {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText("Selecione um animal na tabela.");
            return;
        }

        String raca = TextoAnimal.raca(animal);

        mensagem.setTextFill(COR_INFO);

        mensagem.setText(
                animal.getNome()
                        + " · " + TextoAnimal.especie(animal)
                        + (raca == null || raca.isBlank()
                        ? "" : " (" + raca + ")")
                        + " · nascido em "
                        + (animal.getDataNascimento() == null
                        ? "?"
                        : animal.getDataNascimento().format(FORMATO))
                        + " (" + TextoAnimal.idade(animal.getDataNascimento()) + ")"
                        + " · abrigo "
                        + (animal.getAbrigo() == null
                        ? "sem abrigo"
                        : animal.getAbrigo().getNome())
                        + " · " + animal.getStatus().getDescricao()
        );
    }

    /**
     * REQ17: envia o animal selecionado para tratamento veterinário.
     */
    @FXML
    private void enviarParaTratamento() {

        Animal animal = tabelaAnimais.getSelectionModel()
                .getSelectedItem();

        if (animal == null) {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText("Selecione um animal na tabela.");
            return;
        }

        boolean sucesso =
                fachada.enviarParaTratamento(animal.getId());

        if (sucesso) {
            filtrar();
            mensagem.setTextFill(COR_INFO);
            mensagem.setText(
                    "Animal enviado para tratamento veterinário."
            );
        } else {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText(
                    "Não foi possível enviar o animal para tratamento. "
                            + "Verifique o status atual."
            );
        }
    }

    /**
     * REQ17: registra a alta do tratamento veterinário.
     */
    @FXML
    private void receberAlta() {

        Animal animal = tabelaAnimais.getSelectionModel()
                .getSelectedItem();

        if (animal == null) {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText("Selecione um animal na tabela.");
            return;
        }

        boolean sucesso =
                fachada.receberAlta(animal.getId());

        if (sucesso) {
            filtrar();
            mensagem.setTextFill(COR_INFO);
            mensagem.setText(
                    "Alta veterinária registrada com sucesso."
            );
        } else {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText(
                    "Não foi possível registrar a alta. "
                            + "Verifique se o animal está em tratamento."
            );
        }
    }

    @FXML
    private void editarAnimal() {
        Animal animal = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (animal == null || !AbrigoLogado.pode(animal)) {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText("Selecione um animal do seu abrigo.");
            return;
        }

        TextField nome = new TextField(animal.getNome());
        DatePicker nascimento = new DatePicker(animal.getDataNascimento());
        nascimento.setEditable(false);
        DialogoEdicao dialogo = new DialogoEdicao(
                tabelaAnimais.getScene().getWindow(), "Editar animal");
        dialogo.adicionarCampo("Nome", nome);
        dialogo.adicionarCampo("Data de nascimento", nascimento);

        if (dialogo.editar(() ->
                fachada.atualizarAnimal(animal.getId(), nome.getText().trim(), nascimento.getValue()))) {
            filtrar();
            mensagem.setTextFill(COR_INFO);
            mensagem.setText("Animal atualizado com sucesso.");
        }
    }

    @FXML
    private void excluirAnimal() {
        Animal animal = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (animal == null) {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText("Selecione um animal na tabela.");
            return;
        }
        if (!AbrigoLogado.pode(animal) || animal.getStatus() != StatusAnimal.DISPONIVEL) {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText("Selecione um animal disponível do seu abrigo.");
            return;
        }

        ButtonType excluir = new ButtonType("Excluir");
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja excluir " + animal.getNome() + "?", ButtonType.CANCEL, excluir);
        confirmacao.initOwner(tabelaAnimais.getScene().getWindow());
        confirmacao.setTitle("Excluir animal");
        confirmacao.setHeaderText(null);
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != excluir) {
            return;
        }

        if (!fachada.excluirAnimal(animal.getId())) {
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText("Só é possível excluir animais disponíveis sem histórico de adoção.");
            return;
        }

        filtrar();
        mensagem.setTextFill(COR_INFO);
        mensagem.setText("Animal excluído com sucesso.");
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

        String busca = campoBusca.getText()
                .trim()
                .toLowerCase();

        tabelaAnimais.setItems(
                FXCollections.observableArrayList(
                        fachada.listarAnimais().stream()
                                .filter(AbrigoLogado::pode)
                                .filter(a -> abrigo == null
                                        || a.getAbrigo() == abrigo)
                                .filter(a -> especie == null
                                        || especie.equals(
                                        TextoAnimal.grupoEspecie(a)))
                                .filter(a -> status == null
                                        || a.getStatus() == status)
                                .filter(a -> busca.isEmpty()
                                        || (a.getNome() != null
                                        && a.getNome().toLowerCase()
                                        .contains(busca)))
                                .sorted(Comparator.comparing(
                                        Animal::getNome,
                                        String.CASE_INSENSITIVE_ORDER))
                                .toList()
                )
        );

        mensagem.setText("");
    }

    private static void coluna(
            TableColumn<Animal, String> coluna,
            Function<Animal, Object> valor) {

        coluna.setCellValueFactory(c -> {
            Object v = valor.apply(c.getValue());

            return new SimpleStringProperty(
                    v == null ? "" : v.toString()
            );
        });
    }

    private static <T> StringConverter<T> conversor(
            Function<T, String> texto) {

        return new StringConverter<>() {

            @Override
            public String toString(T valor) {
                return valor == null ? "" : texto.apply(valor);
            }

            @Override
            public T fromString(String s) {
                return null;
            }
        };
    }
}
