package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.servico.ContaAdministrador;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.Optional;

/**
 * Controller da TelaLogin.fxml (Login).
 * Confere e-mail e senha no perfil escolhido e guarda no Contexto quem entrou.
 */
public class TelaLoginController {

    private static final String ADOTANTE = "Adotante";
    private static final String ABRIGO = "Abrigo";
    private static final String ADMINISTRADOR = "Administrador";

    private final Navegador navegador;
    private final Contexto contexto;

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private ComboBox<String> campoPerfil;
    @FXML private Label mensagem;

    public TelaLoginController(Navegador navegador, Contexto contexto) {
        this.navegador = navegador;
        this.contexto = contexto;
    }

    @FXML
    private void initialize() {
        campoPerfil.getItems().addAll(ADOTANTE, ABRIGO, ADMINISTRADOR);
        campoPerfil.setValue(ADOTANTE);
    }

    /** Botão "Entrar" (também é acionado pela tecla Enter). */
    @FXML
    private void entrar() {
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            mensagem.setText("Preencha o e-mail e a senha.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            mensagem.setText("Informe um e-mail válido.");
            campoEmail.requestFocus();
            return;
        }

        String perfil = campoPerfil.getValue();
        boolean entrou = switch (perfil) {
            case ABRIGO -> entrarComoAbrigo(email, senha);
            case ADMINISTRADOR -> entrarComoAdministrador(email, senha);
            default -> entrarComoAdotante(email, senha);
        };
        if (!entrou) {
            return;
        }
        mensagem.setText("");
        navegador.ir(telaPrincipalDo(perfil));
    }

    @FXML
    private void abrirCadastro() {
        navegador.ir("TelaCadastro.fxml");
    }

    private boolean entrarComoAdotante(String email, String senha) {
        Optional<Adotante> adotante = contexto.adotanteServico().autenticar(email, senha);
        if (adotante.isEmpty()) {
            credenciaisIncorretas(ADOTANTE);
            return false;
        }
        contexto.entrarComoAdotante(adotante.get());
        return true;
    }

    private boolean entrarComoAbrigo(String email, String senha) {
        Optional<Abrigo> abrigo = contexto.abrigoServico().autenticar(email, senha);
        if (abrigo.isEmpty()) {
            credenciaisIncorretas(ABRIGO);
            return false;
        }
        // Abrigo só entra depois que o administrador aprovar o cadastro
        if (abrigo.get().getStatus() != StatusAbrigo.APROVADO) {
            mensagem.setText(abrigo.get().getStatus() == StatusAbrigo.PENDENTE
                    ? "Cadastro do abrigo aguardando aprovação do administrador."
                    : "O cadastro deste abrigo foi negado pelo administrador.");
            return false;
        }
        contexto.entrarComoAbrigo(abrigo.get());
        return true;
    }

    private boolean entrarComoAdministrador(String email, String senha) {
        if (!ContaAdministrador.confere(email, senha)) {
            credenciaisIncorretas(ADMINISTRADOR);
            return false;
        }
        contexto.encerrarSessao(); // administrador não tem cadastro de adotante/abrigo
        return true;
    }

    private void credenciaisIncorretas(String perfil) {
        mensagem.setText("E-mail ou senha incorretos para o perfil " + perfil + ".");
        campoSenha.clear();
        campoSenha.requestFocus();
    }

    private static String telaPrincipalDo(String perfil) {
        return switch (perfil) {
            case ABRIGO -> "TelaPrincipalAbrigo.fxml";
            case ADMINISTRADOR -> "TelaPrincipalAdministrador.fxml";
            default -> "TelaPrincipalAdotante.fxml";
        };
    }
}
