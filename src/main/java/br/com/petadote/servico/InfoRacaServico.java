package br.com.petadote.servico;

import br.com.petadote.model.InfoRaca;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** REQ03: busca porte, peso e temperamento da raça na TheDogAPI e na TheCatAPI. */
public class InfoRacaServico {

    private static final String URL_CAES = "https://api.thedogapi.com/v1/breeds/search?q=";
    private static final String URL_GATOS = "https://api.thecatapi.com/v1/breeds/search?q=";
    private static final Pattern NUMERO = Pattern.compile("\\d+(?:\\.\\d+)?");

    private final ConfigApi config;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public InfoRacaServico() {
        this(new ConfigApi());
    }

    public InfoRacaServico(ConfigApi config) {
        this.config = config;
    }

    /** Porte estimado pelo peso: até 10 kg pequeno, até 25 kg médio, acima disso grande. */
    public Optional<InfoRaca> buscarCao(String raca) {
        return buscar(URL_CAES, "DOG_API_KEY", raca, 10, 25);
    }

    /** Porte estimado pelo peso: até 3,5 kg pequeno, até 5,5 kg médio, acima disso grande. */
    public Optional<InfoRaca> buscarGato(String raca) {
        return buscar(URL_GATOS, "CAT_API_KEY", raca, 3.5, 5.5);
    }

    /** Vazio se faltar chave, a raça não existir ou a API falhar: o cadastro segue sem as informações. */
    private Optional<InfoRaca> buscar(String urlBase, String nomeChave, String raca,
                                      double limitePequeno, double limiteMedio) {
        String chave = config.chave(nomeChave);
        if (chave == null || raca == null || raca.isBlank()) {
            return Optional.empty();
        }
        try {
            String consulta = URLEncoder.encode(raca.trim(), StandardCharsets.UTF_8);
            HttpRequest pedido = HttpRequest.newBuilder(URI.create(urlBase + consulta))
                    .timeout(Duration.ofSeconds(8))
                    .header("x-api-key", chave)
                    .GET()
                    .build();
            HttpResponse<String> resposta = http.send(pedido, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() != 200) {
                return Optional.empty();
            }
            return interpretar(resposta.body(), limitePequeno, limiteMedio);
        } catch (IOException | RuntimeException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    private static Optional<InfoRaca> interpretar(String json, double limitePequeno, double limiteMedio) {
        JsonArray racas = JsonParser.parseString(json).getAsJsonArray();
        if (racas.isEmpty()) {
            return Optional.empty();
        }
        JsonObject primeira = racas.get(0).getAsJsonObject();

        double peso = pesoMedio(texto(primeira.getAsJsonObject("weight"), "metric"));
        String temperamento = texto(primeira, "temperament");
        if (temperamento == null || temperamento.isBlank()) {
            temperamento = "Não informado";
        }

        String porte;
        if (peso <= 0) {
            porte = "Não informado";
        } else if (peso <= limitePequeno) {
            porte = "Pequeno";
        } else if (peso <= limiteMedio) {
            porte = "Médio";
        } else {
            porte = "Grande";
        }
        return Optional.of(new InfoRaca(porte, peso, temperamento));
    }

    /** "29 - 36" vira 32,5; "6" vira 6; texto sem número vira 0. */
    private static double pesoMedio(String texto) {
        if (texto == null) {
            return 0;
        }
        Matcher m = NUMERO.matcher(texto);
        List<Double> numeros = new ArrayList<>();
        while (m.find()) {
            numeros.add(Double.parseDouble(m.group()));
        }
        if (numeros.isEmpty()) {
            return 0;
        }
        return (numeros.get(0) + numeros.get(numeros.size() - 1)) / 2.0;
    }

    private static String texto(JsonObject objeto, String campo) {
        if (objeto == null) {
            return null;
        }
        JsonElement elemento = objeto.get(campo);
        return (elemento == null || elemento.isJsonNull()) ? null : elemento.getAsString();
    }
}