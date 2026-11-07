package br.com.petadote.model;

public class InfoRaca {
    private String porte;
    private double pesoMedio;
    private String temperamento;

    public InfoRaca() {
    }

    public InfoRaca(String porte, double pesoMedio, String temperamento) {
        this.porte = porte;
        this.pesoMedio = pesoMedio;
        this.temperamento = temperamento;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public double getPesoMedio() {
        return pesoMedio;
    }

    public void setPesoMedio(double pesoMedio) {
        this.pesoMedio = pesoMedio;
    }

    public String getTemperamento() {
        return temperamento;
    }

    public void setTemperamento(String temperamento) {
        this.temperamento = temperamento;
    }
}