package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;
import br.com.petadote.servico.IProcessoAdocaoServico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class TelaRelatorioAdocoesConcluidasController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML private DatePicker campoDe;
    @FXML private DatePicker campoAte;
    @FXML private TableView<ProcessoAdocao> tabela;
    @FXML private TableColumn<ProcessoAdocao, String> colProcesso;
    @FXML private TableColumn<ProcessoAdocao, String> colAbertura;
    @FXML private TableColumn<ProcessoAdocao, String> colConclusao;
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
    @FXML private Label mensagem;

    public TelaRelatorioAdocoesConcluidasController(Navegador navegador) {
        this(navegador, Contexto.get().processoServico());
    }

    public TelaRelatorioAdocoesConcluidasController(Navegador navegador, IProcessoAdocaoServico processoServico) {
        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        coluna(colProcesso, ProcessoAdocao::getId);
        coluna(colAbertura, p -> data(p.getDataAbertura()));
        coluna(colConclusao, p -> data(p.getDataConclusao()));
        coluna(colAnimal, p -> p.getAnimal().getNome());
        coluna(colEspecie, p -> TextoAnimal.especie(p.getAnimal()));
        coluna(colRaca, p -> TextoAnimal.raca(p.getAnimal()));
        coluna(colNascimentoAnimal, p -> data(p.getAnimal().getDataNascimento()));
        coluna(colAbrigo, p -> p.getAnimal().getAbrigo() == null ? null : p.getAnimal().getAbrigo().getNome());
        coluna(colAdotante, p -> p.getAdotante().getNome());
        coluna(colCpf, p -> p.getAdotante().getCpf());
        coluna(colEnderecoAdotante, p -> p.getAdotante().getEndereco());
        coluna(colQuantAnimais, p -> p.getAdotante().getQuantAnimal());
        coluna(colAvaliacoes, p -> p.getAvaliacoes().size());
        coluna(colVisitas, p -> p.getVisitas().stream().filter(Visita::isConcluida).count()
                + "/" + p.getVisitas().size());

        filtrar();
    }

    @FXML
    private void filtrar() {
        LocalDate de = campoDe.getValue();
        LocalDate ate = campoAte.getValue();
        if (de != null && ate != null && de.isAfter(ate)) {
            mensagem.setText("A data inicial não pode ser depois da data final.");
            return;
        }
        mensagem.setText("");

        List<ProcessoAdocao> concluidos = processoServico.listarConcluidos().stream()
                .filter(p -> de == null || (p.getDataConclusao() != null && !p.getDataConclusao().isBefore(de)))
                .filter(p -> ate == null || (p.getDataConclusao() != null && !p.getDataConclusao().isAfter(ate)))
                .sorted(Comparator.comparing(ProcessoAdocao::getDataConclusao,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        tabela.setItems(FXCollections.observableArrayList(concluidos));
        total.setText(concluidos.size() == 1
                ? "1 adoção concluída"
                : concluidos.size() + " adoções concluídas");
    }

    @FXML
    private void limpar() {
        campoDe.setValue(null);
        campoAte.setValue(null);
        filtrar();
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private static String data(LocalDate valor) {
        return valor == null ? "" : valor.format(FORMATO);
    }

    private static void coluna(TableColumn<ProcessoAdocao, String> coluna, Function<ProcessoAdocao, Object> valor) {
        coluna.setCellValueFactory(c -> {
            Object v = valor.apply(c.getValue());
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
    }
}