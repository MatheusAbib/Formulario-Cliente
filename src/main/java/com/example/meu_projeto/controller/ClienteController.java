package com.example.meu_projeto.controller;

import com.example.meu_projeto.model.Cliente;
import com.example.meu_projeto.model.Endereco;
import com.example.meu_projeto.model.Cartao;
import com.example.meu_projeto.repository.ClienteRepository;
import com.example.meu_projeto.service.ClienteService;
import com.example.meu_projeto.util.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

@GetMapping
public String listarClientes(
        @RequestParam(defaultValue = "0") int pagina,
        @RequestParam(defaultValue = "10") int tamanho,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String telefone,
        @RequestParam(required = false) String cpf,
        @RequestParam(required = false) String cep,
        Model model) {
    
        Page<Cliente> paginaClientes = clienteService.filtrarClientes(nome, telefone, cpf, cep, pagina, tamanho);

        int totalPaginas = paginaClientes.getTotalPages();
        if (totalPaginas == 0) {
            totalPaginas = 1;
        }

        model.addAttribute("clientes", paginaClientes.getContent());
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalItens", paginaClientes.getTotalElements());
        model.addAttribute("tamanhoPagina", tamanho);

        return "clientes";
        }

    @GetMapping("/add")
    public String showForm(Model model) {
        Cliente cliente = new Cliente();
        Endereco enderecoInicial = new Endereco();
        cliente.addEndereco(enderecoInicial);
        cliente.setCartao(new Cartao());

        model.addAttribute("cliente", cliente);
        return "formularioCliente";
    }

@PostMapping("/add")
public String addCliente(@ModelAttribute Cliente cliente,
                       @RequestParam String senha,
                       @RequestParam String confirmarSenha,
                       @RequestParam(value = "enderecos[0].tipoResidencia", required = false) String tipoResidencia,
                       @RequestParam(value = "enderecos[0].numero", required = false) String numero,
                       @RequestParam(value = "enderecos[0].bairro", required = false) String bairro,
                       @RequestParam(value = "enderecos[0].cep", required = false) String cep,
                       @RequestParam(value = "enderecos[0].cidade", required = false) String cidade,
                       @RequestParam(value = "enderecos[0].estado", required = false) String estado,
                       @RequestParam(value = "enderecos[0].pais", required = false) String pais,
                       @RequestParam(value = "enderecos[0].enderecoEntrega", required = false) String enderecoEntrega,
                       @RequestParam(value = "enderecos[0].descricaoEndereco", required = false) String descricaoEndereco,
                       Model model,
                       RedirectAttributes redirectAttributes) {
    
    if (!senha.equals(confirmarSenha)) {
        model.addAttribute("error", "As senhas não coincidem");
        model.addAttribute("toastType", "error");
        model.addAttribute("toastTitle", "Erro!");
        model.addAttribute("toastMessage", "As senhas não coincidem. Por favor, verifique.");
        return "formularioCliente";
    }
    
    if (senha.length() < 6) {
        model.addAttribute("error", "A senha deve ter pelo menos 6 caracteres");
        model.addAttribute("toastType", "error");
        model.addAttribute("toastTitle", "Erro!");
        model.addAttribute("toastMessage", "A senha deve ter pelo menos 6 caracteres.");
        return "formularioCliente";
    }

    if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
        model.addAttribute("toastType", "error");
        model.addAttribute("toastTitle", "Erro!");
        model.addAttribute("toastMessage", "O nome é obrigatório.");
        return "formularioCliente";
    }
    
    if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
        model.addAttribute("toastType", "error");
        model.addAttribute("toastTitle", "Erro!");
        model.addAttribute("toastMessage", "O e-mail é obrigatório.");
        return "formularioCliente";
    }

    cliente.setSenha(senha);
    cliente.setDataCadastro(LocalDateTime.now());

    if (cliente.getEnderecos() != null && !cliente.getEnderecos().isEmpty()) {
        for (Endereco endereco : cliente.getEnderecos()) {
            endereco.setCliente(cliente);
        }
    } else {
        Endereco endereco = new Endereco();
        endereco.setTipoResidencia(tipoResidencia);
        endereco.setNumero(numero);
        endereco.setBairro(bairro);
        endereco.setCep(cep);
        endereco.setCidade(cidade);
        endereco.setEstado(estado);
        endereco.setPais(pais);
        endereco.setEnderecoEntrega(enderecoEntrega);
        endereco.setDescricaoEndereco(descricaoEndereco);
        endereco.setCliente(cliente);
        cliente.addEndereco(endereco);
    }
    
    if (cliente.getCartao() != null) {
        cliente.getCartao().setCliente(cliente);
    }

    try {
        clienteService.adicionarCliente(cliente);
        
        redirectAttributes.addFlashAttribute("toastType", "success");
        redirectAttributes.addFlashAttribute("toastTitle", "Sucesso!");
        redirectAttributes.addFlashAttribute("toastMessage", "Cliente cadastrado com sucesso.");
        
        return "redirect:/clientes?pagina=0&tamanho=10";
        
    } catch (Exception e) {
        model.addAttribute("toastType", "error");
        model.addAttribute("toastTitle", "Erro no servidor!");
        model.addAttribute("toastMessage", "Ocorreu um erro ao salvar o cliente. Tente novamente.");
        return "formularioCliente";
    }
}

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        if (cliente.getEnderecos() == null || cliente.getEnderecos().isEmpty()) {
            cliente.addEndereco(new Endereco());
        }
        if (cliente.getCartao() == null) {
            cliente.setCartao(new Cartao());
        }

        model.addAttribute("cliente", cliente);
        return "formularioCliente";
    }

@PostMapping("/editar/{id}")
public String salvarAlteracoes(@PathVariable Long id,
                             @ModelAttribute Cliente clienteAtualizado,
                             @RequestParam(required = false) String senha,
                             @RequestParam(required = false) String confirmarSenha,
                             Model model,
                             RedirectAttributes redirectAttributes) {
    
    if (clienteAtualizado.getNome() == null || clienteAtualizado.getNome().trim().isEmpty()) {
        model.addAttribute("cliente", clienteAtualizado);
        model.addAttribute("toastType", "error");
        model.addAttribute("toastTitle", "Erro!");
        model.addAttribute("toastMessage", "O nome é obrigatório.");
        return "formularioCliente";
    }
    
    if (clienteAtualizado.getEmail() == null || clienteAtualizado.getEmail().trim().isEmpty()) {
        model.addAttribute("cliente", clienteAtualizado);
        model.addAttribute("toastType", "error");
        model.addAttribute("toastTitle", "Erro!");
        model.addAttribute("toastMessage", "O e-mail é obrigatório.");
        return "formularioCliente";
    }

    if (senha != null && !senha.isEmpty()) {
        if (confirmarSenha == null || !senha.equals(confirmarSenha)) {
            model.addAttribute("cliente", clienteAtualizado);
            model.addAttribute("toastType", "error");
            model.addAttribute("toastTitle", "Erro!");
            model.addAttribute("toastMessage", "As senhas não coincidem.");
            return "formularioCliente";
        }
        
        if (senha.length() < 6) {
            model.addAttribute("cliente", clienteAtualizado);
            model.addAttribute("toastType", "error");
            model.addAttribute("toastTitle", "Erro!");
            model.addAttribute("toastMessage", "A senha deve ter pelo menos 6 caracteres.");
            return "formularioCliente";
        }
        
        clienteAtualizado.setSenha(senha);
    }

    try {
        Cliente clienteAtualizadoSalvo = clienteService.atualizarCliente(id, clienteAtualizado);
        
        if (clienteAtualizadoSalvo == null) {
            model.addAttribute("cliente", clienteAtualizado);
            model.addAttribute("toastType", "error");
            model.addAttribute("toastTitle", "Erro!");
            model.addAttribute("toastMessage", "Cliente não encontrado.");
            return "formularioCliente";
        }

        redirectAttributes.addFlashAttribute("toastType", "success");
        redirectAttributes.addFlashAttribute("toastTitle", "Sucesso!");
        redirectAttributes.addFlashAttribute("toastMessage", "Cliente atualizado com sucesso.");
        
        return "redirect:/clientes?pagina=0&tamanho=10";
        
    } catch (Exception e) {
        model.addAttribute("cliente", clienteAtualizado);
        model.addAttribute("toastType", "error");
        model.addAttribute("toastTitle", "Erro no servidor!");
        model.addAttribute("toastMessage", "Ocorreu um erro ao atualizar o cliente. Tente novamente.");
        return "formularioCliente";
    }
}

@GetMapping("/excluir/{id}")
public String excluirCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    clienteService.excluirCliente(id);
    
    redirectAttributes.addFlashAttribute("toastType", "success");
    redirectAttributes.addFlashAttribute("toastTitle", "Sucesso!");
    redirectAttributes.addFlashAttribute("toastMessage", "Cliente excluído com sucesso.");
    
    return "redirect:/clientes?pagina=0&tamanho=10";
}

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

        if (atualizado.getEnderecos() != null && !atualizado.getEnderecos().isEmpty()) {
            original.getEnderecos().clear();
            for (Endereco end : atualizado.getEnderecos()) {
                if (end != null) {
                    Endereco novoEnd = new Endereco();
                    novoEnd.setTipoResidencia(end.getTipoResidencia());
                    novoEnd.setNumero(end.getNumero());
                    novoEnd.setBairro(end.getBairro());
                    novoEnd.setCep(end.getCep());
                    novoEnd.setCidade(end.getCidade());
                    novoEnd.setEstado(end.getEstado());
                    novoEnd.setPais(end.getPais());
                    novoEnd.setEnderecoEntrega(end.getEnderecoEntrega());
                    novoEnd.setDescricaoEndereco(end.getDescricaoEndereco());
                    novoEnd.setCliente(original);
                    original.addEndereco(novoEnd);
                }
            }
        }

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

    private boolean validarCPFComplexo(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return false;
        }
        
        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;
            
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;
            
            return (Character.getNumericValue(cpf.charAt(9)) == digito1 && 
                   Character.getNumericValue(cpf.charAt(10)) == digito2);
        } catch (NumberFormatException e) {
            return false;
        }
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
            if (cliente.getEnderecos() != null && !cliente.getEnderecos().isEmpty()) {
                for (Endereco endereco : cliente.getEnderecos()) {
                    if (endereco.getEstado() != null) {
                        String estado = endereco.getEstado();
                        contagem.put(estado, contagem.getOrDefault(estado, 0) + 1);
                    }
                }
            }
        }
        return contagem;
    }

    {
        System.out.println("Bloco de inicialização executado");
        List<String> listaTemporaria = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            listaTemporaria.add("Item " + i);
        }
    }


    private <T> List<T> filtrarLista(List<T> lista, java.util.function.Predicate<T> predicado) {
        return lista.stream()
            .filter(predicado)
            .collect(Collectors.toList());
    }
    

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
}