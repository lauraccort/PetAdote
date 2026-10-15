package br.com.petadote.controle;

import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;
import br.com.petadote.servico.IProcessoAdocaoServico;
import java.util.List;

public class ProcessoAdocaoControle {

    private final IProcessoAdocaoServico servico; // dependência da interface

    public ProcessoAdocaoControle(IProcessoAdocaoServico servico) {
        this.servico = servico;
    }

    public void iniciar(Long idAnimal, Long idAdotante) {
        try {
            ProcessoAdocao processo = servico.iniciar(idAnimal, idAdotante);
            System.out.println("Processo aberto (id " + processo.getId() + ") em avaliação.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage()); // dados inválidos ou regra bloqueada
        }
    }

    public void listar() {
        List<ProcessoAdocao> processos = servico.listarTodos();
        if (processos.isEmpty()) {
            System.out.println("Nenhum processo cadastrado.");
            return;
        }
        processos.forEach(p -> System.out.println(
                p.getId() + " - Animal id " + p.getAnimal().getId()
                        + " | Adotante: " + p.getAdotante().getNome()
                        + " | " + p.getStatus()));
    }


    public void listarPorAdotante(Long idAdotante) {
        List<ProcessoAdocao> processos = servico.listarPorAdotante(idAdotante);
        if (processos.isEmpty()) { // adotante sem processos? avisa e sai
            System.out.println("Este adotante não possui processos.");
            return;
        }
        processos.forEach(p -> System.out.println(
                p.getId() + " - Animal id " + p.getAnimal().getId() + " | " + p.getStatus()));
    }

    public void relatorioConcluidos() {
        System.out.println("--- Relatório: adoções concluídas ---");
        servico.listarConcluidos().forEach(p -> System.out.println(
                p.getId() + " - Animal id " + p.getAnimal().getId()
                        + " | Adotante: " + p.getAdotante().getNome()
                        + " | Concluído em: " + p.getDataConclusao()));
    }

    public void relatorioPendentes15Dias() {
        System.out.println("--- Relatório: processos pendentes há 15+ dias ---");
        service.listarPendentesHa15Dias().forEach(p -> System.out.println(
                p.getId() + " - Animal id " + p.getAnimal().getId()
                        + " | Aberto em: " + p.getDataAbertura() + " | " + p.getStatus()));
    }


    public void registrarAvaliacao(Long id, Avaliacao avaliacao) {
        try {
            servico.registrarAvaliacao(id, avaliacao);
            System.out.println("Avaliação registrada no processo " + id + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void agendarVisita(Long id, Visita visita) {
        try {
            servico.agendarVisita(id, visita);
            System.out.println("Visita agendada para o processo " + id + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void aprovar(Long id) {
        System.out.println(servico.aprovar(id)
                ? "Processo aprovado."
                : "Não foi possível aprovar: inexistente, sem parecer favorável ou status inválido.");
    }

    public void concluir(Long id) {
        System.out.println(service.concluir(id)
                ? "Adoção concluída! Animal agora está ADOTADO."
                : "Não foi possível concluir: verifique aprovação, parecer, saúde do animal e 2 visitas mínimas.");
    }
}
