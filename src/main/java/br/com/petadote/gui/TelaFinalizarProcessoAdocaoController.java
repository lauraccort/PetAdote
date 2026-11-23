package br.com.petadote.gui;

import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.servico.PetAdoteFachada;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.util.Comparator;
import java.util.Optional;

/**
 * Controller da TelaFinalizarProcessoAdocao.fxml (REQ08: concluir a adoção).
 * Antes de concluir confere as regras REQ15 (avaliação favorável),
 * REQ17 (animal fora de tratamento) e REQ18 (2 visitas nos próximos 90 dias),
 * dizendo qual delas impediu a conclusão.
 *
 * O processo é escolhido na própria tabela da tela. O ID exibido é
 * somente leitura e nunca é usado como fonte de verdade.
 */
public class TelaFinalizarProcessoAdocaoController {

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final PetAdoteFachada fachada;

    @FXML private TableView<ProcessoAdocao> tabelaProcessos;
    @FXML private TableColumn<ProcessoAdocao, String> colunaId;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAnimal;
    @FXML private TableColumn<ProcessoAdocao, String> colunaAdotante;
    @FXML private TableColumn<ProcessoAdocao, String> colunaStatus;
    @FXML private TableColumn<ProcessoAdocao, String> colunaVisitas;
    @FXML private TextField campoIdProcesso;
    @FXML private Label infoProcesso;
    @FXML private TextField campoObservacoes;
    @FXML private Label mensagem;

    /** Processo escolhido na tabela. Fonte de verdade. */
    private ProcessoAdocao processoSelecionado;

    public TelaFinalizarProcessoAdocaoController(Navegador navegador, PetAdoteFachada fachada) {
        this.navegador = navegador;
        this.fachada = fachada;
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colunaAnimal.setCellValueFactory(c -> texto(c.getValue().getAnimal().getNome()));
        colunaAdotante.setCellValueFactory(c -> texto(c.getValue().getAdotante().getNome()));
        colunaStatus.setCellValueFactory(c -> texto(c.getValue().getStatus().getDescricao()));
        colunaVisitas.setCellValueFactory(c -> texto(
                c.getValue().contarVisitasNoPeriodo()
                        + " de " + ProcessoAdocao.getMinVisitasPosAdocao()));

        carregarProcessos();

        // Ao selecionar na tabela, preenche o resumo e limpa a mensagem anterior
        tabelaProcessos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> {
                    processoSelecionado = atual;
                    mensagem.setText("");
                    mostrarInfo();
                });

        // Se veio de outra tela com um processo escolhido, já deixa selecionado
        navegador.selecionado(ProcessoAdocao.class)
                .filter(AbrigoLogado::pode)
                .ifPresent(p -> {
                    processoSelecionado = p;
                    campoIdProcesso.setText(String.valueOf(p.getId()));
                    tabelaProcessos.getSelectionModel().select(p);
                });

        mostrarInfo();
    }

    /** Lista os processos aprovados do abrigo logado, do mais antigo ao mais novo. */
    private void carregarProcessos() {
        tabelaProcessos.setItems(FXCollections.observableArrayList(
                fachada.listarProcessos().stream()
                        .filter(p -> p.getStatus() == StatusProcesso.APROVADO)
                        .filter(AbrigoLogado::pode)
                        .sorted(Comparator.comparing(ProcessoAdocao::getDataAbertura,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList()));
    }

    @FXML
    private void finalizarProcesso() {
        Optional<ProcessoAdocao> encontrado = buscarProcesso();
        if (encontrado.isEmpty()) {
            erro("Selecione um processo na tabela.");
            return;
        }
        ProcessoAdocao processo = encontrado.get();
        String impedimento = motivoParaNaoConcluir(processo);
        if (impedimento != null) {
            erro(impedimento);
            return;
        }

        String observacoes = campoObservacoes.getText().trim();
        processo.setObservacaoFinal(observacoes.isEmpty() ? null : observacoes);
        // REQ08: conclui o processo e o animal passa a "Adotado"
        if (!fachada.concluirProcesso(processo.getId())) {
            erro("Não foi possível concluir este processo.");
            return;
        }

        processoSelecionado = null;
        campoIdProcesso.clear();
        campoObservacoes.clear();
        carregarProcessos();   // o processo concluído sai da lista

        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText("Adoção concluída: " + processo.getAnimal().getNome() + " agora é de "
                + processo.getAdotante().getNome() + ". Acompanhe as visitas em \"Acompanhamento pós-adoção\".");
    }

    @FXML
    private void abrirAcompanharPosAdocao() {
        navegador.ir("TelaAcompanharPosAdocao.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    /** Explica qual regra impede a conclusão, ou null se pode concluir. */
    private static String motivoParaNaoConcluir(ProcessoAdocao processo) {
        StatusProcesso status = processo.getStatus();
        if (status == StatusProcesso.CONCLUIDO) {
            return "Este processo já foi concluído.";
        }
        if (status == StatusProcesso.NEGADO) {
            return "Este processo foi negado na avaliação e não pode ser concluído.";
        }
        if (status == StatusProcesso.EM_AVALIACAO) {
            return "O processo ainda precisa ser avaliado e aprovado antes de ser concluído.";
        }
        if (!processo.avaliacaoFavoravel()) {
            return "É preciso ao menos uma avaliação com parecer favorável (REQ15).";
        }
        if (!processo.getAdotante().podeConcluirNovoProcesso()) {
            return "O adotante já tem três adoções em acompanhamento pós-adoção (REQ16).";
        }
        if (!processo.getAnimal().podeConcluirAdocao()) {
            return processo.getAnimal().getNome() + " está em tratamento veterinário; a adoção não pode ser concluída agora (REQ17).";
        }
        if (!processo.temVisitasSuficientes()) {
            long faltam = ProcessoAdocao.getMinVisitasPosAdocao() - processo.contarVisitasNoPeriodo();
            return "Faltam " + faltam + " visita(s) de acompanhamento aprovada(s) nos próximos 90 dias (REQ18). "
                    + "Confirme os pedidos do adotante no \"Painel de visitas\" ou agende em \"Acompanhamento pós-adoção\".";
        }
        return null;
    }

    private void mostrarInfo() {
        if (processoSelecionado == null) {
            campoIdProcesso.setText("");
            infoProcesso.setText("");
            return;
        }

        ProcessoAdocao p = processoSelecionado;
        campoIdProcesso.setText(String.valueOf(p.getId()));
        infoProcesso.setText("Animal: " + p.getAnimal().getNome()
                + "  ·  Adotante: " + p.getAdotante().getNome()
                + "  ·  Status: " + p.getStatus().getDescricao()
                + "  ·  Visitas aprovadas nos próx. 90 dias: " + p.contarVisitasNoPeriodo()
                + " de " + ProcessoAdocao.getMinVisitasPosAdocao());
    }

    /**
     * Retorna o processo selecionado na tabela. O texto do campo NÃO é lido:
     * o ID exibido é apenas informativo.
     */
    private Optional<ProcessoAdocao> buscarProcesso() {
        return Optional.ofNullable(processoSelecionado);
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}
