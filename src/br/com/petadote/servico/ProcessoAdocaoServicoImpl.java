package br.com.petadote.servico;

import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.Animal;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso; 
import br.com.petadote.model.Visita;
import br.com.petadote.repositorio.IRepositorio;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; 


public class ProcessoAdocaoServicoImpl implements IProcessoAdocaoServico {

    private final IRepositorio<ProcessoAdocao> processoRepositorio; 
    private final IRepositorio<Animal> animalRepositorio;           
    private final IRepositorio<Adotante> adotanteRepositorio;       


    public ProcessoAdocaoServicoImpl(IRepositorio<ProcessoAdocao> processoRepositorio,
                                     IRepositorio<Animal> animalRepositorio,
                                     IRepositorio<Adotante> adotanteRepositorio) {
        this.processoRepositorio = processoRepositorio;
        this.animalRepositorio = animalRepositorio;
        this.adotanteRepositorio= adotanteRepositorio;
    }

    @Override
    public ProcessoAdocao iniciar(Long idAnimal, Long idAdotante) {
        Animal animal = animalRepositorio.buscarPorId(idAnimal)
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado."));
        Adotante adotante = adotanteRepositorio.buscarPorId(idAdotante)
                .orElseThrow(() -> new IllegalArgumentException("Adotante não encontrado."));

        ProcessoAdocao processo = new ProcessoAdocao(animal, adotante);
        // REQ06+14+16: valida animal disponível e limite de 3 processos ativos do adotante
        if (!processo.abrirProcesso()) {
            throw new IllegalStateException("Não foi possível abrir: animal indisponível ou adotante no limite de 3 processos ativos.");
        }
        return processoRepositorio.salvar(processo);
    }

    @Override
    public Optional<ProcessoAdocao> buscarPorId(Long id) {
        return processoRepositorio.buscarPorId(id);
    }

    @Override
    public List<ProcessoAdocao> listarTodos() {
        return processoRepositorio.listarTodos();
    }

    @Override
    public List<ProcessoAdocao> listarPorAdotante(Long idAdotante) {
        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getAdotante().getId().equals(idAdotante)) 
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessoAdocao> listarConcluidos() {
        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getStatus() == StatusProcesso.CONCLUIDO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessoAdocao> listarPendentesHa15Dias() {
        LocalDate limite = LocalDate.now().minusDays(15);
        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getStatus() == StatusProcesso.EM_AVALIACAO) 
                .filter(p -> !p.getDataAbertura().isAfter(limite))
                .collect(Collectors.toList());
    }


    @Override
    public void registrarAvaliacao(Long idProcesso, Avaliacao avaliacao) {
        // Localiza o processo e registra o parecer (REQ07)
        processoRepositorio.buscarPorId(idProcesso)
                .orElseThrow(() -> new IllegalArgumentException("Processo não encontrado."))
                .registrarAvaliacao(avaliacao);
    }

    @Override
    public void agendarVisita(Long idProcesso, Visita visita) {
        // Localiza o processo e agenda a visita pós-adoção (REQ09)
        processoRepositorio.buscarPorId(idProcesso)
                .orElseThrow(() -> new IllegalArgumentException("Processo não encontrado."))
                .agendarVisita(visita);
    }

    @Override
    public boolean aprovar(Long idProcesso) {
        // REQ07+15: só aprova se estiver EM_AVALIACAO e com parecer favorável
        return processoRepositorio.buscarPorId(idProcesso)
                .map(ProcessoAdocao::aprovarProcesso)
                .orElse(false); 

    @Override
    public boolean concluir(Long idProcesso) {
        Optional<ProcessoAdocao> processo = processoRepositorio.buscarPorId(idProcesso);
        // REQ08+15+17+18: valida aprovação, parecer, saúde do animal e visitas mínimas
        if (processo.isEmpty() || !processo.get().concluirProcesso()) {
            return false;
        }
        processoRepositorio.salvar(processo.get()); // persiste o status atualizado
        return true;
    }
}
