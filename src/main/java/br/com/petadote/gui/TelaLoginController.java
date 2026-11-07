package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.servico.ContaAdministrador;
import br.com.petadote.servico.IAbrigoServico;
import br.com.petadote.servico.IAdotanteServico;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.Optional;

/**
 * Controller da TelaLogin.fxml (Login).
 */
public class TelaLoginController {

    private static final String ADOTANTE = "Adotante";
    private static final String ABRIGO = "Abrigo";
    private static final String ADMINISTRADOR = "Administrador";

    private final Navegador navegador;
    private final IAdotanteServico adotanteServico;
    private final IAbrigoServico abrigoServico;

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private ComboBox<String> campoPerfil;
    @FXML private Label mensagem;

    public TelaLoginController(Navegador navegador, IAdotanteServico adotanteServico, IAbrigoServico abrigoServico) {
        this.navegador = navegador;
        this.adotanteServico = adotanteServico;
        this.abrigoServico = abrigoServico;
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
        if (ABRIGO.equals(perfil)) {
            // Abrigo só entra depois que o administrador aprovar o cadastro
            Optional<Abrigo> abrigo = abrigoServico.autenticar(email, senha);
            if (abrigo.isPresent() && abrigo.get().getStatus() != StatusAbrigo.APROVADO) {
                mensagem.setText(abrigo.get().getStatus() == StatusAbrigo.PENDENTE
                        ? "Cadastro do abrigo aguardando aprovação do administrador."
                        : "O cadastro deste abrigo foi negado pelo administrador.");
                return;
            }
        }
        if (!credenciaisConferem(perfil, email, senha)) {
            mensagem.setText("E-mail ou senha incorretos para o perfil " + perfil + ".");
            campoSenha.clear();
            campoSenha.requestFocus();
            return;
        }

        mensagem.setText("");
        navegador.ir(telaPrincipalDo(perfil));
    }

    @FXML
    private void abrirCadastro() {
        navegador.ir("TelaCadastro.fxml");
    }

    /** Procura o cadastro só no perfil escolhido. */
    private boolean credenciaisConferem(String perfil, String email, String senha) {
        return switch (perfil) {
            case ABRIGO -> abrigoServico.autenticar(email, senha).isPresent();
            case ADMINISTRADOR -> ContaAdministrador.confere(email, senha);
            default -> adotanteServico.autenticar(email, senha).isPresent();
        };
    }

    private static String telaPrincipalDo(String perfil) {
        return switch (perfil) {
            case ABRIGO -> "TelaPrincipalAbrigo.fxml";
            case ADMINISTRADOR -> "TelaPrincipalAdministrador.fxml";
            default -> "TelaPrincipalAdotante.fxml";
        };
    }
}
