package com.example.meu_projeto.controller;

import com.example.meu_projeto.model.Cliente;
import com.example.meu_projeto.repository.ClienteRepository;
import com.example.meu_projeto.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;

@Controller
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    

    // Método para listar os clientes
    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "clientes";
    }

    // Método para exibir o formulário de adição
    @GetMapping("/addCliente")
    public String showForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "formularioCliente";
    }

    // Método para adicionar um cliente (usando ModelAttribute)
   @PostMapping("/addCliente")
public String addCliente(@ModelAttribute Cliente cliente,
                        @RequestParam String senha,
                        @RequestParam String confirmarSenha,
                        Model model) {
    
    // System.out.println("Dados recebidos - Senha: " + senha + ", Confirmar: " + confirmarSenha);
    
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

    // Método para exibir o formulário de edição
    @GetMapping("/editarCliente/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        model.addAttribute("cliente", cliente);
        return "formularioCliente";
    }

    // Método para salvar as alterações do cliente (usando ModelAttribute)
    @PostMapping("/editarCliente/{id}")
    public String salvarAlteracoes(@PathVariable Long id,
                                  @ModelAttribute Cliente clienteAtualizado,
                                  @RequestParam(required = false) String senha) {
        
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        // Atualiza os campos
        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setGenero(clienteAtualizado.getGenero());
        cliente.setDataNascimento(clienteAtualizado.getDataNascimento());
        cliente.setCpf(clienteAtualizado.getCpf());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEnderecoEntrega(clienteAtualizado.getEnderecoEntrega());
        cliente.setDescricaoEndereco(clienteAtualizado.getDescricaoEndereco());
        
        // Atualiza senha apenas se fornecida
        if (senha != null && !senha.isEmpty()) {
            cliente.setSenha(senha);
        }
        
        // Dados do cartão
        cliente.setNumeroCartao(clienteAtualizado.getNumeroCartao());
        cliente.setBandeira(clienteAtualizado.getBandeira());
        cliente.setCodigoSeguranca(clienteAtualizado.getCodigoSeguranca());
        cliente.setNomeCartao(clienteAtualizado.getNomeCartao());
        
        // Endereço
        cliente.setTipoResidencia(clienteAtualizado.getTipoResidencia());
        cliente.setLogradouro(clienteAtualizado.getLogradouro());
        cliente.setNumero(clienteAtualizado.getNumero());
        cliente.setBairro(clienteAtualizado.getBairro());
        cliente.setCep(clienteAtualizado.getCep());
        cliente.setCidade(clienteAtualizado.getCidade());
        cliente.setEstado(clienteAtualizado.getEstado());
        cliente.setPais(clienteAtualizado.getPais());
        
        cliente.setDataAlteracao(LocalDateTime.now());
        clienteRepository.save(cliente);
        
        return "redirect:/clientes";
    }

    // Método para excluir um cliente
    @GetMapping("/excluirCliente/{id}")
    public String excluirCliente(@PathVariable Long id) {
        clienteService.excluirCliente(id);
        return "redirect:/clientes";
    }
}