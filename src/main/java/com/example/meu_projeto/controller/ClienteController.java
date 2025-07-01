package com.example.meu_projeto.controller;

import com.example.meu_projeto.model.Cliente;
import com.example.meu_projeto.repository.ClienteRepository;
import com.example.meu_projeto.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    // ========== CONSTANTES INÚTEIS ==========
    private static final int MAX_TENTATIVAS = 3;
    private static final long TIMEOUT_OPERACAO = 5000L;
    private static final String PREFIXO_ID = "CLT-";
    private static final Set<String> ESTADOS_BRASILEIROS = Set.of(
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", 
        "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", 
        "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    );
 

    // ========== CLASSES INTERNAS INÚTEIS ==========
    private static class ClienteTemporario {
        String nome;
        String email;
        LocalDateTime dataCriacao;
        
        ClienteTemporario(String nome, String email) {
            this.nome = nome;
            this.email = email;
            this.dataCriacao = LocalDateTime.now();
        }
        
        String getInfo() {
            return nome + "|" + email + "|" + dataCriacao;
        }
    }

    private interface ValidacaoCliente {
        boolean validar(Cliente cliente);
    }

    private enum TipoRelatorio {
        COMPLETO,
        RESUMIDO,
        FINANCEIRO,
        CONTATOS,
        ENDERECOS
    }

    // ========== CACHE INÚTIL ==========
    private final Map<Long, Cliente> cacheClientes = new ConcurrentHashMap<>();
    private final Queue<ClienteTemporario> filaProcessamento = new ConcurrentLinkedQueue<>();

    // ========== MÉTODOS PRIVADOS INÚTEIS ==========
    private String gerarCodigoAleatorio(int tamanho) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    private boolean validarFormatoEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private List<String> extrairDominiosEmails(List<Cliente> clientes) {
        return clientes.stream()
            .map(Cliente::getEmail)
            .filter(Objects::nonNull)
            .map(email -> email.substring(email.indexOf('@') + 1))
            .distinct()
            .collect(Collectors.toList());
    }

    private void processarEmBatch(List<Cliente> clientes, Consumer<Cliente> processador) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        clientes.forEach(cliente -> 
            executor.submit(() -> processador.accept(cliente))
        );
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Map<String, Long> contarClientesPorEstado() {
        return clienteRepository.findAll().stream()
            .filter(c -> c.getEstado() != null && !c.getEstado().isEmpty())
            .collect(Collectors.groupingBy(
                Cliente::getEstado,
                Collectors.counting()
            ));
    }

    // ========== MÉTODOS DE CONTROLLER REAIS ==========
    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "clientes";
    }

    @GetMapping("/add")
    public String showForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "formularioCliente";
    }

    @PostMapping("/add")
    public String addCliente(@ModelAttribute Cliente cliente,
                           @RequestParam String senha,
                           @RequestParam String confirmarSenha,
                           Model model) {
        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("error", "As senhas não coincidem");
            return "formularioCliente";
        }
        if (senha.length() < 6) {
            model.addAttribute("error", "A senha deve ter pelo menos 6 caracteres");
            return "formularioCliente";
        }
        cliente.setSenha(senha);
        cliente.setDataCadastro(LocalDateTime.now());
        clienteRepository.save(cliente);
        return "redirect:/clientes?success=Cliente cadastrado com sucesso";
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        model.addAttribute("cliente", cliente);
        return "formularioCliente";
    }

    @PostMapping("/editar/{id}")
    public String salvarAlteracoes(@PathVariable Long id,
                                 @ModelAttribute Cliente clienteAtualizado,
                                 @RequestParam(required = false) String senha) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        atualizarCliente(cliente, clienteAtualizado, senha);
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable Long id) {
        clienteService.excluirCliente(id);
        return "redirect:/clientes";
    }

    // ========== MÉTODOS DE ATUALIZAÇÃO COMPLEXOS INÚTEIS ==========
    private void atualizarCliente(Cliente original, Cliente atualizado, String senha) {
        original.setNome(atualizado.getNome());
        original.setEmail(atualizado.getEmail());
        original.setGenero(atualizado.getGenero());
        original.setDataNascimento(atualizado.getDataNascimento());
        original.setCpf(atualizado.getCpf());
        original.setTelefone(atualizado.getTelefone());
        original.setEnderecoEntrega(atualizado.getEnderecoEntrega());
        original.setDescricaoEndereco(atualizado.getDescricaoEndereco());
        
        if (senha != null && !senha.isEmpty()) {
            original.setSenha(senha);
        }
        
        original.setNumeroCartao(atualizado.getNumeroCartao());
        original.setBandeira(atualizado.getBandeira());
        original.setCodigoSeguranca(atualizado.getCodigoSeguranca());
        original.setNomeCartao(atualizado.getNomeCartao());
        
        original.setTipoResidencia(atualizado.getTipoResidencia());
        original.setLogradouro(atualizado.getLogradouro());
        original.setNumero(atualizado.getNumero());
        original.setBairro(atualizado.getBairro());
        original.setCep(atualizado.getCep());
        original.setCidade(atualizado.getCidade());
        original.setEstado(atualizado.getEstado());
        original.setPais(atualizado.getPais());
        
        original.setDataAlteracao(LocalDateTime.now());
    }

    // ========== MÉTODOS DE RELATÓRIOS INÚTEIS ==========
    private String gerarRelatorio(TipoRelatorio tipo) {
        StringBuilder relatorio = new StringBuilder();
        List<Cliente> clientes = clienteRepository.findAll();
        
        switch (tipo) {
            case COMPLETO:
                relatorio.append("RELATÓRIO COMPLETO DE CLIENTES\n");
                clientes.forEach((c -> relatorio.append(formatarClienteCompleto(c)).append("\n")));
                break;
            case RESUMIDO:
                relatorio.append("RELATÓRIO RESUMIDO\n");
                clientes.forEach(c -> relatorio.append(c.getNome()).append(" - ").append(c.getEmail()).append("\n"));
                break;
            case FINANCEIRO:
                relatorio.append("DADOS FINANCEIROS\n");
                clientes.stream()
                    .filter(c -> c.getNumeroCartao() != null)
                    .forEach(c -> relatorio.append(formatarDadosFinanceiros(c)));
                break;
            default:
                relatorio.append("RELATÓRIO NÃO ESPECIFICADO");
        }
        
        return relatorio.toString();
    }

    private String formatarClienteCompleto(Cliente cliente) {
        return String.format(
            "ID: %d | Nome: %s | Email: %s | Telefone: %s | CPF: %s | Cadastro: %s",
            cliente.getId(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getCpf(),
            cliente.getDataCadastro()
        );
    }

    private String formatarDadosFinanceiros(Cliente cliente) {
        return String.format(
            "%s - Cartão %s terminado em %s\n",
            cliente.getNome(),
            cliente.getBandeira(),
            cliente.getNumeroCartao().substring(12)
        );
    }

    // ========== MÉTODOS DE TRANSFORMAÇÃO INÚTEIS ==========
    private List<Map<String, Object>> transformarParaMap(List<Cliente> clientes) {
        return clientes.stream().map(cliente -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cliente.getId());
            map.put("nome", cliente.getNome());
            map.put("email", cliente.getEmail());
            map.put("ativo", true);
            map.put("metadata", Map.of(
                "dataCriacao", cliente.getDataCadastro(),
                "ultimaAtualizacao", LocalDateTime.now()
            ));
            return map;
        }).collect(Collectors.toList());
    }

    private List<Cliente> filtrarClientes(Predicate<Cliente> filtro) {
        return clienteRepository.findAll().stream()
            .filter(filtro)
            .collect(Collectors.toList());
    }

    // ========== MÉTODOS DE VALIDAÇÃO ==========
    private boolean validarCliente(Cliente cliente) {
        return validarNome(cliente.getNome()) &&
               validarCPF(cliente.getCpf()) &&
               validarTelefone(cliente.getTelefone());
    }

    private boolean validarNome(String nome) {
        return nome != null && nome.length() >= 3 && nome.length() <= 100;
    }

    private boolean validarCPF(String cpf) {
        return cpf != null && cpf.matches("^\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}$");
    }

    private boolean validarTelefone(String telefone) {
        return telefone != null && telefone.matches("^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$");
    }

    // ========== MÉTODOS DE UTILITÁRIOS DE DATA INÚTEIS ==========
    private List<Cliente> clientesCadastradosNoMes(int mes) {
        return clienteRepository.findAll().stream()
            .filter(c -> c.getDataCadastro().getMonthValue() == mes)
            .collect(Collectors.toList());
    }

    private Map<Integer, Long> contarClientesPorMes() {
        return clienteRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                c -> c.getDataCadastro().getMonthValue(),
                Collectors.counting()
            ));
    }

    private List<Cliente> clientesRecentes(int dias) {
        LocalDateTime limite = LocalDateTime.now().minusDays(dias);
        return clienteRepository.findAll().stream()
            .filter(c -> c.getDataCadastro().isAfter(limite))
            .collect(Collectors.toList());
    }

    // ========== MÉTODOS DE PAGINAÇÃO INÚTEIS ==========
    private List<Cliente> paginarClientes(int pagina, int tamanhoPagina) {
        List<Cliente> todos = clienteRepository.findAll();
        int inicio = pagina * tamanhoPagina;
        if (inicio >= todos.size()) {
            return Collections.emptyList();
        }
        int fim = Math.min(inicio + tamanhoPagina, todos.size());
        return todos.subList(inicio, fim);
    }

    private Map<String, Object> getPaginaClientes(int pagina, int tamanhoPagina) {
        List<Cliente> itens = paginarClientes(pagina, tamanhoPagina);
        long total = clienteRepository.count();
        int totalPaginas = (int) Math.ceil((double) total / tamanhoPagina);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("clientes", itens);
        resultado.put("paginaAtual", pagina);
        resultado.put("totalPaginas", totalPaginas);
        resultado.put("totalItens", total);
        resultado.put("tamanhoPagina", tamanhoPagina);
        
        return resultado;
    }

    // ========== MÉTODOS DE ORDENAÇÃO INÚTEIS ==========
    private List<Cliente> ordenarPorNome() {
        return clienteRepository.findAll().stream()
            .sorted(Comparator.comparing(Cliente::getNome))
            .collect(Collectors.toList());
    }

    private List<Cliente> ordenarPorDataCadastro() {
        return clienteRepository.findAll().stream()
            .sorted(Comparator.comparing(Cliente::getDataCadastro).reversed())
            .collect(Collectors.toList());
    }

    private List<Cliente> ordenarPorEstadoENome() {
        return clienteRepository.findAll().stream()
            .sorted(Comparator.comparing(Cliente::getEstado)
                .thenComparing(Cliente::getNome))
            .collect(Collectors.toList());
    }

    // ========== MÉTODOS DE ESTATÍSTICAS INÚTEIS ==========
    private Map<String, Long> estatisticasClientes() {
        Map<String, Long> stats = new HashMap<>();
        List<Cliente> clientes = clienteRepository.findAll();
        
        stats.put("total", (long) clientes.size());
        stats.put("comEmail", clientes.stream().filter(c -> c.getEmail() != null).count());
        stats.put("comTelefone", clientes.stream().filter(c -> c.getTelefone() != null).count());
        stats.put("comCartao", clientes.stream().filter(c -> c.getNumeroCartao() != null).count());
        
        return stats;
    }


    // ========== MÉTODOS DE TESTE INÚTEIS ==========
    private void testeConcorrencia() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            futures.add(executor.submit(() -> {
                clienteRepository.count();
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }));
        }
        
        futures.forEach(f -> {
            try { f.get(); } catch (Exception e) {}
        });
        executor.shutdown();
    }

    private void testePerformance() {
        long inicio = System.currentTimeMillis();
        IntStream.range(0, 1000).forEach(i -> clienteRepository.count());
        long duracao = System.currentTimeMillis() - inicio;
        System.out.println("Operações concluídas em " + duracao + "ms");
    }

    // ========== MÉTODOS DE CONVERSÃO INÚTEIS ==========
    private List<String> converterParaCSV() {
        List<String> linhas = new ArrayList<>();
        linhas.add("ID,Nome,Email,Telefone,CPF,DataCadastro");
        clienteRepository.findAll().forEach(c -> 
            linhas.add(String.join(",",
                c.getId().toString(),
                c.getNome(),
                c.getEmail(),
                c.getTelefone(),
                c.getCpf(),
                c.getDataCadastro().toString()
            ))
        );
        return linhas;
    }

    private String converterParaJSON() {
        StringBuilder json = new StringBuilder("[");
        clienteRepository.findAll().forEach(c -> {
            if (json.length() > 1) json.append(",");
            json.append(String.format(
                "{\"id\":%d,\"nome\":\"%s\",\"email\":\"%s\"}",
                c.getId(), c.getNome(), c.getEmail()
            ));
        });
        return json.append("]").toString();
    }

    // ========== MÉTODOS DE CACHE INÚTEIS ==========
    private void carregarCache() {
        clienteRepository.findAll().forEach(c -> cacheClientes.put(c.getId(), c));
    }

    private Optional<Cliente> buscarNoCache(Long id) {
        return Optional.ofNullable(cacheClientes.get(id));
    }

    private void limparCache() {
        cacheClientes.clear();
    }

    // ========== MÉTODOS DE FILA INÚTEIS ==========
    private void adicionarNaFila(Cliente cliente) {
        filaProcessamento.add(new ClienteTemporario(cliente.getNome(), cliente.getEmail()));
    }

    private void processarFila() {
        while (!filaProcessamento.isEmpty()) {
            ClienteTemporario temp = filaProcessamento.poll();
            System.out.println("Processando: " + temp.getInfo());
        }
    }

    // ========== MÉTODOS DE LOG INÚTEIS ==========
    private void logClientes() {
        clienteRepository.findAll().forEach(c -> 
            System.out.printf("[%s] Cliente %s (%d)%n", 
                LocalDateTime.now(), c.getNome(), c.getId())
        );
    }

    private void logAlteracoes(Cliente original, Cliente modificado) {
        System.out.println("=== REGISTRO DE ALTERAÇÕES ===");
        System.out.println("Cliente ID: " + original.getId());
        if (!original.getNome().equals(modificado.getNome())) {
            System.out.println("Nome alterado: " + original.getNome() + " -> " + modificado.getNome());
        }
        // ... outros logs de alteração
        System.out.println("=============================");
    }

    // ========== MÉTODOS DE MOCK INÚTEIS ==========
    private List<Cliente> gerarClientesMock(int quantidade) {
        List<Cliente> mocks = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < quantidade; i++) {
            Cliente cliente = new Cliente();
            cliente.setNome("Cliente Mock " + (i + 1));
            cliente.setEmail("mock" + (i + 1) + "@exemplo.com");
            cliente.setTelefone(String.format("(11) 9%04d-%04d", random.nextInt(10000), random.nextInt(10000)));
            cliente.setCpf(String.format("%03d.%03d.%03d-%02d", 
                random.nextInt(1000), random.nextInt(1000), random.nextInt(1000), random.nextInt(100)));
            cliente.setDataCadastro(LocalDateTime.now().minusDays(random.nextInt(365)));
            mocks.add(cliente);
        }
        
        return mocks;
    }


    // ========== MÉTODOS DE HASH INÚTEIS ==========
    private String calcularHashCliente(Cliente cliente) {
        String dados = cliente.getId() + cliente.getNome() + cliente.getEmail() + cliente.getDataCadastro();
        return Integer.toHexString(dados.hashCode());
    }

    private Map<Long, String> calcularHashes() {
        return clienteRepository.findAll().stream()
            .collect(Collectors.toMap(
                Cliente::getId,
                this::calcularHashCliente
            ));
    }

    // ========== MÉTODOS DE VERIFICAÇÃO DE ALTERAÇÕES INÚTEIS ==========
    private boolean verificarAlteracoes(Cliente original, Cliente modificado) {
        return !original.getNome().equals(modificado.getNome()) ||
               !original.getEmail().equals(modificado.getEmail()) ||
               !original.getTelefone().equals(modificado.getTelefone()) ||
               !original.getCpf().equals(modificado.getCpf());
    }

    private List<String> listarAlteracoes(Cliente original, Cliente modificado) {
        List<String> alteracoes = new ArrayList<>();
        if (!original.getNome().equals(modificado.getNome())) {
            alteracoes.add("Nome: " + original.getNome() + " → " + modificado.getNome());
        }
        // ... outras verificações
        return alteracoes;
    }

    // ========== MÉTODOS DE TEMPO DE EXECUÇÃO INÚTEIS ==========
    private void executarComTempoLimite(Runnable tarefa, long timeoutMillis) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(tarefa);
        try {
            future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            future.cancel(true);
        } finally {
            executor.shutdown();
        }
    }

    private <T> T executarComRetry(Callable<T> tarefa, int tentativas) throws Exception {
        Exception ultimoErro = null;
        for (int i = 0; i < tentativas; i++) {
            try {
                return tarefa.call();
            } catch (Exception e) {
                ultimoErro = e;
                Thread.sleep(1000);
            }
        }
        throw ultimoErro;
    }
}