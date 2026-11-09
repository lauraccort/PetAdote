package br.com.petadote.gui;

import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

/**
 * Controller da TelaConsultarParecer.fxml (Parecer da adoção).
 * Mostra ao adotante o status do processo escolhido em "Meus processos"
 * e o parecer mais recente dado pelo abrigo (consulta da avaliação do REQ07).
 */
public class TelaConsultarParecerController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;

    @FXML private TextField campoStatus;
    @FXML private TextField campoParecer;
    @FXML private Label infoAvaliacao;
    @FXML private Label mensagem;

    public TelaConsultarParecerController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        Optional<ProcessoAdocao> escolhido = navegador.selecionado(ProcessoAdocao.class);
        if (escolhido.isEmpty()) {
            mensagem.setText("Nenhum processo selecionado. Volte e escolha um processo em \"Meus processos\".");
            infoAvaliacao.setText("");
            return;
        }
        ProcessoAdocao processo = escolhido.get();
        campoStatus.setText(processo.getStatus().getDescricao() + " — processo " + processo.getId()
                + " (" + processo.getAnimal().getNome() + ")");

        Optional<Avaliacao> ultima = processo.getAvaliacoes().stream()
                .max(Comparator.comparing(Avaliacao::getData, Comparator.nullsFirst(Comparator.naturalOrder())));
        if (ultima.isEmpty()) {
            campoParecer.setText("O abrigo ainda não avaliou este processo.");
            infoAvaliacao.setText("Aberto em " + data(processo) + ". Você será avaliado em breve.");
            return;
        }
        Avaliacao avaliacao = ultima.get();
        String observacao = avaliacao.getObservacao() == null || avaliacao.getObservacao().isBlank()
                ? "" : " — " + avaliacao.getObservacao();
        campoParecer.setText((avaliacao.isFavoravel() ? "Favorável" : "Desfavorável") + observacao);
        infoAvaliacao.setText("Avaliado em " + (avaliacao.getData() == null ? "?" : avaliacao.getData().format(FORMATO))
                + " por " + avaliacao.getResponsavel() + "." + proximoPasso(processo.getStatus()));
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    /** Dica do que acontece depois, conforme o status. */
    private static String proximoPasso(StatusProcesso status) {
        return switch (status) {
            case APROVADO -> " Próximo passo: peça as visitas de acompanhamento em \"Minhas visitas\" e aguarde a aprovação do abrigo.";
            case CONCLUIDO -> " A adoção foi concluída. Parabéns!";
            case NEGADO -> " Você pode iniciar um processo para outro animal.";
            case EM_AVALIACAO -> "";
        };
    }

    private static String data(ProcessoAdocao processo) {
        return processo.getDataAbertura() == null ? "?" : processo.getDataAbertura().format(FORMATO);
    }
}
