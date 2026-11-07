module br.com.petadote {
    requires javafx.controls;
    requires javafx.fxml;

    // O FXMLLoader precisa acessar os controllers (@FXML) por reflexão.
    opens br.com.petadote.gui to javafx.fxml;
    // O JavaFX precisa instanciar a classe App (extends Application).
    exports br.com.petadote.gui;
}
