package br.com.petadote.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller da TelaLogin.fxml (Login).
 */
public class TelaLoginController {

    private static final String ADOTANTE = "Adotante";
    private static final String ABRIGO = "Abrigo";
    private static final String ADMINISTRADOR = "Administrador";

    private final Navegador navegador;

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private ComboBox<String> campoPerfil;
    @FXML private Label mensagem;

    public TelaLoginController(Navegador navegador) {
        this.navegador = navegador;
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

        // TODO: conferir e-mail e senha quando existir cadastro de usuários.
        mensagem.setText("");
        navegador.ir(telaPrincipalDo(campoPerfil.getValue()));
    }

    @FXML
    private void abrirCadastro() {
        navegador.ir("TelaCadastro.fxml");
    }

    private static String telaPrincipalDo(String perfil) {
        return switch (perfil) {
            case ABRIGO -> "TelaPrincipalAbrigo.fxml";
            case ADMINISTRADOR -> "TelaPrincipalAdministrador.fxml";
            default -> "TelaPrincipalAdotante.fxml";
        };
    }
}
