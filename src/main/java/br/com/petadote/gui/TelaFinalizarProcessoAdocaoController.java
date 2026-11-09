package br.com.petadote.gui;

import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.servico.IProcessoAdocaoServico;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.util.Optional;

/**
 * Controller da TelaFinalizarProcessoAdocao.fxml (REQ08: concluir a adoção).
 * Antes de concluir confere as regras REQ15 (avaliação favorável),
 * REQ17 (animal fora de tratamento) e REQ18 (2 visitas nos próximos 90 dias),
 * dizendo qual delas impediu a conclusão.
 */
public class TelaFinalizarProcessoAdocaoController {

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML private TextField campoIdProcesso;
    @FXML private Label infoProcesso;
    @FXML private TextField campoObservacoes;
    @FXML private Label mensagem;

    public TelaFinalizarProcessoAdocaoController(Navegador navegador, IProcessoAdocaoServico processoServico) {
        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {
        // Mostra de quem é o processo enquanto o ID é digitado
        campoIdProcesso.textProperty().addListener((obs, anterior, texto) -> mostrarInfo());
        mostrarInfo();
    }

    @FXML
    private void finalizarProcesso() {
        Optional<ProcessoAdocao> encontrado = buscarProcesso();
        if (encontrado.isEmpty()) {
            erro("Informe o ID de um processo existente.");
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
        if (!processoServico.concluir(processo.getId())) {
            erro("Não foi possível concluir este processo.");
            return;
        }

        campoIdProcesso.clear();
        campoObservacoes.clear();
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
        Optional<ProcessoAdocao> processo = buscarProcesso();
        if (campoIdProcesso.getText().isBlank()) {
            infoProcesso.setText("");
        } else if (processo.isEmpty()) {
            infoProcesso.setText("Processo não encontrado.");
        } else {
            ProcessoAdocao p = processo.get();
            infoProcesso.setText("Animal: " + p.getAnimal().getNome()
                    + "  ·  Adotante: " + p.getAdotante().getNome()
                    + "  ·  Status: " + p.getStatus().getDescricao()
                    + "  ·  Visitas aprovadas nos próx. 90 dias: " + p.contarVisitasNoPeriodo()
                    + " de " + ProcessoAdocao.getMinVisitasPosAdocao());
        }
    }

    private Optional<ProcessoAdocao> buscarProcesso() {
        try {
            return processoServico.buscarPorId(Long.parseLong(campoIdProcesso.getText().trim()))
                    .filter(AbrigoLogado::pode);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }
}
