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

@Controller
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    // Método para listar os clientes
    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        Iterable<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);
        return "clientes";  // Página de listagem de clientes
    }

    // Método para exibir o formulário de adição
    @GetMapping("/addCliente")
    public String showForm(Model model) {
        model.addAttribute("cliente", new Cliente());  // Formulário para adicionar cliente
        return "formularioCliente";  // Página do formulário
    }

    // Método para adicionar um cliente
    @PostMapping("/addCliente")
    public String addCliente(@RequestParam String nome, 
                             @RequestParam String email,
                             @RequestParam String genero,
                             @RequestParam String dataNascimento,
                             @RequestParam String cpf,
                             @RequestParam String telefone,
                             @RequestParam String senha1,
                             @RequestParam String senha2,  // Campo para confirmar a senha
                             @RequestParam String enderecoEntrega, 
                             @RequestParam String descricaoEndereco,
                             @RequestParam String numeroCartao,
                             @RequestParam String bandeira,
                             @RequestParam String codigoSeguranca,
                             @RequestParam String nomeCartao,
                             @RequestParam String tipoResidencia,  // Novo campo
                             @RequestParam String logradouro,  // Novo campo
                             @RequestParam String numero,  // Novo campo
                             @RequestParam String bairro,  // Novo campo
                             @RequestParam String cep,  // Novo campo
                             @RequestParam String cidade,  // Novo campo
                             @RequestParam String estado,  // Novo campo
                             @RequestParam String pais) {  // Novo campo
        // Validação: as senhas precisam ser iguais
        if (!senha1.equals(senha2)) {
            // Se as senhas não coincidirem, retorna um erro
            return "As senhas não coincidem. Tente novamente.";  // Isso pode ser uma mensagem para exibir no front-end
        }

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setGenero(genero);
        cliente.setDataNascimento(dataNascimento);
        cliente.setCpf(cpf);
        cliente.setTelefone(telefone);
        cliente.setSenha(senha1);  // Salvando a senha do cliente
        cliente.setEnderecoEntrega(enderecoEntrega);  // Salvando o endereço de entrega
        cliente.setDescricaoEndereco(descricaoEndereco);  // Salvando a descrição do endereço
        
        // Salvando os dados do cartão
        cliente.setNumeroCartao(numeroCartao);
        cliente.setBandeira(bandeira);
        cliente.setCodigoSeguranca(codigoSeguranca);
        cliente.setNomeCartao(nomeCartao);

        
        // Salvando os novos campos de endereço
        cliente.setTipoResidencia(tipoResidencia);
        cliente.setLogradouro(logradouro);
        cliente.setNumero(numero);
        cliente.setBairro(bairro);
        cliente.setCep(cep);
        cliente.setCidade(cidade);
        cliente.setEstado(estado);
        cliente.setPais(pais);

        // Atribuindo a data de cadastro
        cliente.setDataCadastro(java.time.LocalDateTime.now());  // Data e hora de criação

        clienteRepository.save(cliente);  // Salva o cliente com a data de cadastro
        return "redirect:/clientes";  // Redireciona após salvar
    }

    // Método para exibir o formulário de edição
    @GetMapping("/editarCliente/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        model.addAttribute("cliente", cliente);  // Passa o cliente para o formulário
        return "formularioCliente";  // Página do formulário de edição
    }

    // Método para salvar as alterações do cliente
    @PostMapping("/editarCliente/{id}")
    public String salvarAlteracoes(@PathVariable Long id, 
                                    @RequestParam String nome, 
                                    @RequestParam String email,
                                    @RequestParam String genero, 
                                    @RequestParam String dataNascimento,
                                    @RequestParam String cpf,
                                    @RequestParam String telefone,
                                    @RequestParam String enderecoEntrega, 
                                    @RequestParam String descricaoEndereco,
                                    @RequestParam String senha,
                                    @RequestParam String numeroCartao,
                                    @RequestParam String bandeira,
                                    @RequestParam String codigoSeguranca,
                                    @RequestParam String nomeCartao,
                                    @RequestParam String tipoResidencia,  // Novo campo
                                    @RequestParam String logradouro,  // Novo campo
                                    @RequestParam String numero,  // Novo campo
                                    @RequestParam String bairro,  // Novo campo
                                    @RequestParam String cep,  // Novo campo
                                    @RequestParam String cidade,  // Novo campo
                                    @RequestParam String estado,  // Novo campo
                                    @RequestParam String pais) {  // Novo campo
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        // Atualiza todos os campos
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setGenero(genero);
        cliente.setDataNascimento(dataNascimento);
        cliente.setCpf(cpf);
        cliente.setTelefone(telefone);
        cliente.setEnderecoEntrega(enderecoEntrega);  // Atualiza o endereço de entrega
        cliente.setDescricaoEndereco(descricaoEndereco);  // Atualiza a descrição do endereço
        if (senha != null && !senha.isEmpty()) {
        cliente.setSenha(senha);  // Atualiza a senha se fornecida
    }
        
        // Atualiza os dados do cartão
        cliente.setNumeroCartao(numeroCartao);
        cliente.setBandeira(bandeira);
        cliente.setCodigoSeguranca(codigoSeguranca);
        cliente.setNomeCartao(nomeCartao);

        
        // Atualiza os novos campos de endereço
        cliente.setTipoResidencia(tipoResidencia);
        cliente.setLogradouro(logradouro);
        cliente.setNumero(numero);
        cliente.setBairro(bairro);
        cliente.setCep(cep);
        cliente.setCidade(cidade);
        cliente.setEstado(estado);
        cliente.setPais(pais);
        
        // Atribui a data de alteração
        cliente.setDataAlteracao(java.time.LocalDateTime.now());  // Data e hora da alteração
        
        clienteRepository.save(cliente);  // Salva as alterações no banco de dados
        
        return "redirect:/clientes";  // Redireciona após salvar
    }

    // Método para excluir um cliente
    @GetMapping("/excluirCliente/{id}")
    public String excluirCliente(@PathVariable Long id) {
        clienteService.excluirCliente(id);  // Chama o serviço para excluir o cliente
        return "redirect:/clientes";  // Redireciona para a lista de clientes
    }
}
