package br.com.petadote.gui;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Animal;
import br.com.petadote.model.Cao;
import br.com.petadote.model.Gato;
import br.com.petadote.model.OutraEspecie;
import br.com.petadote.model.StatusAbrigo;
import br.com.petadote.model.StatusAnimal;
import br.com.petadote.servico.IAbrigoServico;
import br.com.petadote.servico.IAnimalServico;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.util.Comparator;

/**
 * Controller da TelaCadastroAnimal.fxml (REQ02: cadastro de animal vinculado a um abrigo).
 * Cão e Gato guardam a raça; "Outra" guarda qual é a espécie no mesmo campo.
 * Antes de salvar confere a capacidade do abrigo (REQ19).
 */
public class TelaCadastroAnimalController {

    private static final String CAO = "Cão";
    private static final String GATO = "Gato";
    private static final String OUTRA = "Outra";
    private static final Color COR_ERRO = Color.web("#c0392b");
    private static final Color COR_SUCESSO = Color.web("#2e7d32");

    private final Navegador navegador;
    private final IAnimalServico animalServico;
    private final IAbrigoServico abrigoServico;

    @FXML private TextField campoNome;
    @FXML private ComboBox<String> campoEspecie;
    @FXML private Label rotuloRaca;
    @FXML private TextField campoRaca;
    @FXML private DatePicker campoNascimento;
    @FXML private ComboBox<StatusAnimal> campoStatus;
    @FXML private ComboBox<Abrigo> campoAbrigo;
    @FXML private Label mensagem;

    public TelaCadastroAnimalController(Navegador navegador, IAnimalServico animalServico, IAbrigoServico abrigoServico) {
        this.navegador = navegador;
        this.animalServico = animalServico;
        this.abrigoServico = abrigoServico;
    }

    @FXML
    private void initialize() {
        campoEspecie.getItems().addAll(CAO, GATO, OUTRA);
        campoEspecie.valueProperty().addListener((obs, anterior, especie) -> ajustarCampoRaca(especie));

        // No cadastro o animal entra disponível ou em tratamento; os outros status vêm do processo de adoção
        campoStatus.getItems().addAll(StatusAnimal.DISPONIVEL, StatusAnimal.EM_TRATAMENTO_VETERINARIO);
        campoStatus.setConverter(conversor(s -> s == StatusAnimal.DISPONIVEL ? "Disponível" : "Em tratamento veterinário"));
        campoStatus.setValue(StatusAnimal.DISPONIVEL);

        campoAbrigo.setConverter(conversor(a -> a.getNome() + " (" + a.getQuantidadeAnimais() + "/" + a.getCapacidade() + ")"));
        carregarAbrigos();
    }

    @FXML
    private void salvar() {
        String nome = campoNome.getText().trim();
        String especie = campoEspecie.getValue();
        String raca = campoRaca.getText().trim();
        LocalDate nascimento = campoNascimento.getValue();
        Abrigo abrigo = campoAbrigo.getValue();

        if (nome.isEmpty()) {
            erro("Informe o nome do animal.");
            return;
        }
        if (especie == null) {
            erro("Selecione a espécie.");
            return;
        }
        if (OUTRA.equals(especie) && raca.isEmpty()) {
            erro("Informe qual é a espécie do animal.");
            return;
        }
        if (nascimento == null) {
            erro("Informe a data de nascimento.");
            return;
        }
        if (abrigo == null) {
            erro("Selecione o abrigo.");
            return;
        }
        if (!abrigo.temVaga()) { // REQ19
            erro("O abrigo " + abrigo.getNome() + " está lotado (capacidade: " + abrigo.getCapacidade() + ").");
            return;
        }

        Animal animal = switch (especie) {
            case CAO -> new Cao(nome, nascimento, raca);
            case GATO -> new Gato(nome, nascimento, raca);
            default -> new OutraEspecie(nome, nascimento, raca);
        };
        animal.setStatus(campoStatus.getValue());
        try {
            Animal salvo = animalServico.cadastrar(animal);
            if (!abrigoServico.adicionarAnimal(abrigo.getId(), salvo)) {
                erro("Não foi possível vincular o animal ao abrigo.");
                return;
            }
            limpar();
            mensagem.setTextFill(COR_SUCESSO);
            mensagem.setText(salvo.getNome() + " cadastrado no abrigo " + abrigo.getNome() + ".");
        } catch (IllegalArgumentException e) {
            erro(e.getMessage()); // ex.: data de nascimento no futuro
        }
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    /** Para "Outra" o campo de raça passa a pedir qual é a espécie. */
    private void ajustarCampoRaca(String especie) {
        if (OUTRA.equals(especie)) {
            rotuloRaca.setText("Qual espécie?");
            campoRaca.setPromptText("ex.: Coelho");
        } else {
            rotuloRaca.setText("Raça");
            campoRaca.setPromptText("ex.: Labrador");
        }
    }

    private void carregarAbrigos() {
        campoAbrigo.getItems().setAll(abrigoServico.listarTodos().stream()
                .filter(a -> a.getStatus() == StatusAbrigo.APROVADO)
                .sorted(Comparator.comparing(Abrigo::getNome))
                .toList());
    }

    private void limpar() {
        Abrigo abrigo = campoAbrigo.getValue();
        campoNome.clear();
        campoEspecie.setValue(null);
        campoRaca.clear();
        campoNascimento.setValue(null);
        campoStatus.setValue(StatusAnimal.DISPONIVEL);
        carregarAbrigos();              // atualiza a contagem de vagas
        campoAbrigo.setValue(abrigo);   // mantém o abrigo para cadastrar o próximo
    }

    private void erro(String texto) {
        mensagem.setTextFill(COR_ERRO);
        mensagem.setText(texto);
    }

    private static <T> StringConverter<T> conversor(java.util.function.Function<T, String> texto) {
        return new StringConverter<>() {
            @Override
            public String toString(T valor) {
                return valor == null ? "" : texto.apply(valor);
            }

            @Override
            public T fromString(String s) {
                return null; // combos não editáveis
            }
        };
    }
}
