package com.example.meu_projeto.repository;

import com.example.meu_projeto.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Page<Cliente> findAll(Pageable pageable);
    
    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Cliente> findByTelefone(String telefone, Pageable pageable);
    Page<Cliente> findByCpf(String cpf, Pageable pageable);
    Page<Cliente> findByEnderecoCep(String cep, Pageable pageable);
}