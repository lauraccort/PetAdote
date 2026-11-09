
package br.com.petadote.gui;

import br.com.petadote.model.Adotante;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.servico.IProcessoAdocaoServico;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.List;
import java.util.Optional;

/** REQ05: histórico de processos de adoção de um adotante (visão do abrigo). */
public class TelaHistoricoAdotanteController {

    private final Navegador navegador;
    private final IProcessoAdocaoServico processoServico;

    @FXML
    private TableView<ProcessoAdocao> tabelaHistorico;

    @FXML
    private TableColumn<ProcessoAdocao, Long> colunaId;

    @FXML
    private TableColumn<ProcessoAdocao, String> colunaProcesso;

    @FXML
    private TableColumn<ProcessoAdocao, String> colunaStatus;

    @FXML
    private TableColumn<ProcessoAdocao, String> colunaData;

    @FXML
    private Label mensagem;

    public TelaHistoricoAdotanteController(
            Navegador navegador,
            IProcessoAdocaoServico processoServico) {

        this.navegador = navegador;
        this.processoServico = processoServico;
    }

    @FXML
    private void initialize() {

        colunaId.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        colunaProcesso.setCellValueFactory(cell -> {
            ProcessoAdocao processo = cell.getValue();

            String nomeAnimal = processo.getAnimal() != null
                    ? processo.getAnimal().getNome()
                    : "Animal não informado";

            return new ReadOnlyStringWrapper(nomeAnimal);
        });

        colunaStatus.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        String.valueOf(cell.getValue().getStatus())));

        colunaData.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        String.valueOf(cell.getValue().getDataAbertura())));

        carregarHistorico();
    }

    private void carregarHistorico() {

        Optional<Adotante> selecionado =
                navegador.selecionado(Adotante.class);

        if (selecionado.isEmpty()) {
            mensagem.setText("Nenhum adotante foi selecionado.");
            return;
        }

        try {
            Long idAdotante = selecionado.get().getId();

            List<ProcessoAdocao> processos =
                    processoServico.listarPorAdotante(idAdotante);

            tabelaHistorico.setItems(
                    FXCollections.observableArrayList(processos));

            if (processos.isEmpty()) {
                mensagem.setText(
                        "Este adotante ainda não possui processos de adoção.");
            } else {
                mensagem.setText("");
            }

        } catch (Exception e) {
            mensagem.setText(
                    "Erro ao carregar histórico: " + e.getMessage());
        }
    }

    @FXML
    private void abrirListarSolicitacoesPendentes() {
        navegador.ir("TelaListarSolicitacoesPendentes.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}

