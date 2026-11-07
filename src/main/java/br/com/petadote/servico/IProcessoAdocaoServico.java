package br.com.petadote.servico;

import br.com.petadote.model.Avaliacao; 
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.Visita;    
import java.util.List;
import java.util.Optional;

// Contrato de negócio do fluxo de adoção (REQ06 a REQ18).
public interface IProcessoAdocaoServico {

    ProcessoAdocao iniciar(Long idAnimal, Long idAdotante); // abre processo (REQ06/14/16)

    Optional<ProcessoAdocao> buscarPorId(Long id); 

    List<ProcessoAdocao> listarTodos();           

    List<ProcessoAdocao> listarPorAdotante(Long idAdotante); 

    List<ProcessoAdocao> listarConcluidos(); 

    List<ProcessoAdocao> listarPendentesHa15Dias(); 

    void registrarAvaliacao(Long idProcesso, Avaliacao avaliacao); // REQ07

    void agendarVisita(Long idProcesso, Visita visita);            // REQ09

    boolean aprovar(Long idProcesso);  // REQ07/15: aprova se houver parecer favorável

    boolean concluir(Long idProcesso); // REQ08/15/17/18: finaliza adoção
}
