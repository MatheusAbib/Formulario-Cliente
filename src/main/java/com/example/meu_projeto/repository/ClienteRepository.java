package com.example.meu_projeto.repository;

import com.example.meu_projeto.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @EntityGraph(attributePaths = {"enderecos", "cartao"})
    Page<Cliente> findAll(Pageable pageable);

    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Cliente> findByTelefone(String telefone, Pageable pageable);
    Page<Cliente> findByCpf(String cpf, Pageable pageable);

    @Query("""
        SELECT DISTINCT c 
        FROM Cliente c 
        JOIN c.enderecos e 
        WHERE e.cep = :cep
    """)
    Page<Cliente> findByEnderecoCep(@Param("cep") String cep, Pageable pageable);
}
