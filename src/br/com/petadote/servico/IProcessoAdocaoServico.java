package br.com.petadote.servico;

import br.com.petadote.model.Avaliacao; // parecer do responsável do abrigo
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;    // visita de acompanhamento
import java.util.List;
import java.util.Optional;

// Contrato de negócio do fluxo de adoção (REQ06 a REQ18).
// Não há excluir: processo é registro histórico do fluxo.
public interface IProcessoAdocaoServico {

    ProcessoAdocao iniciar(Long idAnimal, Long idAdotante); // abre processo (REQ06/14/16)

    Optional<ProcessoAdocao> buscarPorId(Long id); // R

    List<ProcessoAdocao> listarTodos();            // R

    List<ProcessoAdocao> listarPorAdotante(Long idAdotante); // "meus processos" (perfil Adotante)

    List<ProcessoAdocao> listarConcluidos();       // relatório Admin — adoções concluídas

    List<ProcessoAdocao> listarPendentesHa15Dias(); // relatório Admin — pendentes há 15+ dias

    void registrarAvaliacao(Long idProcesso, Avaliacao avaliacao); // REQ07

    void agendarVisita(Long idProcesso, Visita visita);            // REQ09

    boolean aprovar(Long idProcesso);  // REQ07/15: aprova se houver parecer favorável

    boolean concluir(Long idProcesso); // REQ08/15/17/18: finaliza adoção
}
