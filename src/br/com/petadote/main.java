package br.com.petadote;

import br.com.petadote.controle.*;      // pacote renomeado (era: controller)
import br.com.petadote.model.Avaliacao; // parecer do responsável
import br.com.petadote.model.Parecer;   // enum POSITIVO/NEGATIVO
import br.com.petadote.model.Visita;    // visita pós-adoção
import br.com.petadote.repositorio.*;   // pacote renomeado (era: repository)
import br.com.petadote.servico.*;       // pacote renomeado (era: service)
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // 1. Cria os repositórios UMA vez (todos herdam o CRUD do RepositorioBase)
        AbrigoRepositorio abrigoRepo = new AbrigoRepositorio();
        AnimalRepositorio animalRepo = new AnimalRepositorio();
        AdotanteRepositorio adotanteRepo = new AdotanteRepositorio();
        ProcessoAdocaoRepositorio processoRepo = new ProcessoAdocaoRepositorio();

        // 2. Monta a cadeia de dependências: Controle -> Servico -> Repositorio
        AbrigoControle abrigoControle = new AbrigoControle(new AbrigoServicoImpl(abrigoRepo));
        AnimalControle animalControle = new AnimalControle(new AnimalServicoImpl(animalRepo));
        AdotanteControle adotanteControle = new AdotanteControle(new AdotanteServicoImpl(adotanteRepo));
        ProcessoAdocaoControle processoControle = new ProcessoAdocaoControle(
                new ProcessoAdocaoServicoImpl(processoRepo, animalRepo, adotanteRepo));

        // 3. Demonstração dos CRUDs
        abrigoControle.criar("Abrigo Central", "Rua das Flores, 123", 10);          // CREATE abrigo
        animalControle.criarCao("Rex", "10/05/2022", "Vira-lata");                  // CREATE animal
        adotanteControle.criar("Maria Silva", "12345678900", "Av. Brasil, 456", 0); // CREATE adotante

        abrigoControle.listar();   // READ abrigos
        animalControle.listar();   // READ animais
        adotanteControle.listar(); // READ adotantes

        // 4. Fluxo de adoção completo (REQ06 -> REQ08)
        processoControle.iniciar(1L, 1L); // abre processo EM_AVALIACAO
        processoControle.registrarAvaliacao(1L,
                new Avaliacao(LocalDate.now(), "João (responsável do abrigo)", Parecer.POSITIVO, "Casa adequada.")); // REQ07
        processoControle.agendarVisita(1L, new Visita(LocalDate.now().plusDays(30))); // REQ09 (mínimo 2)
        processoControle.agendarVisita(1L, new Visita(LocalDate.now().plusDays(60))); // REQ09
        processoControle.aprovar(1L);  // EM_AVALIACAO -> APROVADO
        processoControle.concluir(1L); // APROVADO -> CONCLUIDO, animal vira ADOTADO
        processoControle.listar();     // mostra o processo CONCLUIDO

        // 5. Consultas do modelo navegacional (perfil Adotante + relatórios do Admin)
        processoControle.listarPorAdotante(1L);      // "meus processos" do adotante 1
        processoControle.relatorioConcluidos();      // relatório: adoções concluídas
        processoControle.relatorioPendentes15Dias(); // relatório: pendentes há 15+ dias
    }
}
