package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.Animal;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.model.Visita;
import br.com.petadote.servico.IProcessoAdocaoServico;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TelaAgendarVisitaController {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final int DIAS_LIMITE = 90;

    private final Navegador navegador;

    private final IProcessoAdocaoServico processoServico =
            Contexto.get().processoServico();

    @FXML
    private ComboBox<ProcessoAdocao> campoProcesso;

    @FXML
    private DatePicker campoData;

    @FXML
    private Label mensagem;

    public TelaAgendarVisitaController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {

        mensagem.setText("");

        campoData.setValue(LocalDate.now().plusDays(7));

        campoProcesso.setConverter(new StringConverter<ProcessoAdocao>() {

            @Override
            public String toString(ProcessoAdocao processo) {

                if (processo == null) {
                    return "";
                }

                Animal animal = processo.getAnimal();

                if (animal == null) {
                    return "Processo " + processo.getId();
                }

                return animal.getNome()
                        + " - Processo " + processo.getId();
            }

            @Override
            public ProcessoAdocao fromString(String texto) {
                return null;
            }
        });

        carregarProcessos();
    }

    private void carregarProcessos() {

        campoProcesso.getItems().clear();

        Adotante adotante = Contexto.get().adotanteLogado().orElse(null);

        if (adotante == null) {
            mensagem.setStyle("-fx-text-fill: #c0392b;");
            mensagem.setText(
                    "Entre como adotante para agendar visitas."
            );
            return;
        }

        // Processos aprovados do adotante logado que ainda não têm
        // pedidos de visita suficientes (contando os que aguardam o abrigo).
        processoServico.listarPorAdotante(adotante.getId()).stream()

                .filter(Objects::nonNull)

                .filter(processo ->
                        processo.getStatus() == StatusProcesso.APROVADO
                )

                .filter(ProcessoAdocao::precisaSolicitarVisitas)

                .filter(processo -> processo.getAnimal() != null)

                .forEach(processo ->
                        campoProcesso.getItems().add(processo)
                );

        // Se veio de "Minhas visitas" com uma visita selecionada, já escolhe o processo dela
        navegador.selecionado(VisitaDoProcesso.class)
                .map(VisitaDoProcesso::processo)
                .filter(campoProcesso.getItems()::contains)
                .ifPresent(campoProcesso::setValue);

        if (campoProcesso.getItems().size() == 1) {
            campoProcesso.setValue(campoProcesso.getItems().get(0));
        }

        mensagem.setStyle("-fx-text-fill: #555555;");

        if (campoProcesso.getItems().isEmpty()) {
            mensagem.setText(
                    "Nenhum processo aprovado precisa de novas visitas."
            );
        } else {
            mensagem.setText(
                    "São necessárias " + ProcessoAdocao.getMinVisitasPosAdocao()
                            + " visitas nos próximos " + DIAS_LIMITE
                            + " dias. Cada pedido precisa ser aprovado pelo abrigo."
            );
        }
    }

    @FXML
    private void agendar() {

        try {

            ProcessoAdocao processo = campoProcesso.getValue();

            if (processo == null) {
                throw new IllegalArgumentException(
                        "Selecione um processo de adoção."
                );
            }

            LocalDate data = campoData.getValue();

            validarData(data);

            Visita nova = new Visita(data);

            processoServico.agendarVisita(processo.getId(), nova);

            String sucesso = "Visita pedida para "
                    + data.format(FORMATO)
                    + " (" + processo.getAnimal().getNome()
                    + "). Aguarde a aprovação do abrigo.";

            campoProcesso.setValue(null);

            carregarProcessos();

            mensagem.setStyle("-fx-text-fill: #2e7d32;");
            mensagem.setText(sucesso);

        } catch (IllegalArgumentException | IllegalStateException e) {

            mensagem.setStyle("-fx-text-fill: #c0392b;");
            mensagem.setText(e.getMessage());
        }
    }

    private void validarData(LocalDate data) {

        if (data == null) {
            throw new IllegalArgumentException(
                    "Selecione a data da visita."
            );
        }

        if (data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "A data da visita não pode estar no passado."
            );
        }

        if (!data.isBefore(LocalDate.now().plusDays(DIAS_LIMITE))) {
            throw new IllegalArgumentException(
                    "A visita deve ocorrer dentro dos próximos "
                            + DIAS_LIMITE + " dias."
            );
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}