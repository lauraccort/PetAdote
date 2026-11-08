package br.com.petadote.repositorio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class Persistencia {

    private static final Path ARQUIVO = Path.of("dados", "petadote.dat");
    private static boolean carregando = false;

    private Persistencia() {
    }

    /** Carrega o arquivo, se existir. Devolve true quando havia dados. */
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

            Files.move(temporario, ARQUIVO, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gravar " + ARQUIVO
                    + ": " + e.getMessage(), e);
        }
    }

    /** Usado pela semeadura inicial para não gravar a cada salvar(). */
    public static void marcarCarregando(boolean valor) {
        carregando = valor;
    }
}