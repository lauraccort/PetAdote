package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Adotante;
import br.com.petadote.servico.IAbrigoServico;
import br.com.petadote.servico.IAdotanteServico;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * Controller da TelaCadastro.fxml (Cadastro de Adotante ou Abrigo).
 */
public class TelaCadastroController {

    private static final String ADOTANTE = "Adotante";
    private static final String ABRIGO = "Abrigo";

    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final IAdotanteServico adotanteServico;
    private final IAbrigoServico abrigoServico;

    @FXML private ComboBox<String> campoPerfil;
    @FXML private Node camposComuns;
    @FXML private Node camposAdotante;
    @FXML private Node camposAbrigo;
    @FXML private TextField campoNome;
    @FXML private TextField campoEndereco;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private PasswordField campoConfirmarSenha;
    @FXML private TextField campoCpf;
    @FXML private TextField campoQuantAnimais;
    @FXML private TextField campoCapacidade;
    @FXML private Label mensagem;
    @FXML private Button botaoCadastrar;

    public TelaCadastroController(Navegador navegador, IAdotanteServico adotanteServico, IAbrigoServico abrigoServico) {
        this.navegador = navegador;
        this.adotanteServico = adotanteServico;
        this.abrigoServico = abrigoServico;
    }

    @FXML
    private void initialize() {
        campoPerfil.getItems().addAll(ADOTANTE, ABRIGO);

        // Campo escondido não ocupa espaço na tela.
        for (Node grupo : new Node[] {camposComuns, camposAdotante, camposAbrigo}) {
            grupo.managedProperty().bind(grupo.visibleProperty());
        }
        campoPerfil.valueProperty().addListener((obs, anterior, perfil) -> mostrarCampos(perfil));
        mostrarCampos(null);
    }

    /** Mostra só os campos do perfil escolhido. */
    private void mostrarCampos(String perfil) {
        camposComuns.setVisible(perfil != null);
        camposAdotante.setVisible(ADOTANTE.equals(perfil));
        camposAbrigo.setVisible(ABRIGO.equals(perfil));
        botaoCadastrar.setDisable(perfil == null);
        mensagem.setText("");
    }

    @FXML
    private void cadastrar() {
        try {
            String email = campoEmail.getText().trim();
            String senha = campoSenha.getText();
            if (!senha.equals(campoConfirmarSenha.getText())) {
                throw new IllegalArgumentException("As senhas não conferem.");
            }
            if (ADOTANTE.equals(campoPerfil.getValue())) {
                int quantAnimais = lerNumero(campoQuantAnimais, "Animais que já possui", 0);
                Adotante novo = new Adotante(
                        campoNome.getText().trim(), campoCpf.getText().trim(),
                        campoEndereco.getText().trim(), quantAnimais);
                novo.setEmail(email);
                novo.setSenha(senha);
                Adotante adotante = adotanteServico.cadastrar(novo);
                sucesso("Adotante cadastrado: " + adotante.getNome() + " (id " + adotante.getId() + ").");
            } else {
                int capacidade = lerNumero(campoCapacidade, "Capacidade de animais", null);
                Abrigo novo = new Abrigo(campoNome.getText().trim(), campoEndereco.getText().trim(), capacidade);
                novo.setEmail(email);
                novo.setSenha(senha);
                Abrigo abrigo = abrigoServico.cadastrar(novo);
                sucesso("Abrigo cadastrado: " + abrigo.getNome() + " (id " + abrigo.getId() + ").");
            }
        } catch (IllegalArgumentException e) {
            // Erros de validação (nome vazio, CPF inválido, e-mail repetido, senha curta...)
            mensagem.setTextFill(COR_ERRO);
            mensagem.setText(e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    /** Converte o texto do campo em número; usa o padrão quando vazio (null = obrigatório). */
    private static int lerNumero(TextField campo, String nomeCampo, Integer padrao) {
        String texto = campo.getText().trim();
        if (texto.isEmpty() && padrao != null) {
            return padrao;
        }
        try {
            int valor = Integer.parseInt(texto);
            if (valor < 0) {
                throw new NumberFormatException();
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um número válido em \"" + nomeCampo + "\".");
        }
    }

    private void sucesso(String texto) {
        mensagem.setTextFill(COR_SUCESSO);
        mensagem.setText(texto + " Volte e faça login.");
        for (TextField campo : new TextField[] {campoNome, campoEndereco, campoEmail, campoSenha,
                campoConfirmarSenha, campoCpf, campoQuantAnimais, campoCapacidade}) {
            campo.clear();
        }
    }
}
