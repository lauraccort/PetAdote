package br.com.petadote.gui;

import br.com.petadote.Contexto;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Ponto de entrada da GUI. Cria o Navegador (com os serviços do Contexto)
 * e abre a tela de login.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        Navegador navegador = new Navegador(stage, Contexto.get());
        stage.setTitle("PetAdote");
        navegador.ir(Navegador.TELA_INICIAL);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
