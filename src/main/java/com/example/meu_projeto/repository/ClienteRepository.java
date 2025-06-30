package com.example.meu_projeto.repository;

import com.example.meu_projeto.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Não é necessário adicionar métodos aqui por enquanto, o JpaRepository já fornece os principais
}

