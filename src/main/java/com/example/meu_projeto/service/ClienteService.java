package com.example.meu_projeto.service;

import com.example.meu_projeto.model.Cliente;
import com.example.meu_projeto.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Método para adicionar um novo cliente
    public Cliente adicionarCliente(Cliente cliente) {
        // Definir a data de cadastro no momento da criação
        cliente.setDataCadastro(java.time.LocalDateTime.now());  // Data e hora de criação
        return clienteRepository.save(cliente);  // Salva o cliente no banco de dados
    }

    // Método para listar todos os clientes
    public Iterable<Cliente> listarClientes() {
        return clienteRepository.findAll();  // Retorna todos os clientes do banco de dados
    }

    // Método para encontrar um cliente por ID
    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);  // Retorna o cliente com o ID fornecido
    }

    // Método para excluir um cliente por ID
    public void excluirCliente(Long id) {
        clienteRepository.deleteById(id);  // Exclui o cliente com o ID fornecido
    }

    // Método para atualizar um cliente existente
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        if (clienteRepository.existsById(id)) {
            clienteAtualizado.setId(id);  // Define o ID do cliente atualizado
            clienteAtualizado.setDataAlteracao(java.time.LocalDateTime.now());  // Data e hora da alteração
            return clienteRepository.save(clienteAtualizado);  // Salva o cliente atualizado no banco
        }
        return null;  // Se o cliente não existir, retorna null
    }
}
