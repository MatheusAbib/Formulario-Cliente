        // Máscaras para os campos
        document.addEventListener('DOMContentLoaded', function() {
            // Máscara para CPF (000.000.000-00)
            const cpfInput = document.getElementById('cpf');
            cpfInput.addEventListener('input', function() {
                let value = this.value.replace(/\D/g, ''); // remove tudo que não é número
                
                // Limita a 11 dígitos
                value = value.substring(0, 11);
                
                // Aplica a formatação: 000.000.000-00
                if (value.length > 9) {
                    value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
                } else if (value.length > 6) {
                    value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, "$1.$2.$3");
                } else if (value.length > 3) {
                    value = value.replace(/(\d{3})(\d{1,3})/, "$1.$2");
                }
                
                this.value = value;
            });

            // Máscara para Telefone ((00) 00000-0000)
            const telefoneInput = document.getElementById('telefone');
            telefoneInput.addEventListener('input', function() {
                let value = this.value.replace(/\D/g, '');
                if (value.length > 0) {
                    value = '(' + value.substring(0, 2) + ') ' + value.substring(2);
                }
                if (value.length > 10) {
                    value = value.substring(0, 10) + '-' + value.substring(10);
                }
                this.value = value.substring(0, 15);
            });

            // Máscara para CEP (00000-000)
            const cepInput = document.getElementById('cep');
            cepInput.addEventListener('input', function() {
                let value = this.value.replace(/\D/g, '');
                if (value.length > 5) {
                    value = value.substring(0, 5) + '-' + value.substring(5);
                }
                this.value = value.substring(0, 9);
            });

            // Máscara para CVV (apenas números, max 4 dígitos)
            const cvvInput = document.getElementById('codigoSeguranca');
            cvvInput.addEventListener('input', function() {
                this.value = this.value.replace(/\D/g, '').substring(0, 3);
            });

            // Máscara para Número do Cartão (0000 0000 0000 0000)
            const cartaoInput = document.getElementById('numeroCartao');
            cartaoInput.addEventListener('input', function() {
                let value = this.value.replace(/\D/g, '');
                if (value.length > 4) {
                    value = value.substring(0, 4) + ' ' + value.substring(4);
                }
                if (value.length > 9) {
                    value = value.substring(0, 9) + ' ' + value.substring(9);
                }
                if (value.length > 14) {
                    value = value.substring(0, 14) + ' ' + value.substring(14);
                }
                this.value = value.substring(0, 19);
            });

            // Formatação automática para Logradouro (capitalize)
            const logradouroInput = document.getElementById('logradouro');
            logradouroInput.addEventListener('blur', function() {
                this.value = this.value.toLowerCase().replace(/(?:^|\s)\S/g, function(a) {
                    return a.toUpperCase();
                });
            });

            // Validação de formulário
         document.getElementById('formCadastro').onsubmit = function(event) {
    let isValid = true;
    
    // Validação básica de CPF (apenas formato)
    if (cpfInput.value.length < 14) {
        document.getElementById('cpfError').textContent = 'CPF inválido';
        document.getElementById('cpfError').classList.add('active');
        isValid = false;
    } else {
        document.getElementById('cpfError').classList.remove('active');
    }
    
    // Validação básica de CEP (apenas formato)
    if (cepInput.value.length < 9) {
        document.getElementById('cepError').textContent = 'CEP inválido';
        document.getElementById('cepError').classList.add('active');
        isValid = false;
    } else {
        document.getElementById('cepError').classList.remove('active');
    }
    
    // Validação básica de telefone (apenas formato)
    if (telefoneInput.value.length < 15) {
        document.getElementById('telefoneError').textContent = 'Telefone inválido';
        document.getElementById('telefoneError').classList.add('active');
        isValid = false;
    } else {
        document.getElementById('telefoneError').classList.remove('active');
    }
    
    // Validação básica de CVV (3 ou 4 dígitos)
    if (cvvInput.value.length < 3) {
        document.getElementById('codigoSegurancaError').textContent = 'CVV inválido';
        document.getElementById('codigoSegurancaError').classList.add('active');
        isValid = false;
    } else {
        document.getElementById('codigoSegurancaError').classList.remove('active');
    }
    
    // Validação de senha apenas para novo cadastro
    if (document.getElementById('confirmarSenha')) {
        const senha = document.getElementById('senha').value;
        const confirmarSenha = document.getElementById('confirmarSenha').value;
        
        if (senha.length < 6) {
            document.getElementById('senhaError').textContent = 'Senha deve ter pelo menos 6 caracteres';
            document.getElementById('senhaError').classList.add('active');
            isValid = false;
        } else {
            document.getElementById('senhaError').classList.remove('active');
        }
        
        if (senha !== confirmarSenha) {
            document.getElementById('confirmarSenhaError').textContent = 'As senhas não coincidem';
            document.getElementById('confirmarSenhaError').classList.add('active');
            isValid = false;
        } else {
            document.getElementById('confirmarSenhaError').classList.remove('active');
        }
    }
    
    if (!isValid) {
        event.preventDefault();
    }
};

            // Lista de campos que não podem conter números
            const camposSemNumeros = [
                'nome', 'cidade', 'bairro', 'estado',
                'tipoResidencia', 'bandeira',
                'nomeCartao', 'enderecoEntrega', 'descricaoEndereco'
            ];

            camposSemNumeros.forEach(id => {
                const input = document.getElementById(id);
                if (input) {
                    input.addEventListener('input', function() {
                        this.value = this.value.replace(/\d/g, ''); // Remove dígitos
                    });
                }
            });
        });

        // Animação para os campos com máscara quando completos
        function animateField(field) {
            field.style.transform = 'scale(1.02)';
            field.style.boxShadow = '0 0 0 2px rgba(0, 179, 60, 0.3)';
            setTimeout(() => {
                field.style.transform = '';
                field.style.boxShadow = '';
            }, 300);
        }

        // Máscara para Validade do Cartão (MM/AAAA)
        const validadeInput = document.getElementById('validade');
        validadeInput.addEventListener('input', function() {
            let value = this.value.replace(/\D/g, '');
            if (value.length > 2) {
                value = value.substring(0, 2) + '/' + value.substring(2);
            }
            this.value = value.substring(0, 7);
        });
