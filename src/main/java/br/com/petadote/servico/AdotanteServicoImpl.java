package br.com.petadote.servico;

import br.com.petadote.model.Adotante;
import br.com.petadote.repositorio.IRepositorio;
import br.com.petadote.repositorio.AdotanteRepositorio;
import java.util.List;
import java.util.Optional;

public class AdotanteServicoImpl implements IAdotanteServico {
    private static final AdotanteServicoImpl INSTANCIA = new AdotanteServicoImpl();

    private final IRepositorio<Adotante> repositorio = AdotanteRepositorio.getInstance();

    private AdotanteServicoImpl() {
    }

    public static AdotanteServicoImpl getInstance() {
        return INSTANCIA;
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
        ValidadorAcesso.validar(adotante.getEmail(), adotante.getSenha(),
                repositorio.listarTodos().stream().map(Adotante::getEmail));
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

    @Override
    public Optional<Adotante> autenticar(String email, String senha) {
        return repositorio.listarTodos().stream()
                .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email))
                .filter(c -> c.getSenha() != null && c.getSenha().equals(senha))
                .findFirst();
    }
}
