package br.com.petadote.repositorio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Guarda os dados em UM arquivo (dados/petadote.dat) no lugar de um banco.
 * Os 4 repositorios sao gravados juntos, para que objetos compartilhados
 * (ex.: o mesmo Animal no abrigo e no repositorio de animais) continuem
 * sendo o mesmo objeto depois de ler o arquivo.
 */
public final class Persistencia {

    private static final Path ARQUIVO = Path.of("dados", "petadote.dat");
    private static boolean carregando = false;

    private Persistencia() {
    }

    /**
     * Le o arquivo (se existir) e preenche os repositorios. Chamar ao iniciar.
     *
     * @return true se carregou dados do arquivo; false se ele nao existe
     *         (primeira execucao) e os repositorios continuam vazios.
     */
    public static synchronized boolean carregar() {
        if (!Files.exists(ARQUIVO)) {
            return false;
        }

        carregando = true;
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(ARQUIVO)))) {

            Object[] pacote = (Object[]) in.readObject();

            AbrigoRepositorio.getInstance().restaurar((Object[]) pacote[0]);
            AnimalRepositorio.getInstance().restaurar((Object[]) pacote[1]);
            AdotanteRepositorio.getInstance().restaurar((Object[]) pacote[2]);
            ProcessoAdocaoRepositorio.getInstance().restaurar((Object[]) pacote[3]);
            return true;

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Não foi possível ler " + ARQUIVO
                    + ". Se o código mudou, apague esse arquivo. Detalhe: "
                    + e.getMessage(), e);
        } finally {
            carregando = false;
        }
    }

    /**
     * Enquanto estiver marcado, salvar() nao faz nada. Usado ao semear os dados
     * de exemplo, para gravar o arquivo uma unica vez no final.
     */
    public static synchronized void marcarCarregando(boolean valor) {
        carregando = valor;
    }

    /** Grava o estado atual dos 4 repositorios. */
    public static synchronized void salvar() {
        if (carregando) {
            return;
        }

        try {
            Files.createDirectories(ARQUIVO.getParent());
            Path temporario = ARQUIVO.resolveSibling("petadote.tmp");

            try (ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporario)))) {

                out.writeObject(new Object[]{
                        AbrigoRepositorio.getInstance().exportar(),
                        AnimalRepositorio.getInstance().exportar(),
                        AdotanteRepositorio.getInstance().exportar(),
                        ProcessoAdocaoRepositorio.getInstance().exportar()
                });
            }

            // grava num temporario e troca, para nao corromper se der erro
            Files.move(temporario, ARQUIVO, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gravar " + ARQUIVO
                    + ": " + e.getMessage(), e);
        }
    }
}