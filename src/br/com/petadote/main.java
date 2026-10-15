package br.com.petadote;

import br.com.petadote.controle.*;
import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.Parecer;
import br.com.petadote.model.Visita;
import br.com.petadote.repositorio.*;
import br.com.petadote.servico.*;
import java.time.LocalDate;

public class main {
    public static void main(String[] args) {
        AbrigoRepositorio abrigoRepo = new AbrigoRepositorio();
        AnimalRepositorio animalRepo = new AnimalRepositorio();
        AdotanteRepositorio adotanteRepo = new AdotanteRepositorio();
        ProcessoAdocaoRepositorio processoRepo = new ProcessoAdocaoRepositorio();

        AbrigoControle abrigoControle = new AbrigoControle(new AbrigoServicoImpl(abrigoRepo));
        AnimalControle animalControle = new AnimalControle(new AnimalServicoImpl(animalRepo));
        AdotanteControle adotanteControle = new AdotanteControle(new AdotanteServicoImpl(adotanteRepo));
        ProcessoAdocaoControle processoControle = new ProcessoAdocaoControle(
                new ProcessoAdocaoServicoImpl(processoRepo, animalRepo, adotanteRepo));

        abrigoControle.criar("Abrigo Central", "Rua das Flores, 123", 10);
        animalControle.criarCao("Rex", "10/05/2022", "Vira-lata");
        adotanteControle.criar("Maria Silva", "12345678900", "Av. Brasil, 456", 0);

        abrigoControle.listar();
        animalControle.listar();
        adotanteControle.listar();

        processoControle.iniciar(1L, 1L);
        processoControle.registrarAvaliacao(1L,
                new Avaliacao(LocalDate.now(), "João (responsável do abrigo)", Parecer.POSITIVO, "Casa adequada."));
        processoControle.agendarVisita(1L, new Visita(LocalDate.now().plusDays(30)));
        processoControle.agendarVisita(1L, new Visita(LocalDate.now().plusDays(60)));
        processoControle.aprovar(1L);
        processoControle.concluir(1L);
        processoControle.listar();

        processoControle.listarPorAdotante(1L);
        processoControle.relatorioConcluidos();
        processoControle.relatorioPendentes15Dias();
    }
}
