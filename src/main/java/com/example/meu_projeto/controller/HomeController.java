package com.example.meu_projeto.controller;

import com.example.meu_projeto.model.Cliente;
import com.example.meu_projeto.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping({"/", "/clientes"})
    public String clientes(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        PageRequest pageable = PageRequest.of(page, 10); 
        Page<Cliente> paginaClientes = clienteRepository.findAll(pageable);

        model.addAttribute("clientes", paginaClientes.getContent()); 
        model.addAttribute("pagina", paginaClientes);                

        return "clientes";
    }
}
