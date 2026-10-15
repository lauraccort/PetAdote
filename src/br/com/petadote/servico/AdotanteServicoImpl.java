package br.com.petadote.servico;

import br.com.petadote.model.Adotante;
import br.com.petadote.repositorio.IRepositorio;
import java.util.List;
import java.util.Optional;

public class AdotanteServicoImpl implements IAdotanteServico {
    private final IRepositorio<Adotante> repositorio; 

    public AdotanteServicoImpl(IRepositorio<Adotante> repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Adotante cadastrar(Adotante adotante) {
        if (adotante.getNome() == null || adotante.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do adotante é obrigatório.");
        }
        String cpf = adotante.getCpf() == null ? "" : adotante.getCpf().replaceAll("\\D", "");
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
        return repositorio.salvar(adotante);
    }

    @Override
    public Optional<Adotante> buscarPorId(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Adotante> listarTodos() {
        return repositorio.listarTodos();
    }

    @Override
    public Adotante atualizar(Long id, String nome, String endereco) {
        Adotante adotante = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Adotante não encontrado."));
        adotante.setNome(nome);             
        adotante.setEndereco(endereco);     
        return repositorio.salvar(adotante); 
    }

    @Override
    public boolean excluir(Long id) {
        Optional<Adotante> adotante = repositorio.buscarPorId(id);
        // Regra: não exclui adotante que possui histórico de processos (REQ05)
        if (adotante.isEmpty() || !adotante.get().getHistoricoProcessos().isEmpty()) {
            return false;
        }
        return repositorio.excluirPorId(id);
    }
}
