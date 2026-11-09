
package br.com.petadote.servico;

import br.com.petadote.model.Avaliacao;
import br.com.petadote.model.Adotante;
import br.com.petadote.model.Animal;
import br.com.petadote.model.ProcessoAdocao;
import br.com.petadote.model.StatusProcesso;
import br.com.petadote.model.Visita;
import br.com.petadote.repositorio.IRepositorio;
import br.com.petadote.repositorio.ProcessoAdocaoRepositorio;
import br.com.petadote.repositorio.AnimalRepositorio;
import br.com.petadote.repositorio.AdotanteRepositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ProcessoAdocaoServicoImpl implements IProcessoAdocaoServico {

    private static final ProcessoAdocaoServicoImpl INSTANCIA =
            new ProcessoAdocaoServicoImpl();

    private final IRepositorio<ProcessoAdocao> processoRepositorio =
            ProcessoAdocaoRepositorio.getInstance();

    private final IRepositorio<Animal> animalRepositorio =
            AnimalRepositorio.getInstance();

    private final IRepositorio<Adotante> adotanteRepositorio =
            AdotanteRepositorio.getInstance();

    private final AtomicLong proximoIdVisita = new AtomicLong(1);

    // ERRO 6:
    // Recupera o maior ID de visita já salvo para evitar
    // a repetição de identificadores após reiniciar o sistema.
    private ProcessoAdocaoServicoImpl() {

        long maiorId = 0;

        for (ProcessoAdocao processo : processoRepositorio.listarTodos()) {

            if (processo != null && processo.getVisitas() != null) {

                for (Visita visita : processo.getVisitas()) {

                    if (visita != null && visita.getId() != null) {

                        if (visita.getId() > maiorId) {
                            maiorId = visita.getId();
                        }
                    }
                }
            }
        }

        proximoIdVisita.set(maiorId + 1);
    }

    public static ProcessoAdocaoServicoImpl getInstance() {
        return INSTANCIA;
    }

    // REQ06, REQ14 e REQ16:
    // Abre um processo, verificando a disponibilidade do animal
    // e o limite de processos ativos do adotante.
    @Override
    public ProcessoAdocao iniciar(Long idAnimal, Long idAdotante) {

        Animal animal = animalRepositorio.buscarPorId(idAnimal)
                .orElseThrow(() ->
                        new IllegalArgumentException("Animal não encontrado."));

        Adotante adotante = adotanteRepositorio.buscarPorId(idAdotante)
                .orElseThrow(() ->
                        new IllegalArgumentException("Adotante não encontrado."));

        ProcessoAdocao processo = new ProcessoAdocao(animal, adotante);

        if (!processo.abrirProcesso()) {
            throw new IllegalStateException(
                    "Não foi possível abrir: animal indisponível ou adotante no limite de 3 processos ativos."
            );
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

    // REQ05: consulta o histórico de processos de um adotante.
    @Override
    public List<ProcessoAdocao> listarPorAdotante(Long idAdotante) {

        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getAdotante() != null
                        && p.getAdotante().getId().equals(idAdotante))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessoAdocao> listarConcluidos() {

        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getStatus() == StatusProcesso.CONCLUIDO)
                .collect(Collectors.toList());
    }

    // REQ13: lista processos pendentes de avaliação há pelo menos 15 dias.
    @Override
    public List<ProcessoAdocao> listarPendentesHa15Dias() {

        LocalDate limite = LocalDate.now().minusDays(15);

        return processoRepositorio.listarTodos().stream()
                .filter(p -> p.getStatus() == StatusProcesso.EM_AVALIACAO)
                .filter(p -> p.getDataAbertura() != null
                        && !p.getDataAbertura().isAfter(limite))
                .collect(Collectors.toList());
    }

    // REQ07: registra a avaliação prévia do processo.
    @Override
    public void registrarAvaliacao(Long idProcesso, Avaliacao avaliacao) {

        ProcessoAdocao processo = processoRepositorio.buscarPorId(idProcesso)
                .orElseThrow(() ->
                        new IllegalArgumentException("Processo não encontrado."));

        processo.registrarAvaliacao(avaliacao);

        processoRepositorio.salvar(processo);
    }

    // REQ09: agenda uma visita vinculada ao processo.
    @Override
    public void agendarVisita(Long idProcesso, Visita visita) {

        ProcessoAdocao processo = processoRepositorio.buscarPorId(idProcesso)
                .orElseThrow(() ->
                        new IllegalArgumentException("Processo não encontrado."));

        if (processo.getStatus() != StatusProcesso.APROVADO
                && processo.getStatus() != StatusProcesso.CONCLUIDO) {

            throw new IllegalStateException(
                    "Só é possível agendar visitas para processos aprovados ou concluídos."
            );
        }

        if (visita == null || visita.getData() == null) {
            throw new IllegalArgumentException("Informe a data da visita.");
        }

        if (visita.getData().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "A data da visita não pode estar no passado."
            );
        }

        // ERRO 6: gera um ID único, continuando a sequência
        // recuperada das visitas já existentes.
        visita.setId(proximoIdVisita.getAndIncrement());

        processo.agendarVisita(visita);

        processoRepositorio.salvar(processo);
    }

    // REQ07 e REQ15: aprova somente processos com avaliação favorável.
    @Override
    public boolean aprovar(Long idProcesso) {

        Optional<ProcessoAdocao> resultado =
                processoRepositorio.buscarPorId(idProcesso);

        if (resultado.isEmpty()) {
            return false;
        }

        ProcessoAdocao processo = resultado.get();

        boolean aprovado = processo.aprovarProcesso();

        if (aprovado) {
            processoRepositorio.salvar(processo);
        }

        return aprovado;
    }

    // REQ07: nega o processo quando não deve prosseguir.
    @Override
    public boolean negar(Long idProcesso) {

        Optional<ProcessoAdocao> resultado =
                processoRepositorio.buscarPorId(idProcesso);

        if (resultado.isEmpty()) {
            return false;
        }

        ProcessoAdocao processo = resultado.get();

        boolean negado = processo.negarProcesso();

        if (negado) {
            processoRepositorio.salvar(processo);
        }

        return negado;
    }

    // REQ08, REQ15, REQ17 e REQ18:
    // Conclui a adoção verificando aprovação, avaliação,
    // condição do animal e quantidade mínima de visitas.
    @Override
    public boolean concluir(Long idProcesso) {

        Optional<ProcessoAdocao> resultado =
                processoRepositorio.buscarPorId(idProcesso);

        if (resultado.isEmpty()) {
            return false;
        }

        ProcessoAdocao processo = resultado.get();

        boolean concluido = processo.concluirProcesso();

        if (concluido) {
            processoRepositorio.salvar(processo);
        }

        return concluido;
    }

    // REQ12: registra a justificativa apresentada pelo adotante.
    @Override
    public void registrarJustificativa(Long idProcesso, String justificativa) {

        ProcessoAdocao processo = processoRepositorio.buscarPorId(idProcesso)
                .orElseThrow(() ->
                        new IllegalArgumentException("Processo não encontrado."));

        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "A justificativa não pode estar vazia."
            );
        }

        processo.setJustificativaAdotante(justificativa);

        processoRepositorio.salvar(processo);
    }

    // Localiza uma visita pelo identificador dentro do processo.
    @Override
    public boolean confirmarVisita(
            Long idProcesso, Long idVisita, String observacao) {

        Optional<ProcessoAdocao> resultado =
                processoRepositorio.buscarPorId(idProcesso);

        if (resultado.isEmpty()) {
            return false;
        }

        ProcessoAdocao processo = resultado.get();
        Visita visita = localizarVisita(processo, idVisita);

        if (visita == null) {
            return false;
        }

        boolean confirmou = visita.confirmar(observacao);

        if (confirmou) {
            processoRepositorio.salvar(processo);
        }

        return confirmou;
    }

    // Localiza uma visita pelo identificador dentro do processo.
    private Visita localizarVisita(ProcessoAdocao processo, Long idVisita) {

        if (idVisita == null) {
            return null;
        }

        for (Visita visita : processo.getVisitas()) {
            if (idVisita.equals(visita.getId())) {
                return visita;
            }
        }

        return null;
    }

    // REQ11: cancela uma visita agendada.
    @Override
    public boolean cancelarVisita(
            Long idProcesso, Long idVisita, String observacao) {

        Optional<ProcessoAdocao> resultado =
                processoRepositorio.buscarPorId(idProcesso);

        if (resultado.isEmpty()) {
            return false;
        }

        ProcessoAdocao processo = resultado.get();
        Visita visita = localizarVisita(processo, idVisita);

        if (visita == null) {
            return false;
        }

        boolean cancelou = visita.cancelar(observacao);

        if (cancelou) {
            processoRepositorio.salvar(processo);
        }

        return cancelou;
    }

    // REQ11: altera a data de uma visita agendada.
    @Override
    public boolean reagendarVisita(
            Long idProcesso,
            Long idVisita,
            LocalDate novaData,
            String observacao) {

        Optional<ProcessoAdocao> resultado =
                processoRepositorio.buscarPorId(idProcesso);

        if (resultado.isEmpty()) {
            return false;
        }

        ProcessoAdocao processo = resultado.get();
        Visita visita = localizarVisita(processo, idVisita);

        if (visita == null) {
            return false;
        }

        boolean reagendou = visita.reagendar(novaData, observacao);

        if (reagendou) {
            processoRepositorio.salvar(processo);
        }

        return reagendou;
    }

    // REQ10: registra o resultado e as observações da visita.
    @Override
    public void registrarResultadoVisita(
            Long idProcesso,
            Long idVisita,
            String situacao,
            String observacoes) {

        ProcessoAdocao processo = processoRepositorio.buscarPorId(idProcesso)
                .orElseThrow(() ->
                        new IllegalArgumentException("Processo não encontrado."));

        Visita visita = localizarVisita(processo, idVisita);

        if (visita == null) {
            throw new IllegalArgumentException("Visita não encontrada.");
        }

        if (visita.isCancelada() || visita.isConcluida()) {
            throw new IllegalStateException(
                    "Não é possível registrar o resultado desta visita."
            );
        }

        visita.registrarResultado(situacao, observacoes);

        processoRepositorio.salvar(processo);
    }
}

