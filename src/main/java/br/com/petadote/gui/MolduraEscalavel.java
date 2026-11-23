package br.com.petadote.gui;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;


final class MolduraEscalavel extends Pane {

    static final double LARGURA_BASE = 720;
    static final double ALTURA_BASE = 560;

    private final Scale escala = new Scale(1, 1, 0, 0);
    private Parent tela;
    private double proporcao = 1;

    MolduraEscalavel() {
        getStyleClass().add("moldura");
        setPrefSize(LARGURA_BASE, ALTURA_BASE);
    }


    void mostrar(Parent novaTela) {
        mostrar(novaTela, 1);
    }



    void mostrar(Parent novaTela, double proporcao) {
        this.proporcao = proporcao;
        if (tela != null) {
            tela.getTransforms().remove(escala);
        }
        tela = novaTela;
        tela.getTransforms().add(escala);
        getChildren().setAll(tela);
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        if (tela == null || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        double fatorJanela = Math.min(getWidth() / LARGURA_BASE, getHeight() / ALTURA_BASE);
        double fator = Math.max(Math.min(fatorJanela, 1), fatorJanela * proporcao);
        escala.setX(fator);
        escala.setY(fator);
        tela.relocate(0, 0);
        // A tela é organizada no tamanho "sem escala" que preenche a janela inteira
        double largura = getWidth() / fator;
        double altura = getHeight() / fator;
        if (tela instanceof Region regiao) {
            regiao.resize(largura, altura);
        } else {
            tela.resize(largura, altura);
        }
    }
}
