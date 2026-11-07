package br.com.petadote.gui;

import br.com.petadote.Contexto;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Troca as telas da janela principal e cria os controllers.
 * Guarda o histórico de telas abertas para o botão "Voltar".
 */
public class Navegador {

    public static final String TELA_INICIAL = "TelaLogin.fxml";

    private final Stage stage;
    private final Contexto contexto;
    private final Deque<String> historico = new ArrayDeque<>();

    public Navegador(Stage stage, Contexto contexto) {
        this.stage = stage;
        this.contexto = contexto;
    }

    /**
     * Abre a tela. Se ela já está no histórico (ex.: voltar para a tela principal
     * ou para o login), volta até ela em vez de empilhar de novo.
     */
    public void ir(String fxml) {
        if (historico.contains(fxml)) {
            while (!historico.peek().equals(fxml)) {
                historico.pop();
            }
        } else {
            historico.push(fxml);
        }
        mostrar(fxml);
    }

    /** Volta para a tela anterior do histórico. */
    public void voltar() {
        if (historico.size() > 1) {
            historico.pop();
            mostrar(historico.peek());
        }
    }

    /** Volta para o login, descartando o histórico. */
    public void sair() {
        historico.clear();
        ir(TELA_INICIAL);
    }

    private void mostrar(String fxml) {
        URL url = Navegador.class.getResource(fxml);
        if (url == null) {
            throw new IllegalArgumentException("Tela não encontrada: " + fxml);
        }
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(this::criarController);
        try {
            Parent raiz = loader.load();
            if (stage.getScene() == null) {
                Scene cena = new Scene(raiz);
                cena.getStylesheets().add(Navegador.class.getResource("petadote.css").toExternalForm());
                stage.setScene(cena);
            } else {
                stage.getScene().setRoot(raiz); // mantém o tamanho da janela
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao abrir " + fxml, e);
        }
    }

    /**
     * Cria o controller pedido pelo fx:controller do .fxml.
     * Telas que precisam de serviços ganham um "if" aqui, recebendo o serviço do Contexto.
     * As demais só recebem o Navegador pelo construtor.
     */
    private Object criarController(Class<?> tipo) {
        if (tipo == TelaPrincipalController.class) {
            return new TelaPrincipalController(contexto.animalServico());
        }
        if (tipo == TelaLoginController.class) {
            return new TelaLoginController(this, contexto.adotanteServico(), contexto.abrigoServico());
        }
        if (tipo == TelaAcompanharPosAdocaoController.class) {
            return new TelaAcompanharPosAdocaoController(this, contexto.processoServico());
        }
        if (tipo == TelaAprovarCadastroAbrigoController.class) {
            return new TelaAprovarCadastroAbrigoController(this, contexto.abrigoServico());
        }
        if (tipo == TelaListarProcessosAdocaoController.class) {
            return new TelaListarProcessosAdocaoController(this, contexto.processoServico());
        }
        if (tipo == TelaRelatorioAdocoesConcluidasController.class) {
            return new TelaRelatorioAdocoesConcluidasController(this, contexto.processoServico());
        }
        if (tipo == TelaListarSolicitacoesPendentesController.class) {
            return new TelaListarSolicitacoesPendentesController(this, contexto.processoServico());
        }
        if (tipo == TelaAvaliarProcessoAdocaoController.class) {
            return new TelaAvaliarProcessoAdocaoController(this, contexto.processoServico());
        }
        if (tipo == TelaListarVisitasController.class) {
            return new TelaListarVisitasController(this, contexto.processoServico());
        }
        if (tipo == TelaCadastroController.class) {
            return new TelaCadastroController(this, contexto.adotanteServico(), contexto.abrigoServico());
        }
        try {
            return tipo.getConstructor(Navegador.class).newInstance(this);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Não foi possível criar " + tipo.getName()
                    + ": ele precisa de um construtor público que receba o Navegador.", e);
        }
    }
}
