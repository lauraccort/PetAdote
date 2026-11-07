package br.com.petadote.servico;

/**
 * O administrador não se cadastra pela tela de cadastro: usa esta conta fixa.
 */
public final class ContaAdministrador {

    public static final String EMAIL = "admin@petadote.com";
    public static final String SENHA = "admin123";

    private ContaAdministrador() {
    }

    public static boolean confere(String email, String senha) {
        return EMAIL.equalsIgnoreCase(email) && SENHA.equals(senha);
    }
}
