package br.com.petadote.gui;

import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

/**
 * Uma visita junto com o processo a que pertence (a Visita não guarda o processo).
 * Usada nas tabelas de visitas e para levar a visita escolhida de uma tela para outra.
 */
public record VisitaDoProcesso(ProcessoAdocao processo, Visita visita) {

    /** Realizada, cancelada, atrasada (data passou sem resultado) ou a situação do agendamento. */
    public String status() {
        if (visita.isConcluida()) {
            return "Realizada";
        }
        if (visita.isCancelada()) {
            return "Cancelada";
        }
        if (visita.getData().isBefore(LocalDate.now())) {
            return "Atrasada";
        }
        return visita.getStatusAgendamento().getDescricao();
    }

    /** Todas as visitas dos processos informados. */
    static Stream<VisitaDoProcesso> de(List<ProcessoAdocao> processos) {
        return processos.stream().flatMap(p -> p.getVisitas().stream().map(v -> new VisitaDoProcesso(p, v)));
    }
}
