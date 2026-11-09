package br.com.petadote.servico;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * REQ03: lê as chaves da TheDogAPI/TheCatAPI do arquivo api.properties
 * (fora do Git) ou de variáveis de ambiente.
 */
public class ConfigApi {

    private static final Path ARQUIVO = Path.of("api.properties");
    private final Properties props = new Properties();

    public ConfigApi() {
        if (Files.exists(ARQUIVO)) {
            try (InputStream in = Files.newInputStream(ARQUIVO)) {
                props.load(in);
            } catch (IOException e) {
                // arquivo ilegível: segue só com as variáveis de ambiente
            }
        }
    }

    /** Devolve a chave pedida (ex.: "DOG_API_KEY") ou null se não estiver configurada. */
    public String chave(String nome) {
        String valor = props.getProperty(nome);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(nome);
        }
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}