package com.example.meu_projeto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    

    @GetMapping("/")
    public String home() {
        return "redirect:/clientes";
    }
    
    @GetMapping("/health")
    public String health() {
        return "{\"status\": \"UP\", \"timestamp\": \"" + java.time.LocalDateTime.now() + "\"}";
    }
    

    @GetMapping("/status")
    public String status(Model model) {
        model.addAttribute("appName", "Sistema de Clientes");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("status", "Operacional");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "status";
    }
    

    @GetMapping("/test")
    public String test() {
        return "✅ Aplicação está funcionando! Acesse <a href='/clientes'>/clientes</a> para o sistema.";
    }
}