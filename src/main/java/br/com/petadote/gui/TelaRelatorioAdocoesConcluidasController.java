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
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class TelaRelatorioAdocoesConcluidasController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML
    private DatePicker campoDe;
    @FXML
    private DatePicker campoAte;
    @FXML
    private TableView<ProcessoAdocao> tabela;
    @FXML
    private TableColumn<ProcessoAdocao, String> colProcesso;
    @FXML
    private TableColumn<ProcessoAdocao, String> colAbertura;
    @FXML
    private TableColumn<ProcessoAdocao, String> colConclusao;
    @FXML
    private TableColumn<ProcessoAdocao, String> colAnimal;
    @FXML
    private TableColumn<ProcessoAdocao, String> colEspecie;
    @FXML
    private TableColumn<ProcessoAdocao, String> colRaca;
    @FXML
    private TableColumn<ProcessoAdocao, String> colNascimentoAnimal;
    @FXML
    private TableColumn<ProcessoAdocao, String> colAbrigo;
    @FXML
    private TableColumn<ProcessoAdocao, String> colAdotante;
    @FXML
    private TableColumn<ProcessoAdocao, String> colCpf;
    @FXML
    private TableColumn<ProcessoAdocao, String> colEnderecoAdotante;
    @FXML
    private TableColumn<ProcessoAdocao, String> colQuantAnimais;
    @FXML
    private TableColumn<ProcessoAdocao, String> colAvaliacoes;
    @FXML
    private TableColumn<ProcessoAdocao, String> colVisitas;
    @FXML
    private Label total;
    @FXML
    private Label mensagem;

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

    @FXML
    private void exportarCsv() {
        if (tabela.getItems().isEmpty()) {
            mensagem.setText("Não há adoções no período para exportar.");
            return;
        }

        FileChooser escolha = new FileChooser();
        escolha.setTitle("Salvar relatório em CSV");
        escolha.setInitialFileName("adocoes_concluidas.csv");
        escolha.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo CSV (*.csv)", "*.csv"));
        File destino = escolha.showSaveDialog(tabela.getScene().getWindow());
        if (destino == null) {
            return; // a pessoa cancelou
        }
        if (!destino.getName().toLowerCase().endsWith(".csv")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".csv");
        }

        try {
            Files.writeString(destino.toPath(), montarCsv(), StandardCharsets.UTF_8);
            mensagem.setText("");
            Alert aviso = new Alert(Alert.AlertType.INFORMATION,
                    "Relatório salvo em:\n" + destino.getAbsolutePath());
            aviso.setHeaderText("Exportação concluída");
            aviso.showAndWait();
        } catch (IOException e) {
            mensagem.setText("Não foi possível salvar o arquivo: " + e.getMessage());
        }
    }

    /**
     * Monta o CSV com o que a tabela mostra agora (já filtrado pelo período).
     */
    private String montarCsv() {
        StringBuilder csv = new StringBuilder("\uFEFF"); // BOM: o Excel abre os acentos certos
        List<TableColumn<ProcessoAdocao, ?>> colunas = tabela.getColumns();

        csv.append(colunas.stream()
                .map(c -> campoCsv(c.getText()))
                .collect(Collectors.joining(";"))).append("\r\n");

        for (ProcessoAdocao p : tabela.getItems()) {
            csv.append(colunas.stream()
                    .map(c -> campoCsv(valorCsv(c, p)))
                    .collect(Collectors.joining(";"))).append("\r\n");
        }
        return csv.toString();
    }

    /**
     * Coloca entre aspas os valores que têm ; aspas ou quebra de linha.
     */
    private static String campoCsv(Object valor) {
        String texto = valor == null ? "" : valor.toString();
        if (texto.contains(";") || texto.contains("\"") || texto.contains("\n") || texto.contains("\r")) {
            texto = "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;

    }

    /**
     * Ajusta valores que o Excel interpretaria errado (CPF como número, "0/2" como data).
     */
    private String valorCsv(TableColumn<ProcessoAdocao, ?> coluna, ProcessoAdocao p) {
        Object valor = coluna.getCellData(p);
        String texto = valor == null ? "" : valor.toString();
        if (coluna == colCpf) {
            return formatarCpf(texto);
        }
        if (coluna == colVisitas) {
            return texto.replace("/", " de ");
        }
        return texto;
    }

    private static String formatarCpf(String texto) {
        String d = texto.replaceAll("\\D", "");
        if (d.length() != 11) {
            return texto;
        }
        return d.substring(0, 3) + "." + d.substring(3, 6) + "."
                + d.substring(6, 9) + "-" + d.substring(9);
    }
}