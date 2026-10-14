package br.com.petadote.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessoAdocao extends Entidade {
    private static final int MIN_VISITAS_POS_ADOCAO = 2;
    private static final int PERIODO_VISITAS_DIAS = 90;

    private Animal animal;
    private Adotante adotante;
    private StatusProcesso status;
    private LocalDate dataAbertura;
    private LocalDate dataConclusao;
    private List<Visita> visitas;
    private List<Avaliacao> avaliacoes;

    public ProcessoAdocao() {
        this.visitas = new ArrayList<>();
        this.avaliacoes = new ArrayList<>();
    }

    public ProcessoAdocao(Animal animal, Adotante adotante) {
        this();
        this.animal = animal;
        this.adotante = adotante;
        this.status = StatusProcesso.EM_AVALIACAO;
        this.dataAbertura = LocalDate.now();
    }

    /**
     * REQ06 + REQ14 + REQ16: abre o processo com status
     * "Em Avaliação", desde que o animal esteja disponível
     * e o adotante não tenha excedido o limite de processos ativos.
     */
    public boolean abrirProcesso() {
        if (!animal.podeIniciarProcesso()) {
            return false;
        }
        if (!adotante.podeIniciarNovoProcesso()) {
            return false;
        }
        animal.setStatus(StatusAnimal.EM_AVALIACAO);
        adotante.adicionarProcesso(this);
        return true;
    }

    /** REQ07: registra avaliação prévia (parecer do responsável do abrigo). */
    public void registrarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
    }

    /** REQ15: existe ao menos uma avaliação com parecer favorável. */
    public boolean avaliacaoFavoravel() {
        return avaliacoes.stream().anyMatch(Avaliacao::isFavoravel);
    }

    /** REQ07: aprova o processo após avaliação favorável. */
    public boolean aprovarProcesso() {
        if (status != StatusProcesso.EM_AVALIACAO || !avaliacaoFavoravel()) {
            return false;
        }
        this.status = StatusProcesso.APROVADO;
        return true;
    }

    /** REQ09: agenda visita de acompanhamento pós-adoção. */
    public void agendarVisita(Visita visita) {
        visitas.add(visita);
    }

    /**
     * REQ08 + REQ15 + REQ17 + REQ18: conclui o processo,
     * alterando o status do animal para "Adotado".
     */
    public boolean concluirProcesso() {
        if (status != StatusProcesso.APROVADO) {
            return false;
        }
        if (!avaliacaoFavoravel()) {
            return false; // REQ15
        }
        if (!animal.podeConcluirAdocao()) {
            return false; // REQ17
        }
        if (!temVisitasSuficientes()) {
            return false; // REQ18
        }
        this.status = StatusProcesso.CONCLUIDO;
        this.dataConclusao = LocalDate.now();
        this.animal.setStatus(StatusAnimal.ADOTADO);
        return true;
    }

    /** REQ18: ao menos 2 visitas agendadas nos 90 dias seguintes. */
    private boolean temVisitasSuficientes() {
        LocalDate hoje = LocalDate.now();
        long agendadas = visitas.stream()
                .filter(v -> !v.getData().isBefore(hoje)
                        && v.getData().isBefore(hoje.plusDays(PERIODO_VISITAS_DIAS)))
                .count();
        return agendadas >= MIN_VISITAS_POS_ADOCAO;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Adotante getAdotante() {
        return adotante;
    }

    public void setAdotante(Adotante adotante) {
        this.adotante = adotante;
    }

    public StatusProcesso getStatus() {
        return status;
    }

    public void setStatus(StatusProcesso status) {
        this.status = status;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDate dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public List<Visita> getVisitas() {
        return Collections.unmodifiableList(visitas);
    }

    public List<Avaliacao> getAvaliacoes() {
        return Collections.unmodifiableList(avaliacoes);
    }
}
