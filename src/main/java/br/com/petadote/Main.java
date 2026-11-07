package br.com.petadote;

import br.com.petadote.model.Abrigo;
import br.com.petadote.model.Cao;
import java.time.LocalDate;

/** Ponto de entrada. Enquanto a interface JavaFX não existe, apenas exercita o Contexto. */
public class Main {
    public static void main(String[] args) {
        Contexto ctx = Contexto.get();

        Abrigo abrigo = ctx.abrigoServico().cadastrar(new Abrigo("Abrigo Central", "Rua das Flores, 123", 10));
        Cao rex = new Cao("Rex", LocalDate.of(2022, 5, 10), "Vira-lata");
        ctx.animalServico().cadastrar(rex);
        ctx.abrigoServico().adicionarAnimal(abrigo.getId(), rex);

        System.out.println("Abrigos: " + ctx.abrigoServico().listarTodos().size());
        System.out.println("Animais: " + ctx.animalServico().listarTodos().size());
    }
}