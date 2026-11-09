package br.com.petadote.gui;

import br.com.petadote.Contexto;
import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Animal;
import br.com.petadote.model.ProcessoAdocao;

/**
 * Restringe as telas do abrigo aos dados do abrigo que fez login.
 * Sem abrigo logado (ex.: administrador), nada é filtrado.
 */
final class AbrigoLogado {

    private AbrigoLogado() {
    }

    /** O abrigo pode ver/alterar este abrigo? */
    static boolean pode(Abrigo abrigo) {
        return Contexto.get().abrigoLogado()
                .map(logado -> abrigo != null && logado.getId().equals(abrigo.getId()))
                .orElse(true);
    }

    /** O abrigo pode ver/alterar este animal? */
    static boolean pode(Animal animal) {
        return Contexto.get().abrigoLogado().isEmpty()
                || (animal != null && pode(animal.getAbrigo()));
    }

    /** O abrigo pode ver/alterar este processo (o animal é dele)? */
    static boolean pode(ProcessoAdocao processo) {
        return processo != null && pode(processo.getAnimal());
    }

    /** O abrigo pode ver/alterar esta visita? */
    static boolean pode(VisitaDoProcesso visita) {
        return visita != null && pode(visita.processo());
    }
}
