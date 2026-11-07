package br.com.petadote.gui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/** Ajustes comuns para os ComboBox das telas. */
final class Combos {

    private Combos() {
    }

    /**
     * Corrige um defeito do JavaFX: depois que o combo volta para vazio (setValue(null)),
     * o texto de dica (promptText) some. Aqui ele volta a aparecer.
     */
    static <T> void mostrarDicaQuandoVazio(ComboBox<T> combo) {
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean vazio) {
                super.updateItem(item, vazio);
                if (vazio || item == null) {
                    setText(combo.getPromptText());
                } else {
                    setText(combo.getConverter().toString(item));
                }
            }
        });
    }
}
