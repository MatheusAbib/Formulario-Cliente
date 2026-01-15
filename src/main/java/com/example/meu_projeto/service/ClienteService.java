package com.example.meu_projeto.service;

import com.example.meu_projeto.model.Cliente;
import com.example.meu_projeto.repository.ClienteRepository;
import com.example.meu_projeto.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente adicionarCliente(Cliente cliente) {
        cliente.setSenha(PasswordUtil.encrypt(cliente.getSenha()));
        cliente.setDataCadastro(java.time.LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    public Page<Cliente> listarClientesPaginados(int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        return clienteRepository.findAll(pageable);
    }

    public Page<Cliente> filtrarClientes(String nome, String telefone, String cpf, String cep, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        
        if (nome != null && !nome.isEmpty()) {
            return clienteRepository.findByNomeContainingIgnoreCase(nome, pageable);
        }
        
        // Filtra por telefone
        if (telefone != null && !telefone.isEmpty()) {
            return clienteRepository.findByTelefone(telefone, pageable);
        }
        
        if (cpf != null && !cpf.isEmpty()) {
            return clienteRepository.findByCpf(cpf, pageable);
        }
        
        if (cep != null && !cep.isEmpty()) {
            return clienteRepository.findByEnderecoCep(cep, pageable);
        }
        
        return clienteRepository.findAll(pageable);
    }

    public Iterable<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public void excluirCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        if (clienteRepository.existsById(id)) {
            Optional<Cliente> clienteExistente = clienteRepository.findById(id);
            
            if (clienteExistente.isPresent()) {
                Cliente cliente = clienteExistente.get();
                
                if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().isEmpty()) {
                    cliente.setSenha(PasswordUtil.encrypt(clienteAtualizado.getSenha()));
                }
                
                cliente.setNome(clienteAtualizado.getNome());
                cliente.setEmail(clienteAtualizado.getEmail());
                cliente.setGenero(clienteAtualizado.getGenero());
                cliente.setDataNascimento(clienteAtualizado.getDataNascimento());
                cliente.setCpf(clienteAtualizado.getCpf());
                cliente.setTelefone(clienteAtualizado.getTelefone());
                cliente.setDataAlteracao(java.time.LocalDateTime.now());
                
                if (clienteAtualizado.getEndereco() != null) {
                    cliente.setEndereco(clienteAtualizado.getEndereco());
                }
                
                if (clienteAtualizado.getCartao() != null) {
                    cliente.setCartao(clienteAtualizado.getCartao());
                }
                
                return clienteRepository.save(cliente);
            }
        }
        return null;
    }
}