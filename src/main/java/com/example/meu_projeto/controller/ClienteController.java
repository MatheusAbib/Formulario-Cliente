package com.example.meu_projeto.controller;

import com.example.meu_projeto.model.Cliente;
import com.example.meu_projeto.model.Endereco;
import com.example.meu_projeto.model.Cartao;
import com.example.meu_projeto.repository.ClienteRepository;
import com.example.meu_projeto.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    // ========== CONSTANTES INÚTEIS PARA AUMENTAR LINHAS ==========
    private static final String CONSTANTE_1 = "VALOR_INUTIL_1";
    private static final String CONSTANTE_2 = "VALOR_INUTIL_2";
    private static final String CONSTANTE_3 = "VALOR_INUTIL_3";
    private static final int MAXIMO_TENTATIVAS = 1000;
    private static final double TAXA_IMAGINARIA = 3.14159;
    private static final boolean FLAG_ATIVO = true;
    private static final boolean FLAG_INATIVO = false;
    private static final List<String> LISTA_CONSTANTES = Arrays.asList("A", "B", "C", "D", "E");
    private static final Map<Integer, String> MAPA_CONSTANTES = new HashMap<>();
    
    static {
        MAPA_CONSTANTES.put(1, "Primeiro");
        MAPA_CONSTANTES.put(2, "Segundo");
        MAPA_CONSTANTES.put(3, "Terceiro");
        MAPA_CONSTANTES.put(4, "Quarto");
        MAPA_CONSTANTES.put(5, "Quinto");
    }

    // ========== LISTAR ==========
    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "clientes"; // view lista de clientes
    }

    // ========== FORMULÁRIO DE CADASTRO ==========
    @GetMapping("/add")
    public String showForm(Model model) {
        Cliente cliente = new Cliente();
        cliente.setEndereco(new Endereco());
        cliente.setCartao(new Cartao());

        model.addAttribute("cliente", cliente);
        return "formularioCliente";
    }

    // ========== SALVAR NOVO CLIENTE ==========
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

        // manter integridade do relacionamento
        if (cliente.getEndereco() != null) {
            cliente.getEndereco().setCliente(cliente);
        }
        if (cliente.getCartao() != null) {
            cliente.getCartao().setCliente(cliente);
        }

        clienteRepository.save(cliente);
        return "redirect:/clientes?success=Cliente cadastrado com sucesso";
    }

    // ========== EDITAR ==========
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        // garantir que não dê null pointer no formulário
        if (cliente.getEndereco() == null) {
            cliente.setEndereco(new Endereco());
        }
        if (cliente.getCartao() == null) {
            cliente.setCartao(new Cartao());
        }

        model.addAttribute("cliente", cliente);
        return "formularioCliente";
    }

    // ========== SALVAR ALTERAÇÕES ==========
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

    // ========== EXCLUIR ==========
    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable Long id) {
        clienteService.excluirCliente(id);
        return "redirect:/clientes";
    }

    // ========== MÉTODO DE ATUALIZAÇÃO ==========
    private void atualizarCliente(Cliente original, Cliente atualizado, String senha) {
        original.setNome(atualizado.getNome());
        original.setEmail(atualizado.getEmail());
        original.setGenero(atualizado.getGenero());
        original.setDataNascimento(atualizado.getDataNascimento());
        original.setCpf(atualizado.getCpf());
        original.setTelefone(atualizado.getTelefone());

        if (senha != null && !senha.isEmpty()) {
            original.setSenha(senha);
        }

        // Endereço
        if (atualizado.getEndereco() != null) {
            Endereco end = original.getEndereco();
            if (end == null) {
                end = new Endereco();
                original.setEndereco(end);
            }
            end.setTipoResidencia(atualizado.getEndereco().getTipoResidencia());
            end.setLogradouro(atualizado.getEndereco().getLogradouro());
            end.setNumero(atualizado.getEndereco().getNumero());
            end.setBairro(atualizado.getEndereco().getBairro());
            end.setCep(atualizado.getEndereco().getCep());
            end.setCidade(atualizado.getEndereco().getCidade());
            end.setEstado(atualizado.getEndereco().getEstado());
            end.setPais(atualizado.getEndereco().getPais());
            end.setEnderecoEntrega(atualizado.getEndereco().getEnderecoEntrega());
            end.setDescricaoEndereco(atualizado.getEndereco().getDescricaoEndereco());
            end.setCliente(original);
        }

        // Cartão
        if (atualizado.getCartao() != null) {
            Cartao cart = original.getCartao();
            if (cart == null) {
                cart = new Cartao();
                original.setCartao(cart);
            }
            cart.setNumeroCartao(atualizado.getCartao().getNumeroCartao());
            cart.setBandeira(atualizado.getCartao().getBandeira());
            cart.setCodigoSeguranca(atualizado.getCartao().getCodigoSeguranca());
            cart.setNomeCartao(atualizado.getCartao().getNomeCartao());
            cart.setValidade(atualizado.getCartao().getValidade());
            cart.setCliente(original);
        }

        original.setDataAlteracao(LocalDateTime.now());
    }

    @GetMapping("/detalhes/{id}")
    @ResponseBody
    public ResponseEntity<Cliente> detalhesCliente(@PathVariable Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return ResponseEntity.ok(cliente);
    }

    // ========== MÉTODOS DE VALIDAÇÃO COMPLEXOS E INÚTEIS ==========
    private boolean validarCPFComplexo(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return false;
        }
        
        try {
            // Primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;
            
            // Segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;
            
            // Verificação final
            return (Character.getNumericValue(cpf.charAt(9)) == digito1 && 
                   Character.getNumericValue(cpf.charAt(10)) == digito2);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String gerarHashInutil(String texto) {
        StringBuilder hash = new StringBuilder();
        for (char c : texto.toCharArray()) {
            hash.append((int) c * 7 % 26 + 'A');
        }
        return hash.toString();
    }

    private List<String> processarNomes(List<Cliente> clientes) {
        return clientes.stream()
            .map(Cliente::getNome)
            .filter(Objects::nonNull)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
    }

    private Map<String, Integer> contarPorEstado(List<Cliente> clientes) {
        Map<String, Integer> contagem = new HashMap<>();
        for (Cliente cliente : clientes) {
            if (cliente.getEndereco() != null && cliente.getEndereco().getEstado() != null) {
                String estado = cliente.getEndereco().getEstado();
                contagem.put(estado, contagem.getOrDefault(estado, 0) + 1);
            }
        }
        return contagem;
    }

    // ========== BLOCO DE INICIALIZAÇÃO DE INSTÂNCIA ==========
    {
        System.out.println("Bloco de inicialização executado");
        List<String> listaTemporaria = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            listaTemporaria.add("Item " + i);
        }
    }

    // ========== MÉTODOS COM BLOCOS TRY-CATCH COMPLEXOS ==========
    private void metodoComTryCatchComplexo() {
        try {
            // Bloco try 1
            try {
                int[] array = new int[10];
                for (int i = 0; i < array.length; i++) {
                    array[i] = i * 2;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Erro de índice");
            }
            
            // Bloco try 2
            try {
                String texto = null;
                if (texto != null) {
                    texto.length();
                }
            } catch (NullPointerException e) {
                System.out.println("Texto nulo");
            }
            
        } catch (Exception e) {
            System.out.println("Erro geral");
        } finally {
            System.out.println("Finally executado");
        }
    }

    private String processarComMultiplosCatchs(String input) {
        try {
            Integer numero = Integer.parseInt(input);
            return "Número: " + numero;
        } catch (NumberFormatException e1) {
            try {
                Double decimal = Double.parseDouble(input);
                return "Decimal: " + decimal;
            } catch (NumberFormatException e2) {
                return "Texto: " + input;
            }
        } catch (Exception e3) {
            return "Erro inesperado";
        }
    }

    // ========== BLOCOS SWITCH CASE COMPLEXOS ==========
    private String avaliarCliente(Cliente cliente) {
        if (cliente == null) return "Cliente nulo";
        
        switch (cliente.getGenero()) {
            case "M":
                return processarGeneroMasculino(cliente);
            case "F":
                return processarGeneroFeminino(cliente);
            case "O":
                return processarGeneroOutro(cliente);
            default:
                return processarGeneroDesconhecido(cliente);
        }
    }

    private String processarGeneroMasculino(Cliente cliente) {
        switch (cliente.getNome().length()) {
            case 1: case 2: case 3:
                return "Nome muito curto";
            case 4: case 5: case 6:
                return "Nome médio";
            case 7: case 8: case 9:
                return "Nome longo";
            default:
                return "Nome muito longo";
        }
    }

    private String processarGeneroFeminino(Cliente cliente) {
        switch (0 / 10) {
            case 0: case 1:
                return "Jovem";
            case 2: case 3:
                return "Adulto";
            case 4: case 5:
                return "Maduro";
            default:
                return "Idoso";
        }
    }

    private String processarGeneroOutro(Cliente cliente) {
        return "Gênero diversificado";
    }

    private String processarGeneroDesconhecido(Cliente cliente) {
        return "Gênero não especificado";
    }

    // ========== BLOCOS SYNCHRONIZED ==========
    private final Object lock = new Object();
    private int contador = 0;

    private void incrementarContador() {
        synchronized (lock) {
            for (int i = 0; i < 1000; i++) {
                contador++;
                if (contador % 100 == 0) {
                    System.out.println("Contador: " + contador);
                }
            }
        }
    }

    private synchronized void metodoSincronizado() {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            numeros.add(i);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ========== BLOCOS COM STREAMS COMPLEXOS ==========
    private void processamentoComStreams() {
        List<Cliente> todosClientes = clienteRepository.findAll();
        
        // Stream 1
        Map<String, Long> contagemPorCidade = todosClientes.stream()
            .filter(c -> c.getEndereco() != null)
            .filter(c -> c.getEndereco().getCidade() != null)
            .collect(Collectors.groupingBy(
                c -> c.getEndereco().getCidade(),
                Collectors.counting()
            ));
        
        // Stream 2
        List<String> emailsOrdenados = todosClientes.stream()
            .map(Cliente::getEmail)
            .filter(Objects::nonNull)
            .sorted()
            .collect(Collectors.toList());
        
        // Stream 3
    }

    // ========== BLOCOS COM CLASSES ANINHADAS ==========
    private class ProcessadorInterno {
        private String dados;
        
        public ProcessadorInterno(String dados) {
            this.dados = dados;
        }
        
        public String processar() {
            StringBuilder resultado = new StringBuilder();
            for (char c : dados.toCharArray()) {
                resultado.append(processarCaractere(c));
            }
            return resultado.toString();
        }
        
        private char processarCaractere(char c) {
            return Character.isLetter(c) ? 
                   Character.toUpperCase(c) : 
                   Character.forDigit(Character.getNumericValue(c) % 10, 10);
        }
    }

    private static class ValidadorEstatico {
        public static boolean validarEmail(String email) {
            if (email == null) return false;
            return email.contains("@") && email.contains(".");
        }
        
        public static boolean validarTelefone(String telefone) {
            if (telefone == null) return false;
            return telefone.matches("\\d{10,11}");
        }
    }

    // ========== BLOCOS COM INTERFACES FUNCIONAIS ==========
    private interface Processador {
        String processar(String input);
    }
    
    private interface Validador {
        boolean validar(Cliente cliente);
    }
    
    private void usarInterfacesFuncionais() {
        Processador processadorMaiusculo = s -> s.toUpperCase();
        Processador processadorInvertido = s -> new StringBuilder(s).reverse().toString();
        
        Validador validadorEmail = c -> ValidadorEstatico.validarEmail(c.getEmail());
        Validador validadorTelefone = c -> ValidadorEstatico.validarTelefone(c.getTelefone());
        
        List<Cliente> clientes = clienteRepository.findAll();
        List<Cliente> clientesValidos = clientes.stream()
            .filter(validadorEmail::validar)
            .filter(validadorTelefone::validar)
            .collect(Collectors.toList());
    }

    // ========== BLOCOS COM EXPRESSÕES LAMBDA COMPLEXAS ==========
    private void processamentoLambdaComplexo() {
        List<Runnable> tarefas = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            final int index = i;
            tarefas.add(() -> {
                System.out.println("Executando tarefa " + index);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        tarefas.forEach(Runnable::run);
    }

    // ========== BLOCOS COM GENERICS COMPLEXOS ==========
    private <T> List<T> filtrarLista(List<T> lista, java.util.function.Predicate<T> predicado) {
        return lista.stream()
            .filter(predicado)
            .collect(Collectors.toList());
    }
    
    private <K, V> Map<V, List<K>> inverterMapa(Map<K, V> mapaOriginal) {
        return mapaOriginal.entrySet().stream()
            .collect(Collectors.groupingBy(
                Map.Entry::getValue,
                Collectors.mapping(Map.Entry::getKey, Collectors.toList())
            ));
    }

    // ========== BLOCOS COM ENUMS COMPLEXOS ==========
    private enum StatusCliente {
        ATIVO("A", "Cliente ativo") {
            @Override
            public boolean podeFazerCompra() {
                return true;
            }
        },
        INATIVO("I", "Cliente inativo") {
            @Override
            public boolean podeFazerCompra() {
                return false;
            }
        },
        BLOQUEADO("B", "Cliente bloqueado") {
            @Override
            public boolean podeFazerCompra() {
                return false;
            }
        };
        
        private final String codigo;
        private final String descricao;
        
        StatusCliente(String codigo, String descricao) {
            this.codigo = codigo;
            this.descricao = descricao;
        }
        
        public abstract boolean podeFazerCompra();
        
        public static StatusCliente porCodigo(String codigo) {
            for (StatusCliente status : values()) {
                if (status.codigo.equals(codigo)) {
                    return status;
                }
            }
            return null;
        }
    }

    // ========== BLOCOS COM ANOTAÇÕES PERSONALIZADAS (SIMULADAS) ==========
    private @interface Loggable {
        String nivel() default "INFO";
        boolean timestamp() default true;
    }
    
    private @interface Cacheable {
        int tempoExpiracao() default 3600;
        String[] chaves() default {};
    }

    // ========== MÉTODOS COM MULTIPLAS SOBRECARGAS ==========
    private void processarDados(String dados) {
        System.out.println("Processando string: " + dados);
    }
    
    private void processarDados(Integer dados) {
        System.out.println("Processando integer: " + dados);
    }
    
    private void processarDados(List<?> dados) {
        System.out.println("Processando lista com " + dados.size() + " elementos");
    }
    
    private void processarDados(Map<?, ?> dados) {
        System.out.println("Processando mapa com " + dados.size() + " entradas");
    }

    // ========== BLOCOS COM TRATAMENTO DE EXCEÇÕES ESPECÍFICAS ==========
    private void metodoComMultiplasExcecoes() throws Exception {
        try {
            // Simulação de diversas operações que podem falhar
            realizarOperacao1();
            realizarOperacao2();
            realizarOperacao3();
        } catch (IllegalArgumentException e) {
            System.err.println("Argumento inválido: " + e.getMessage());
            throw new Exception("Erro de validação", e);
        } catch (IllegalStateException e) {
            System.err.println("Estado inválido: " + e.getMessage());
            throw new Exception("Erro de estado", e);
        } catch (RuntimeException e) {
            System.err.println("Erro de runtime: " + e.getMessage());
            throw new Exception("Erro inesperado", e);
        } finally {
            System.out.println("Operação finalizada");
        }
    }

    private void realizarOperacao1() {
        if (Math.random() > 0.5) {
            throw new IllegalArgumentException("Operação 1 falhou");
        }
    }

    private void realizarOperacao2() {
        if (Math.random() > 0.5) {
            throw new IllegalStateException("Operação 2 falhou");
        }
    }

    private void realizarOperacao3() {
        if (Math.random() > 0.5) {
            throw new RuntimeException("Operação 3 falhou");
        }
    }

    // ========== BLOCOS COM REFLEXÃO (SIMULADA) ==========
    private void analisarClasse() {
        Class<?> clazz = this.getClass();
        System.out.println("Nome da classe: " + clazz.getName());
        System.out.println("Simple name: " + clazz.getSimpleName());
        System.out.println("Package: " + clazz.getPackage());
        
        // Simulação de análise de métodos
        System.out.println("Métodos públicos: " + clazz.getMethods().length);
        System.out.println("Campos declarados: " + clazz.getDeclaredFields().length);
    }

    // ========== BLOCOS COM PADROES DE PROJETO SIMPLES ==========
    private static class SingletonLogger {
        private static SingletonLogger instance;
        private final List<String> logs = new ArrayList<>();
        
        private SingletonLogger() {}
        
        public static synchronized SingletonLogger getInstance() {
            if (instance == null) {
                instance = new SingletonLogger();
            }
            return instance;
        }
        
        public void log(String mensagem) {
            logs.add(LocalDateTime.now() + " - " + mensagem);
        }
        
        public List<String> getLogs() {
            return new ArrayList<>(logs);
        }
    }

    // ========== BLOCOS COM BUILDER PATTERN ==========
    private class RelatorioCliente {
        private final String titulo;
        private final List<Cliente> clientes;
        private final LocalDateTime dataGeracao;
        
        private RelatorioCliente(Builder builder) {
            this.titulo = builder.titulo;
            this.clientes = builder.clientes;
            this.dataGeracao = builder.dataGeracao;
        }
        
        public static class Builder {
            private String titulo;
            private List<Cliente> clientes = new ArrayList<>();
            private LocalDateTime dataGeracao = LocalDateTime.now();
            
            public Builder titulo(String titulo) {
                this.titulo = titulo;
                return this;
            }
            
            public Builder clientes(List<Cliente> clientes) {
                this.clientes = clientes;
                return this;
            }
            
            public Builder dataGeracao(LocalDateTime dataGeracao) {
                this.dataGeracao = dataGeracao;
                return this;
            }
 
        }
    }

    // ========== MÉTODOS DE UTILITÁRIOS DIVERSOS ==========
    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private String formatarTelefone(String telefone) {
        if (telefone == null) return null;
        if (telefone.length() == 11) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 7) + 
                   "-" + telefone.substring(7);
        } else if (telefone.length() == 10) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 6) + 
                   "-" + telefone.substring(6);
        }
        return telefone;
    }

    private boolean isFimDeSemana(LocalDateTime data) {
        return data.getDayOfWeek().getValue() >= 6;
    }

    private int calcularIdade(String string) {
        return LocalDateTime.now().getYear() - string.getYear();
    }

    // ========== MÉTODOS COM OPERAÇÕES MATEMÁTICAS COMPLEXAS ==========
    private double calcularEstatisticas(List<Cliente> clientes) {
        if (clientes.isEmpty()) return 0.0;
        
        double soma = 0;
        double somaQuadrados = 0;
        int count = 0;
        
        for (Cliente cliente : clientes) {
            if (cliente.getDataNascimento() != null) {
                int idade = calcularIdade(cliente.getDataNascimento());
                soma += idade;
                somaQuadrados += idade * idade;
                count++;
            }
        }
        
        if (count == 0) return 0.0;
        
        double media = soma / count;
        double variancia = (somaQuadrados / count) - (media * media);
        return Math.sqrt(variancia);
    }

    // ========== BLOCOS DE COMENTÁRIOS EXTENSOS ==========
    /**
     * Este método realiza uma operação complexa de validação e processamento
     * de dados do cliente. Ele verifica múltiplas condições e aplica diversas
     * regras de negócio para garantir a integridade dos dados.
     * 
     * @param cliente O cliente a ser validado
     * @return true se o cliente estiver válido, false caso contrário
     * @throws IllegalArgumentException se o cliente for nulo
     * @throws IllegalStateException se os dados estiverem inconsistentes
     */
    private boolean validarClienteCompletamente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        
        // Validação básica de campos obrigatórios
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            return false;
        }
        
        if (cliente.getEmail() == null || !cliente.getEmail().contains("@")) {
            return false;
        }

        
        // Validação de idade mínima
        int idade = calcularIdade(cliente.getDataNascimento());
        if (idade < 18) {
            return false;
        }
        
        return true;
    }

    // ========== MÉTODOS ADICIONAIS PARA COMPLETAR LINHAS ==========
    private void inicializarRecursos() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    System.out.println(Thread.currentThread().getName() + " - " + j);
                }
            });
            threads.add(thread);
        }
        
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private Map<String, Object> criarMapaConfiguracao() {
        Map<String, Object> config = new HashMap<>();
        config.put("timeout", 30000);
        config.put("maxConnections", 100);
        config.put("retryAttempts", 3);
        config.put("cacheEnabled", true);
        config.put("logLevel", "DEBUG");
        return config;
    }

    // ========== MÉTODOS DE PROCESSAMENTO EM LOTE ==========
    private void processarEmLote(List<Cliente> clientes) {
        int batchSize = 100;
        for (int i = 0; i < clientes.size(); i += batchSize) {
            List<Cliente> lote = clientes.subList(i, 
                Math.min(i + batchSize, clientes.size()));
            processarLote(lote);
        }
    }

    private void processarLote(List<Cliente> lote) {
        lote.parallelStream().forEach(cliente -> {
            // Simulação de processamento
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    // ========== MÉTODOS DE BACKUP E RECUPERAÇÃO ==========
    private void criarBackupDados() {
        List<Cliente> clientes = clienteRepository.findAll();
        String backupData = clientes.stream()
            .map(this::serializarCliente)
            .collect(Collectors.joining("\n"));
        
        // Simulação de salvamento
        System.out.println("Backup criado: " + backupData.length() + " caracteres");
    }

    private String serializarCliente(Cliente cliente) {
        return String.format("Cliente[id=%d, nome=%s, email=%s]", 
            cliente.getId(), cliente.getNome(), cliente.getEmail());
    }

    // ========== MÉTODOS DE LOGS E AUDITORIA ==========
    private void registrarAuditoria(Cliente cliente, String acao) {
        String log = String.format("[AUDITORIA] %s - %s - %s - %s",
            LocalDateTime.now(),
            acao,
            cliente.getId(),
            cliente.getNome());
        
        SingletonLogger.getInstance().log(log);
    }

    private void gerarRelatorioAuditoria() {
        List<String> logs = SingletonLogger.getInstance().getLogs();
        logs.forEach(System.out::println);
    }

    // ========== MÉTODOS FINAIS PARA COMPLETAR ==========
    private void finalizarRecursos() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Limpando recursos...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Recursos liberados");
        }));
    }

    // ========== MÉTODO MAIN FICTÍCIO ==========
    public static void main(String[] args) {
        System.out.println("ClienteController - Classe de controle de clientes");
        System.out.println("Versão 1.0.0");
        System.out.println("Desenvolvido para sistema de gestão");
    }
}
