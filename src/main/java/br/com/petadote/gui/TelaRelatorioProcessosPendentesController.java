package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;
import br.com.petadote.servico.PetAdoteFachada;
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
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/** REQ13: exibe processos pendentes de avaliação há mais de 15 dias. */
public class TelaRelatorioProcessosPendentesController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private final PetAdoteFachada fachada;

    @FXML private TableView<ProcessoAdocao> tabela;
    @FXML private TableColumn<ProcessoAdocao, String> colProcesso;
    @FXML private TableColumn<ProcessoAdocao, String> colAbertura;
    @FXML private TableColumn<ProcessoAdocao, String> colDias;
    @FXML private TableColumn<ProcessoAdocao, String> colStatus;
    @FXML private TableColumn<ProcessoAdocao, String> colParecer;
    @FXML private TableColumn<ProcessoAdocao, String> colAnimal;
    @FXML private TableColumn<ProcessoAdocao, String> colEspecie;
    @FXML private TableColumn<ProcessoAdocao, String> colRaca;
    @FXML private TableColumn<ProcessoAdocao, String> colNascimentoAnimal;
    @FXML private TableColumn<ProcessoAdocao, String> colAbrigo;
    @FXML private TableColumn<ProcessoAdocao, String> colAdotante;
    @FXML private TableColumn<ProcessoAdocao, String> colCpf;
    @FXML private TableColumn<ProcessoAdocao, String> colEnderecoAdotante;
    @FXML private TableColumn<ProcessoAdocao, String> colQuantAnimais;
    @FXML private TableColumn<ProcessoAdocao, String> colAvaliacoes;
    @FXML private TableColumn<ProcessoAdocao, String> colVisitas;
    @FXML private Label total;

    /** Usado pelo Navegador (reflexao) quando nao ha injecao explicita. */
    public TelaRelatorioProcessosPendentesController(Navegador navegador) {
        this(navegador, Contexto.get().fachada());
    }

    public TelaRelatorioProcessosPendentesController(Navegador navegador,
                                                     PetAdoteFachada fachada) {
        this.navegador = navegador;
        this.fachada = fachada;
    }

    @FXML
    private void initialize() {
        coluna(colProcesso, ProcessoAdocao::getId);
        coluna(colAbertura, p -> data(p.getDataAbertura()));
        coluna(colDias, TelaRelatorioProcessosPendentesController::diasPendente);
        coluna(colStatus, p -> p.getStatus() == null ? "" : p.getStatus().getDescricao());
        coluna(colParecer, TelaRelatorioProcessosPendentesController::ultimoParecer);
        coluna(colAnimal, p -> p.getAnimal().getNome());
        coluna(colEspecie, p -> TextoAnimal.especie(p.getAnimal()));
        coluna(colRaca, p -> {
            String raca = TextoAnimal.raca(p.getAnimal());
            return raca == null || raca.isBlank() ? "-" : raca;
        });
        coluna(colNascimentoAnimal, p -> data(p.getAnimal().getDataNascimento()));
        coluna(colAbrigo, p -> p.getAnimal().getAbrigo() == null
                ? "sem abrigo" : p.getAnimal().getAbrigo().getNome());
        coluna(colAdotante, p -> p.getAdotante().getNome());
        coluna(colCpf, p -> p.getAdotante().getCpf());
        coluna(colEnderecoAdotante, p -> p.getAdotante().getEndereco());
        coluna(colQuantAnimais, p -> p.getAdotante().getQuantAnimal());
        coluna(colAvaliacoes, p -> p.getAvaliacoes().size());
        coluna(colVisitas, p -> p.getVisitas().stream().filter(Visita::isConcluida).count()
                + "/" + p.getVisitas().size());

        // Liga o hyperlink "Voltar" que esta sem onAction no FXML
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
        List<ProcessoAdocao> pendentes = fachada.listarProcessosPendentes().stream()
                .filter(AbrigoLogado::pode)
                .sorted(Comparator.comparing(ProcessoAdocao::getDataAbertura))
                .toList();

        tabela.setItems(FXCollections.observableArrayList(pendentes));

        total.setText(switch (pendentes.size()) {
            case 0 -> "Nenhum processo pendente de avaliação há mais de 15 dias";
            case 1 -> "1 processo pendente de avaliação há mais de 15 dias";
            default -> pendentes.size() + " processos pendentes de avaliação há mais de 15 dias";
        });
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    /** Dias corridos desde a abertura do processo. */
    private static String diasPendente(ProcessoAdocao processo) {
        if (processo.getDataAbertura() == null) {
            return "-";
        }
        long dias = ChronoUnit.DAYS.between(processo.getDataAbertura(), LocalDate.now());
        return dias + (dias == 1 ? " dia" : " dias");
    }

    /** Parecer da ultima avaliacao registrada, ou "sem parecer". */
    private static String ultimoParecer(ProcessoAdocao processo) {
        List<Avaliacao> avaliacoes = processo.getAvaliacoes();
        if (avaliacoes.isEmpty()) {
            return "sem parecer";
        }
        Avaliacao ultima = avaliacoes.get(avaliacoes.size() - 1);
        return ultima.isFavoravel() ? "Favorável" : "Desfavorável";
    }

    private static String data(LocalDate valor) {
        return valor == null ? "" : valor.format(FORMATO);
    }

    private static void coluna(TableColumn<ProcessoAdocao, String> coluna,
                               Function<ProcessoAdocao, Object> valor) {
        coluna.setCellValueFactory(c -> {
            Object v = valor.apply(c.getValue());
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
    }
}
