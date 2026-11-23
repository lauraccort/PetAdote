package br.com.petadote.gui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;


final class Combos {

    private Combos() {
    }


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
