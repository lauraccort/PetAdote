package br.com.petadote.gui;

import br.com.petadote.model.Animal;
import br.com.petadote.model.Cao;
import br.com.petadote.model.Gato;
import br.com.petadote.model.OutraEspecie;
import java.time.LocalDate;
import java.time.Period;

/** Textos de animal usados em várias telas (espécie, raça, idade). */
final class TextoAnimal {

    static final String CAO = "Cão";
    static final String GATO = "Gato";
    static final String OUTRA = "Outra";

    private TextoAnimal() {
    }

    /** Cão, Gato ou Outra (para filtros). */
    static String grupoEspecie(Animal animal) {
        if (animal instanceof Cao) {
            return CAO;
        }
        return animal instanceof Gato ? GATO : OUTRA;
    }

    /** Cão, Gato ou, para "Outra", a espécie cadastrada (ex.: Coelho). */
    static String especie(Animal animal) {
        return animal instanceof OutraEspecie outra ? outra.getEspecie() : grupoEspecie(animal);
    }

    /** Raça de cão ou gato; null para outras espécies. */
    static String raca(Animal animal) {
        if (animal instanceof Cao cao) {
            return cao.getRaca();
        }
        return animal instanceof Gato gato ? gato.getRaca() : null;
    }

    /** "Cão · Labrador", ou só a espécie quando não há raça. */
    static String especieERaca(Animal animal) {
        String raca = raca(animal);
        return raca == null || raca.isBlank() ? especie(animal) : especie(animal) + " · " + raca;
    }

    /** "3 anos", "1 ano" ou "5 meses". */
    static String idade(LocalDate nascimento) {
        if (nascimento == null) {
            return "";
        }
        Period p = Period.between(nascimento, LocalDate.now());
        if (p.getYears() > 0) {
            return p.getYears() + (p.getYears() == 1 ? " ano" : " anos");
        }
        return p.getMonths() + (p.getMonths() == 1 ? " mês" : " meses");
    }
}
