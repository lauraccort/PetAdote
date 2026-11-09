package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.repositorio.ProcessoAdocaoRepositorio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

/**
 * Controller da TelaResponderAcompanhamento.fxml (Responder acompanhamento).
 * Complementa o acompanhamento pós-adoção (REQ09/REQ10) com o relato do adotante.
 * O adotante escolhe um dos seus processos aprovados/concluídos e conta como o
 * animal está se adaptando. A resposta fica guardada no processo e o abrigo a vê
 * em "Acompanhamento pós-adoção". Abaixo do formulário aparecem as respostas já enviadas.
 */
public class TelaResponderAcompanhamentoController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int TAMANHO_MAXIMO = 1000;
    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final Contexto contexto = Contexto.get();

    @FXML private ComboBox<ProcessoAdocao> campoProcesso;
    @FXML private TextArea campoResposta;
    @FXML private Button botaoEnviar;
    @FXML private Label contador;
    @FXML private Label mensagem;
    @FXML private TableView<String> tabelaRespostas;
    @FXML private TableColumn<String, String> colunaTexto;

    public TelaResponderAcompanhamentoController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        campoProcesso.setConverter(new StringConverter<>() {
            @Override
            public String toString(ProcessoAdocao processo) {
                if (processo == null) {
                    return "";
                }
                return processo.getAnimal().getNome() + " - Processo " + processo.getId();
            }

            @Override
            public ProcessoAdocao fromString(String texto) {
                return null;
            }
        });

        colunaTexto.setCellValueFactory(c -> texto(c.getValue()));

        campoResposta.textProperty().addListener((obs, antes, depois) -> {
            if (depois != null && depois.length() > TAMANHO_MAXIMO) {
                campoResposta.setText(antes); // não deixa passar do limite
                return;
            }
            atualizarContador();
        });
        campoProcesso.valueProperty().addListener((obs, antes, processo) -> {
            mostrarRespostas(processo);
            mensagem.setText("");
        });

        atualizarContador();

        Optional<Adotante> adotante = contexto.adotanteLogado();
        if (adotante.isEmpty()) {
            desabilitarFormulario();
            erro("Entre como adotante para responder ao acompanhamento.");
            return;
        }

        carregarProcessos(adotante.get());
    }

    private void carregarProcessos(Adotante adotante) {
        campoProcesso.setItems(FXCollections.observableArrayList(
                contexto.processoServico().listarPorAdotante(adotante.getId()).stream()
                        .filter(p -> p.getStatus() == StatusProcesso.APROVADO
                                || p.getStatus() == StatusProcesso.CONCLUIDO)
                        .filter(p -> p.getAnimal() != null)
                        .sorted(Comparator.comparing(ProcessoAdocao::getId).reversed())
                        .toList()));

        if (campoProcesso.getItems().isEmpty()) {
            desabilitarFormulario();
            neutra("Você ainda não tem adoção aprovada ou concluída para acompanhar.");
        } else if (campoProcesso.getItems().size() == 1) {
            campoProcesso.setValue(campoProcesso.getItems().get(0));
        }
    }

    @FXML
    private void enviar() {
        ProcessoAdocao processo = campoProcesso.getValue();
        if (processo == null) {
            erro("Selecione o processo de adoção.");
            return;
        }

        String texto = campoResposta.getText() == null ? "" : campoResposta.getText().trim();
        if (texto.isEmpty()) {
            erro("Escreva a sua resposta.");
            return;
        }
        if (processo.getStatus() != StatusProcesso.APROVADO
                && processo.getStatus() != StatusProcesso.CONCLUIDO) {
            erro("Só é possível responder o acompanhamento de processos aprovados ou concluídos.");
            return;
        }

        processo.registrarRespostaAcompanhamento(LocalDate.now().format(FORMATO) + " — " + texto);
        ProcessoAdocaoRepositorio.getInstance().salvar(processo); // grava em dados/petadote.dat

        campoResposta.clear();
        mostrarRespostas(processo);
        sucesso("Resposta enviada. O abrigo poderá vê-la no acompanhamento de "
                + processo.getAnimal().getNome() + ".");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void mostrarRespostas(ProcessoAdocao processo) {
        if (processo == null) {
            tabelaRespostas.getItems().clear();
            return;
        }
        // O processo é o mesmo objeto do repositório, então já traz a resposta nova
        tabelaRespostas.setItems(FXCollections.observableArrayList(
                processo.getRespostasAcompanhamento().reversed())); // mais recente primeiro
    }

    private void atualizarContador() {
        int tamanho = campoResposta.getText() == null ? 0 : campoResposta.getText().length();
        contador.setText(tamanho + "/" + TAMANHO_MAXIMO);
    }

    private void desabilitarFormulario() {
        campoProcesso.setDisable(true);
        campoResposta.setDisable(true);
        botaoEnviar.setDisable(true);
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }

    private void sucesso(String texto) {
        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText(texto);
    }

    private void neutra(String texto) {
        mensagem.setTextFill(Color.web("#8a6a58"));
        mensagem.setText(texto);
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }
}