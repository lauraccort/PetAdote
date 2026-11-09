
package br.com.petadote.servico;

import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Contrato dos serviços relacionados ao processo de adoção.
// Contempla os requisitos REQ05 a REQ18.
public interface IProcessoAdocaoServico {

    // REQ06, REQ14 e REQ16
    ProcessoAdocao iniciar(Long idAnimal, Long idAdotante);

    Optional<ProcessoAdocao> buscarPorId(Long id);

    List<ProcessoAdocao> listarTodos();

    // REQ05
    List<ProcessoAdocao> listarPorAdotante(Long idAdotante);

    // REQ12
    List<ProcessoAdocao> listarConcluidos();

    // REQ13
    List<ProcessoAdocao> listarPendentesHa15Dias();

    // REQ07
    void registrarAvaliacao(Long idProcesso, Avaliacao avaliacao);

    // REQ09 e REQ18
    void agendarVisita(Long idProcesso, Visita visita);

    // REQ07 e REQ15
    boolean aprovar(Long idProcesso);

    boolean negar(Long idProcesso);

    // REQ08, REQ15, REQ17 e REQ18
    boolean concluir(Long idProcesso);

    // Apoio ao REQ06/REQ07: justificativa do adotante, lida na avaliação prévia.
    void registrarJustificativa(Long idProcesso, String justificativa);

    // REQ09/REQ18: confirma uma visita agendada (passa a contar para o mínimo).
    boolean confirmarVisita(
            Long idProcesso,
            Long idVisita,
            String observacao
    );

    // REQ09: cancela uma visita; ela deixa de contar para o mínimo do REQ18.
    boolean cancelarVisita(
            Long idProcesso,
            Long idVisita,
            String observacao
    );

    // REQ09: reagenda uma visita; ela continua contando para o mínimo do REQ18.
    boolean reagendarVisita(
            Long idProcesso,
            Long idVisita,
            LocalDate novaData,
            String observacao
    );

    // REQ10: registra o resultado da visita de acompanhamento.
    void registrarResultadoVisita(
            Long idProcesso,
            Long idVisita,
            String situacao,
            String observacoes
    );
}

