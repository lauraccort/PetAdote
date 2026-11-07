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
import java.util.Optional;
import java.util.Set;

/**
 * Troca as telas da janela principal e cria os controllers.
 * Guarda o histórico de telas abertas para o botão "Voltar".
 */
public class Navegador {

    public static final String TELA_INICIAL = "TelaLogin.fxml";

    /** Telas de cartão pequeno (login e cadastro) crescem menos que as outras em tela cheia. */
    private static final Set<String> TELAS_COMPACTAS = Set.of("TelaLogin.fxml", "TelaCadastro.fxml");
    private static final double PROPORCAO_COMPACTA = 0.8;

    private final Stage stage;
    private final Contexto contexto;
    private final Deque<String> historico = new ArrayDeque<>();
    private Object selecionado; // item levado de uma tela para a próxima
    private final MolduraEscalavel moldura = new MolduraEscalavel();

    public Navegador(Stage stage, Contexto contexto) {
        this.stage = stage;
        this.contexto = contexto;
    }

    /**
     * Abre a tela. Se ela já está no histórico (ex.: voltar para a tela principal
     * ou para o login), volta até ela em vez de empilhar de novo.
     */
    public void ir(String fxml) {
        ir(fxml, null); // sem item: não deixa a seleção de uma navegação anterior para trás
    }

    /**
     * Abre a tela levando o item escolhido na tela atual (ex.: o animal selecionado
     * na tabela). A próxima tela o recupera com {@link #selecionado(Class)}.
     * O item continua disponível ao voltar para esta tela com o botão "Voltar".
     */
    public void ir(String fxml, Object selecionado) {
        this.selecionado = selecionado;
        if (historico.contains(fxml)) {
            while (!historico.peek().equals(fxml)) {
                historico.pop();
            }
        } else {
            historico.push(fxml);
        }
        mostrar(fxml);
    }

    /** Item levado pela última navegação, se for do tipo pedido. */
    public <T> Optional<T> selecionado(Class<T> tipo) {
        return tipo.isInstance(selecionado) ? Optional.of(tipo.cast(selecionado)) : Optional.empty();
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
        contexto.encerrarSessao();
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
                // A moldura amplia a tela junto com a janela (tela cheia, maximizar, redimensionar)
                Scene cena = new Scene(moldura, MolduraEscalavel.LARGURA_BASE, MolduraEscalavel.ALTURA_BASE);
                cena.getStylesheets().add(Navegador.class.getResource("petadote.css").toExternalForm());
                stage.setScene(cena);
            }
            moldura.mostrar(raiz, TELAS_COMPACTAS.contains(fxml) ? PROPORCAO_COMPACTA : 1); // mantém o tamanho da janela
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
        if (tipo == TelaLoginController.class) {
            return new TelaLoginController(this, contexto);
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
        if (tipo == TelaFinalizarProcessoAdocaoController.class) {
            return new TelaFinalizarProcessoAdocaoController(this, contexto.processoServico());
        }
        if (tipo == TelaCadastroAnimalController.class) {
            return new TelaCadastroAnimalController(this, contexto.animalServico(), contexto.abrigoServico());
        }
        if (tipo == TelaConfirmarAgendamentoVisitaController.class) {
            return new TelaConfirmarAgendamentoVisitaController(this, contexto.processoServico());
        }
        if (tipo == TelaListarAnimaisAbrigoController.class) {
            return new TelaListarAnimaisAbrigoController(this, contexto.animalServico(), contexto.abrigoServico());
        }
        if (tipo == TelaListarAnimaisDisponiveisController.class) {
            return new TelaListarAnimaisDisponiveisController(this, contexto.animalServico());
        }
        if (tipo == TelaListarMeusProcessosController.class) {
            return new TelaListarMeusProcessosController(this, contexto);
        }
        if (tipo == TelaListarMinhasVisitasController.class) {
            return new TelaListarMinhasVisitasController(this, contexto);
        }
        if (tipo == TelaListarAbrigosController.class) {
            return new TelaListarAbrigosController(this, contexto.abrigoServico());
        }
        if (tipo == TelaExcluirAbrigoController.class) {
            return new TelaExcluirAbrigoController(this, contexto.abrigoServico());
        }
        if (tipo == TelaIniciarProcessoAdocaoController.class) {
            return new TelaIniciarProcessoAdocaoController(this, contexto);
        }
        if (tipo == TelaAvaliarVisitaController.class) {
            return new TelaAvaliarVisitaController(this, contexto.processoServico());
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
