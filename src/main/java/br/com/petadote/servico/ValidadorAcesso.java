package br.com.petadote.servico;

import java.util.stream.Stream;

/** Regras de e-mail e senha usadas no cadastro de Adotante e de Abrigo. */
final class ValidadorAcesso {

    static final int TAMANHO_MINIMO_SENHA = 6;

    private ValidadorAcesso() {
    }

    /**
     * @param emailsCadastrados e-mails já usados por outros cadastros do mesmo perfil
     */
    static void validar(String email, String senha, Stream<String> emailsCadastrados) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        if (emailsCadastrados.anyMatch(email::equalsIgnoreCase)) {
            throw new IllegalArgumentException("Já existe um cadastro com este e-mail.");
        }
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }
}
