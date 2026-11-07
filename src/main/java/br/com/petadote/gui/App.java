package br.com.petadote.gui;

import br.com.petadote.repositorio.AnimalRepositorio;
import br.com.petadote.servico.AnimalServicoImpl;
import br.com.petadote.servico.IAnimalServico;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ponto de entrada da GUI. Monta as camadas gui -> servico -> repositorio
 * e entrega o serviço ao controller da tela.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        IAnimalServico animalServico = new AnimalServicoImpl(new AnimalRepositorio());

        FXMLLoader loader = new FXMLLoader(App.class.getResource("TelaPrincipal.fxml"));
        // Cria o controller passando o serviço (a tela não conhece o repositório).
        loader.setControllerFactory(tipo -> new TelaPrincipalController(animalServico));

        stage.setTitle("PetAdote");
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
