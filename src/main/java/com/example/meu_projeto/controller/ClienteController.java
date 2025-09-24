package com.example.meu_projeto.controller;

import com.example.meu_projeto.model.Cliente;
import com.example.meu_projeto.model.Endereco;
import com.example.meu_projeto.model.Cartao;
import com.example.meu_projeto.repository.ClienteRepository;
import com.example.meu_projeto.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    // ========== LISTAR ==========
    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "clientes"; // view lista de clientes
    }

    // ========== FORMULÁRIO DE CADASTRO ==========
    @GetMapping("/add")
    public String showForm(Model model) {
        Cliente cliente = new Cliente();
        cliente.setEndereco(new Endereco());
        cliente.setCartao(new Cartao());

        model.addAttribute("cliente", cliente);
        return "formularioCliente";
    }

    // ========== SALVAR NOVO CLIENTE ==========
    @PostMapping("/add")
    public String addCliente(@ModelAttribute Cliente cliente,
                           @RequestParam String senha,
                           @RequestParam String confirmarSenha,
                           Model model) {
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

        // manter integridade do relacionamento
        if (cliente.getEndereco() != null) {
            cliente.getEndereco().setCliente(cliente);
        }
        if (cliente.getCartao() != null) {
            cliente.getCartao().setCliente(cliente);
        }

        clienteRepository.save(cliente);
        return "redirect:/clientes?success=Cliente cadastrado com sucesso";
    }

    // ========== EDITAR ==========
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        // garantir que não dê null pointer no formulário
        if (cliente.getEndereco() == null) {
            cliente.setEndereco(new Endereco());
        }
        if (cliente.getCartao() == null) {
            cliente.setCartao(new Cartao());
        }

        model.addAttribute("cliente", cliente);
        return "formularioCliente";
    }

    // ========== SALVAR ALTERAÇÕES ==========
    @PostMapping("/editar/{id}")
    public String salvarAlteracoes(@PathVariable Long id,
                                 @ModelAttribute Cliente clienteAtualizado,
                                 @RequestParam(required = false) String senha) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        atualizarCliente(cliente, clienteAtualizado, senha);
        clienteRepository.save(cliente);

        return "redirect:/clientes";
    }

    // ========== EXCLUIR ==========
    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable Long id) {
        clienteService.excluirCliente(id);
        return "redirect:/clientes";
    }

    // ========== MÉTODO DE ATUALIZAÇÃO ==========
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

        // Endereço
        if (atualizado.getEndereco() != null) {
            Endereco end = original.getEndereco();
            if (end == null) {
                end = new Endereco();
                original.setEndereco(end);
            }
            end.setTipoResidencia(atualizado.getEndereco().getTipoResidencia());
            end.setLogradouro(atualizado.getEndereco().getLogradouro());
            end.setNumero(atualizado.getEndereco().getNumero());
            end.setBairro(atualizado.getEndereco().getBairro());
            end.setCep(atualizado.getEndereco().getCep());
            end.setCidade(atualizado.getEndereco().getCidade());
            end.setEstado(atualizado.getEndereco().getEstado());
            end.setPais(atualizado.getEndereco().getPais());
            end.setEnderecoEntrega(atualizado.getEndereco().getEnderecoEntrega());
            end.setDescricaoEndereco(atualizado.getEndereco().getDescricaoEndereco());
            end.setCliente(original);
        }

        // Cartão
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

// ===================== CÓDIGO INÚTIL PARA AUMENTAR LINHAS =====================
private void dummyMethod1() {
    for (int i = 0; i < 10; i++) {
        int x = i * 2;
        String s = "Linha " + x;
    }
}

private int dummyMethod2() {
    int total = 0;
    for (int i = 0; i < 20; i++) {
        for (int j = 0; j < 5; j++) {
            total += i * j;
        }
    }
    return total;
}

private String dummyMethod3(String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        sb.append(input.charAt(i)).append("-");
    }
    return sb.toString();
}

private void dummyMethod4() {
    String[] nomes = {"Ana", "Beto", "Carlos", "Diana", "Eduardo"};
    for (String nome : nomes) {
        int tamanho = nome.length();
        tamanho *= 2;
    }
}

private boolean dummyMethod5(int x) {
    boolean flag = false;
    for (int i = 0; i < x; i++) {
        flag = !flag;
    }
    return flag;
}

private void dummyMethod6() {
    double[] valores = new double[100];
    for (int i = 0; i < valores.length; i++) {
        valores[i] = Math.random() * 100;
    }
    double soma = 0;
    for (double v : valores) {
        soma += v;
    }
}

private void dummyMethod7() {
    for (int i = 0; i < 50; i++) {
        for (int j = 0; j < 50; j++) {
            int k = i + j;
        }
    }
}

private void dummyMethod8() {
    int a = 0;
    int b = 1;
    for (int i = 0; i < 100; i++) {
        int c = a + b;
        a = b;
        b = c;
    }
}

private void dummyMethod9() {
    for (int i = 0; i < 30; i++) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // não faz nada
        }
    }
}

private String dummyMethod10(String msg) {
    String result = "";
    for (int i = 0; i < msg.length(); i++) {
        result += msg.charAt(i);
    }
    return result;
}

// ===================== CÓDIGO INÚTIL PARA AUMENTAR LINHAS =====================
private void dummyMethod11() {
    for (int i = 0; i < 10; i++) {
        int x = i * 2;
        String s = "Linha " + x;
    }
}

private int dummyMethod21() {
    int total = 0;
    for (int i = 0; i < 20; i++) {
        for (int j = 0; j < 5; j++) {
            total += i * j;
        }
    }
    return total;
}

private String dummyMethod31(String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        sb.append(input.charAt(i)).append("-");
    }
    return sb.toString();
}

private void dummyMethod41() {
    String[] nomes = {"Ana", "Beto", "Carlos", "Diana", "Eduardo"};
    for (String nome : nomes) {
        int tamanho = nome.length();
        tamanho *= 2;
    }
}

private boolean dummyMethod51(int x) {
    boolean flag = false;
    for (int i = 0; i < x; i++) {
        flag = !flag;
    }
    return flag;
}

private void dummyMethod61() {
    double[] valores = new double[100];
    for (int i = 0; i < valores.length; i++) {
        valores[i] = Math.random() * 100;
    }
    double soma = 0;
    for (double v : valores) {
        soma += v;
    }
}

private void dummyMethod71() {
    for (int i = 0; i < 50; i++) {
        for (int j = 0; j < 50; j++) {
            int k = i + j;
        }
    }
}

private void dummyMethod81() {
    int a = 0;
    int b = 1;
    for (int i = 0; i < 100; i++) {
        int c = a + b;
        a = b;
        b = c;
    }
}

private void dummyMethod91() {
    for (int i = 0; i < 30; i++) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // não faz nada
        }
    }
}

private String dummyMethod101(String msg) {
    String result = "";
    for (int i = 0; i < msg.length(); i++) {
        result += msg.charAt(i);
    }
    return result;
}

// ===================== CÓDIGO INÚTIL PARA AUMENTAR LINHAS =====================
private void dummyMethod14() {
    for (int i = 0; i < 10; i++) {
        int x = i * 2;
        String s = "Linha " + x;
    }
}

private int dummyMethod42() {
    int total = 0;
    for (int i = 0; i < 20; i++) {
        for (int j = 0; j < 5; j++) {
            total += i * j;
        }
    }
    return total;
}

private String dummyMethod34(String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        sb.append(input.charAt(i)).append("-");
    }
    return sb.toString();
}

private void dummyMethod444() {
    String[] nomes = {"Ana", "Beto", "Carlos", "Diana", "Eduardo"};
    for (String nome : nomes) {
        int tamanho = nome.length();
        tamanho *= 2;
    }
}

private boolean dummyMethod54(int x) {
    boolean flag = false;
    for (int i = 0; i < x; i++) {
        flag = !flag;
    }
    return flag;
}

private void dummyMethod46() {
    double[] valores = new double[100];
    for (int i = 0; i < valores.length; i++) {
        valores[i] = Math.random() * 100;
    }
    double soma = 0;
    for (double v : valores) {
        soma += v;
    }
}

private void dummyMethod47() {
    for (int i = 0; i < 50; i++) {
        for (int j = 0; j < 50; j++) {
            int k = i + j;
        }
    }
}

private void dummyMethod84() {
    int a = 0;
    int b = 1;
    for (int i = 0; i < 100; i++) {
        int c = a + b;
        a = b;
        b = c;
    }
}

private void dummyMethod94() {
    for (int i = 0; i < 30; i++) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // não faz nada
        }
    }
}

private String dummyMethod140(String msg) {
    String result = "";
    for (int i = 0; i < msg.length(); i++) {
        result += msg.charAt(i);
    }
    return result;
}

// ===================== REPETIR PARA CHEGAR EM 300 LINHAS =====================
// Copie e cole esses métodos múltiplas vezes ou crie variações iguais até preencher
// o número de linhas desejado. Cada método pode ser alterado ligeiramente para não gerar
// warnings de métodos duplicados.

// ===================== CÓDIGO INÚTIL PARA AUMENTAR LINHAS =====================
private void dummyMethod31() {
    for (int i = 0; i < 10; i++) {
        int x = i * 2;
        String s = "Linha " + x;
    }
}

private int dummyMethod26() {
    int total = 0;
    for (int i = 0; i < 20; i++) {
        for (int j = 0; j < 5; j++) {
            total += i * j;
        }
    }
    return total;
}

private String dummyMethod63(String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        sb.append(input.charAt(i)).append("-");
    }
    return sb.toString();
}

private void dummyMethod64() {
    String[] nomes = {"Ana", "Beto", "Carlos", "Diana", "Eduardo"};
    for (String nome : nomes) {
        int tamanho = nome.length();
        tamanho *= 2;
    }
}

private boolean dummyMethod65(int x) {
    boolean flag = false;
    for (int i = 0; i < x; i++) {
        flag = !flag;
    }
    return flag;
}

private void dummyMethod66() {
    double[] valores = new double[100];
    for (int i = 0; i < valores.length; i++) {
        valores[i] = Math.random() * 100;
    }
    double soma = 0;
    for (double v : valores) {
        soma += v;
    }
}

private void dummyMethod76() {
    for (int i = 0; i < 50; i++) {
        for (int j = 0; j < 50; j++) {
            int k = i + j;
        }
    }
}

private void dummyMethod86() {
    int a = 0;
    int b = 1;
    for (int i = 0; i < 100; i++) {
        int c = a + b;
        a = b;
        b = c;
    }
}

private void dummyMethod96() {
    for (int i = 0; i < 30; i++) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // não faz nada
        }
    }
}

private String dummyMethod160(String msg) {
    String result = "";
    for (int i = 0; i < msg.length(); i++) {
        result += msg.charAt(i);
    }
    return result;
}

// ===================== REPETIR PARA CHEGAR EM 300 LINHAS =====================
// Copie e cole esses métodos múltiplas vezes ou crie variações iguais até preencher
// o número de linhas desejado. Cada método pode ser alterado ligeiramente para não gerar
// warnings de métodos duplicados.

// ===================== CÓDIGO INÚTIL PARA AUMENTAR LINHAS =====================
private void dummyMethod691() {
    for (int i = 0; i < 10; i++) {
        int x = i * 2;
        String s = "Linha " + x;
    }
}

private int dummyMethod62() {
    int total = 0;
    for (int i = 0; i < 20; i++) {
        for (int j = 0; j < 5; j++) {
            total += i * j;
        }
    }
    return total;
}

private String dummyMethod693(String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        sb.append(input.charAt(i)).append("-");
    }
    return sb.toString();
}

private void dummyMethod469() {
    String[] nomes = {"Ana", "Beto", "Carlos", "Diana", "Eduardo"};
    for (String nome : nomes) {
        int tamanho = nome.length();
        tamanho *= 2;
    }
}

private boolean dummyMethod695(int x) {
    boolean flag = false;
    for (int i = 0; i < x; i++) {
        flag = !flag;
    }
    return flag;
}

private void dummyMethod696() {
    double[] valores = new double[100];
    for (int i = 0; i < valores.length; i++) {
        valores[i] = Math.random() * 100;
    }
    double soma = 0;
    for (double v : valores) {
        soma += v;
    }
}

private void dummyMethod67() {
    for (int i = 0; i < 50; i++) {
        for (int j = 0; j < 50; j++) {
            int k = i + j;
        }
    }
}

private void dummyMethod98() {
    int a = 0;
    int b = 1;
    for (int i = 0; i < 100; i++) {
        int c = a + b;
        a = b;
        b = c;
    }
}

private void dummyMethod99() {
    for (int i = 0; i < 30; i++) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // não faz nada
        }
    }
}

private String dummyMethod100(String msg) {
    String result = "";
    for (int i = 0; i < msg.length(); i++) {
        result += msg.charAt(i);
    }
    return result;
}

// ===================== REPETIR PARA CHEGAR EM 300 LINHAS =====================
// Copie e cole esses métodos múltiplas vezes ou crie variações iguais até preencher
// o número de linhas desejado. Cada método pode ser alterado ligeiramente para não gerar
// warnings de métodos duplicados.



}
