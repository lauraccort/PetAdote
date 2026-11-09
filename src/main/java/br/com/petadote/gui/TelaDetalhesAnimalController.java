package br.com.petadote.gui;

import br.com.petadote.model.Animal;
import br.com.petadote.model.Cao;
import br.com.petadote.model.Gato;
import br.com.petadote.model.InfoRaca;
import br.com.petadote.model.StatusAnimal;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller da TelaDetalhesAnimal.fxml (Detalhes do animal).
 * Mostra o animal escolhido em "Animais disponíveis" e leva-o para iniciar o processo.
 */
public class TelaDetalhesAnimalController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Navegador navegador;
    private Animal animal;

    @FXML private TextField campoNome;
    @FXML private TextField campoEspecie;
    @FXML private TextField campoIdade;
    @FXML private Label infoExtra;
    @FXML private Label mensagem;
    @FXML private Button btnIniciarProcesso;

    public TelaDetalhesAnimalController(Navegador navegador) {
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        Optional<Animal> escolhido = navegador.selecionado(Animal.class);
        if (escolhido.isEmpty()) {
            mensagem.setText("Nenhum animal selecionado. Volte e escolha um animal na lista.");
            btnIniciarProcesso.setDisable(true);
            infoExtra.setText("");
            return;
        }
        animal = escolhido.get();
        campoNome.setText(animal.getNome());
        campoEspecie.setText(TextoAnimal.especieERaca(animal));
        campoIdade.setText(TextoAnimal.idade(animal.getDataNascimento())
                + (animal.getDataNascimento() == null ? "" : " (nascido em " + animal.getDataNascimento().format(FORMATO) + ")"));
        infoExtra.setText(textoExtra(animal));

        if (animal.getStatus() != StatusAnimal.DISPONIVEL) { // REQ14
            mensagem.setText(animal.getNome() + " não está disponível para adoção no momento ("
                    + animal.getStatus().getDescricao() + ").");
            btnIniciarProcesso.setDisable(true);
        }
    }

    @FXML
    private void abrirIniciarProcessoAdocao() {
        navegador.ir("TelaIniciarProcessoAdocao.fxml", animal);
    }

    @FXML
    private void voltar() {
        navegador.voltar();
    }

    /** Abrigo onde está e, se houver, as características da raça (REQ03). */
    private static String textoExtra(Animal animal) {
        StringBuilder texto = new StringBuilder("Abrigo: ")
                .append(animal.getAbrigo() == null ? "não informado" : animal.getAbrigo().getNome());
        InfoRaca info = animal instanceof Cao cao ? cao.getInfoRaca()
                : animal instanceof Gato gato ? gato.getInfoRaca() : null;
        if (info != null) {
            texto.append("\nRaça: porte ").append(info.getPorte())
                    .append(" · peso médio ").append(String.format("%.1f kg", info.getPesoMedio()))
                    .append(" · ").append(info.getTemperamento());
        }
        return texto.toString();
    }
}
