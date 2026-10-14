package br.com.petadote.servico;

import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.Animal;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso; // enum usado nos relatórios
import br.com.petadote.model.Visita;
import br.com.petadote.repositorio.IRepositorio;
import java.time.LocalDate; // cálculo dos 15 dias
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // usado nos filtros

// Orquestra Animal + Adotante + Processo: é o service mais "rico"
public class ProcessoAdocaoServicoImpl implements IProcessoAdocaoServico {

    private final IRepositorio<ProcessoAdocao> processoRepositorio; // persiste processos
    private final IRepositorio<Animal> animalRepositorio;           // busca o animal
    private final IRepositorio<Adotante> adotanteRepositorio;       // busca o adotante

    // As três dependências são injetadas pelo construtor
    public ProcessoAdocaoServicoImpl(IRepositorio<ProcessoAdocao> processoRepositorio,
                                     IRepositorio<Animal> animalRepositorio,
                                     IRepositorio<Adotante> adotanteRepositorio) {
        this.processoRepositorio = processoRepositorio;
        this.animalRepositorio = animalRepositorio;
        this.adotanteRepositorio= adotanteRepositorio;
    }

    @Override
    public ProcessoAdocao iniciar(Long idAnimal, Long idAdotante) {
        // Busca o animal pelo id; erro se não existir
        Animal animal = animalRepositorio.buscarPorId(idAnimal)
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado."));
        // Busca o adotante pelo id; erro se não existir
        Adotante adotante = adotanteRepositorio.buscarPorId(idAdotante)
                .orElseThrow(() -> new IllegalArgumentException("Adotante não encontrado."));

        ProcessoAdocao processo = new ProcessoAdocao(animal, adotante); // monta o processo
        // REQ06+14+16: valida animal disponível e limite de 3 processos ativos do adotante
        if (!processo.abrirProcesso()) {
            throw new IllegalStateException("Não foi possível abrir: animal indisponível ou adotante no limite de 3 processos ativos.");
        }
        return processoRepositorio.salvar(processo); // persiste o processo aberto
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
        // Perfil Adotante: "listar meus processos" — filtra pelo id do adotante
        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getAdotante().getId().equals(idAdotante)) // só processos deste adotante
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessoAdocao> listarConcluidos() {
        // Relatório do Administrador: adoções concluídas
        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getStatus() == StatusProcesso.CONCLUIDO) // só finalizados
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessoAdocao> listarPendentesHa15Dias() {
        // Relatório do Administrador: processos "pendentes" (ainda EM_AVALIACAO)
        // abertos há 15 dias ou mais
        LocalDate limite = LocalDate.now().minusDays(15); // data de corte: 15 dias atrás
        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getStatus() == StatusProcesso.EM_AVALIACAO) // pendente = aguardando avaliação
                .filter(p -> !p.getDataAbertura().isAfter(limite)) // abertura no limite ou antes (= 15+ dias)
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
                .orElse(false); // processo inexistente = falha
    }

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
