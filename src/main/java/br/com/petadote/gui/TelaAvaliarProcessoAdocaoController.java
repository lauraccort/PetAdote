package br.com.petadote.gui;

import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.Parecer;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.servico.PetAdoteFachada;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller da TelaAvaliarProcessoAdocao.fxml (REQ07: avaliação prévia com parecer).
 * Parecer "Aprovado" registra a avaliação favorável e aprova o processo;
 * "Reprovado" registra a avaliação e nega o processo, liberando o animal.
 *
 * O processo vem sempre do Navegador. O ID exibido é somente leitura e
 * nunca é usado como fonte de verdade.
 */
public class TelaAvaliarProcessoAdocaoController {

    private static final String APROVADO = "Aprovado";
    private static final String REPROVADO = "Reprovado";
    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final PetAdoteFachada fachada;

    @FXML private TextField campoIdProcesso;
    @FXML private Label infoProcesso;
    @FXML private ComboBox<String> campoStatusAvaliacao;
    @FXML private TextField campoObservacoes;
    @FXML private Label mensagem;

    /** Processo escolhido em "Solicitações pendentes". Fonte de verdade. */
    private ProcessoAdocao processoSelecionado;

    public TelaAvaliarProcessoAdocaoController(Navegador navegador, PetAdoteFachada fachada) {
        this.navegador = navegador;
        this.fachada = fachada;
    }

    @FXML
    private void initialize() {
        campoStatusAvaliacao.getItems().addAll(APROVADO, REPROVADO);
        Combos.mostrarDicaQuandoVazio(campoStatusAvaliacao);

        // Vindo de "Solicitações pendentes", o processo já vem escolhido.
        // O ID exibido é apenas informativo.
        processoSelecionado = navegador.selecionado(ProcessoAdocao.class)
                .filter(AbrigoLogado::pode)
                .orElse(null);

        if (processoSelecionado != null) {
            campoIdProcesso.setText(String.valueOf(processoSelecionado.getId()));
        }

        mostrarInfo();
    }

    @FXML
    private void salvarAvaliacao() {
        Optional<ProcessoAdocao> encontrado = buscarProcesso();
        if (encontrado.isEmpty()) {
            erro("Nenhum processo selecionado. Volte e escolha um processo nas solicitações pendentes.");
            return;
        }
        ProcessoAdocao processo = encontrado.get();
        if (processo.getStatus() != StatusProcesso.EM_AVALIACAO) {
            erro("Este processo já foi avaliado (status: " + processo.getStatus().getDescricao() + ").");
            return;
        }
        String decisao = campoStatusAvaliacao.getValue();
        if (decisao == null) {
            erro("Selecione o parecer.");
            return;
        }
        String observacoes = campoObservacoes.getText().trim();
        if (REPROVADO.equals(decisao) && observacoes.isEmpty()) {
            erro("Explique o motivo da reprovação em \"Observações e Justificativa\".");
            return;
        }
        // REQ07: registra a avaliação prévia antes de aprovar/negar o processo
        Parecer parecer = APROVADO.equals(decisao) ? Parecer.POSITIVO : Parecer.NEGATIVO;
        fachada.registrarAvaliacao(processo.getId(),
                new Avaliacao(LocalDate.now(), responsavel(processo), parecer, observacoes));
        boolean ok = parecer == Parecer.POSITIVO
                ? fachada.aprovarProcesso(processo.getId())
                : fachada.negarProcesso(processo.getId());
        if (!ok) {
            erro("Não foi possível concluir a avaliação deste processo.");
            return;
        }

        String nomes = processo.getAnimal().getNome() + " → " + processo.getAdotante().getNome();
        processoSelecionado = null;
        campoIdProcesso.clear();
        campoStatusAvaliacao.setValue(null);
        campoObservacoes.clear();
        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText(parecer == Parecer.POSITIVO
                ? "Processo " + processo.getId() + " (" + nomes + ") aprovado."
                : "Processo " + processo.getId() + " (" + nomes + ") reprovado. O animal voltou a ficar disponível.");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    private void mostrarInfo() {
        if (processoSelecionado == null) {
            infoProcesso.setText("");
            return;
        }

        ProcessoAdocao p = processoSelecionado;
        infoProcesso.setText("Animal: " + p.getAnimal().getNome()
                + "  ·  Adotante: " + p.getAdotante().getNome()
                + " (" + p.getAdotante().getQuantAnimal() + " animal(is) em casa)"
                + "  ·  Status: " + p.getStatus().getDescricao());
    }

    private Optional<ProcessoAdocao> buscarProcesso() {
        return Optional.ofNullable(processoSelecionado);
    }

    private static String responsavel(ProcessoAdocao processo) {
        return processo.getAnimal().getAbrigo() == null
                ? "Abrigo" : processo.getAnimal().getAbrigo().getNome();
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}