package br.com.petadote.gui;

import br.com.petadote.servico.IProcessoAdocaoServico;
import javafx.fxml.FXML;

/**
 * Controller da TelaListarProcessosAdocao.fxml (Processos de adoção).
 * Menu que leva às telas de solicitações pendentes e de finalizar processo.
 */
public class TelaListarProcessosAdocaoController {

    private final Navegador navegador;

    public TelaListarProcessosAdocaoController(Navegador navegador, IProcessoAdocaoServico processoServico) {
        this.navegador = navegador;
    }

    @FXML
    private void abrirListarSolicitacoesPendentes() {
        navegador.ir("TelaListarSolicitacoesPendentes.fxml");
    }

    @FXML
    private void abrirFinalizarProcessoAdocao() {
        navegador.ir("TelaFinalizarProcessoAdocao.fxml");
    }

    @FXML
    private void abrirAcompanharPosAdocao() {
        navegador.ir("TelaAcompanharPosAdocao.fxml");
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }
}
