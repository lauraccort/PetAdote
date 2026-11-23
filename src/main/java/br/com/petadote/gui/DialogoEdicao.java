package br.com.petadote.gui;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

final class DialogoEdicao extends Dialog<ButtonType> {

    private final GridPane campos = new GridPane();
    private final Label erro = new Label();

    DialogoEdicao(Window dono, String titulo) {
        initOwner(dono);
        setTitle(titulo);
        campos.setPadding(new Insets(12));
        campos.setHgap(12);
        campos.setVgap(8);

        erro.setWrapText(true);
        erro.setStyle("-fx-text-fill: #c0392b;");
        erro.setMaxWidth(320);
        getDialogPane().setContent(campos);
    }

    void adicionarCampo(String rotulo, Control campo) {
        Label label = new Label(rotulo);
        label.setLabelFor(campo);
        campos.addRow(campos.getRowCount(), label, campo);
    }

    boolean editar(Runnable salvar) {
        campos.add(erro, 0, campos.getRowCount(), 2, 1);
        ButtonType botaoSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(botaoSalvar, cancelar);
        getDialogPane().lookupButton(botaoSalvar).addEventFilter(ActionEvent.ACTION, evento -> {
            try {
                salvar.run();
            } catch (IllegalArgumentException | IllegalStateException e) {
                erro.setText(e.getMessage());
                evento.consume();
            }
        });
        return showAndWait().filter(botaoSalvar::equals).isPresent();
    }
}
