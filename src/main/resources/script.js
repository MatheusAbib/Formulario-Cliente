
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

        // Máscara melhorada para Telefone ((00) 00000-0000 ou (00) 0000-0000)
        const telefoneInput = document.getElementById('telefone');
        telefoneInput.addEventListener('input', function() {
            let value = this.value.replace(/\D/g, '');
            
            if (value.length === 0) {
                this.value = '';
                return;
            }
            
            // Formato: (00) 00000-0000 para celular (11 dígitos)
            // Formato: (00) 0000-0000 para fixo (10 dígitos)
            if (value.length <= 2) {
                this.value = '(' + value;
            } else if (value.length <= 6) {
                this.value = '(' + value.substring(0, 2) + ') ' + value.substring(2);
            } else if (value.length <= 10) {
                this.value = '(' + value.substring(0, 2) + ') ' + value.substring(2, 6) + '-' + value.substring(6);
            } else {
                this.value = '(' + value.substring(0, 2) + ') ' + value.substring(2, 7) + '-' + value.substring(7, 11);
            }
            
            // Limita o tamanho máximo
            this.value = this.value.substring(0, 15);
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

        // Máscara para CVV (apenas números, max 3 dígitos)
        const cvvInput = document.getElementById('codigoSeguranca');
        cvvInput.addEventListener('input', function() {
            this.value = this.value.replace(/\D/g, '').substring(0, 3);
        });

        // Máscara melhorada para Número do Cartão (0000 0000 0000 0000)
        const cartaoInput = document.getElementById('numeroCartao');
        cartaoInput.addEventListener('input', function() {
            let value = this.value.replace(/\D/g, '');
            
            // Adiciona espaços a cada 4 dígitos
            value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
            
            // Limita o tamanho máximo (19 caracteres incluindo espaços)
            this.value = value.substring(0, 19);
            
            // Validação em tempo real
            const digits = value.replace(/\D/g, '');
            if (digits.length > 0 && (digits.length < 13 || digits.length > 19)) {
                document.getElementById('numeroCartaoError').textContent = 'Número inválido (13-19 dígitos)';
                document.getElementById('numeroCartaoError').classList.add('active');
            } else {
                document.getElementById('numeroCartaoError').classList.remove('active');
            }
        });

        // Máscara para Validade do Cartão (MM/AAAA)
        const validadeInput = document.getElementById('validade');
        validadeInput.addEventListener('input', function() {
            let value = this.value.replace(/\D/g, '');
            if (value.length > 2) {
                value = value.substring(0, 2) + '/' + value.substring(2);
            }
            this.value = value.substring(0, 7);
        });

        // Validação em tempo real para validade
        validadeInput.addEventListener('blur', function() {
            const value = this.value;
            const validadeRegex = /^(0[1-9]|1[0-2])\/\d{4}$/;
            
            if (value && !validadeRegex.test(value)) {
                document.getElementById('validadeError').textContent = 'Formato inválido (use MM/AAAA)';
                document.getElementById('validadeError').classList.add('active');
            } else if (value) {
                const [mes, ano] = value.split('/');
                const dataAtual = new Date();
                const dataValidade = new Date(ano, mes - 1);
                
                if (dataValidade < dataAtual) {
                    document.getElementById('validadeError').textContent = 'Cartão expirado';
                    document.getElementById('validadeError').classList.add('active');
                } else {
                    document.getElementById('validadeError').classList.remove('active');
                }
            }
        });

        // Formatação automática para Logradouro (capitalize)
        const logradouroInput = document.getElementById('logradouro');
        logradouroInput.addEventListener('blur', function() {
            this.value = this.value.toLowerCase().replace(/(?:^|\s)\S/g, function(a) {
                return a.toUpperCase();
            });
        });

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

        // Validação de formulário
        document.getElementById('formCadastro').onsubmit = function(event) {
            let isValid = true;
            
            // Validação de CPF (formato)
            if (cpfInput.value.replace(/\D/g, '').length !== 11) {
                document.getElementById('cpfError').textContent = 'CPF deve ter 11 dígitos';
                document.getElementById('cpfError').classList.add('active');
                isValid = false;
            } else {
                document.getElementById('cpfError').classList.remove('active');
            }
            
            // Validação de CEP (formato)
            if (cepInput.value.replace(/\D/g, '').length !== 8) {
                document.getElementById('cepError').textContent = 'CEP deve ter 8 dígitos';
                document.getElementById('cepError').classList.add('active');
                isValid = false;
            } else {
                document.getElementById('cepError').classList.remove('active');
            }
            
            // Validação de telefone (formato)
            const telefoneDigits = telefoneInput.value.replace(/\D/g, '');
            if (telefoneDigits.length < 10 || telefoneDigits.length > 11) {
                document.getElementById('telefoneError').textContent = 'Telefone inválido (deve ter 10 ou 11 dígitos)';
                document.getElementById('telefoneError').classList.add('active');
                isValid = false;
            } else {
                document.getElementById('telefoneError').classList.remove('active');
            }
            
            // Validação de validade (MM/AAAA)
            const validade = validadeInput.value;
            const validadeRegex = /^(0[1-9]|1[0-2])\/\d{4}$/;
            if (!validadeRegex.test(validade)) {
                document.getElementById('validadeError').textContent = 'Validade inválida (formato: MM/AAAA)';
                document.getElementById('validadeError').classList.add('active');
                isValid = false;
            } else {
                document.getElementById('validadeError').classList.remove('active');
            }
            
            // Validação de número do cartão (apenas formato)
            const cartaoDigits = cartaoInput.value.replace(/\D/g, '');
            if (cartaoDigits.length < 13 || cartaoDigits.length > 19) {
                document.getElementById('numeroCartaoError').textContent = 'Número do cartão inválido (13 a 19 dígitos)';
                document.getElementById('numeroCartaoError').classList.add('active');
                isValid = false;
            } else {
                document.getElementById('numeroCartaoError').classList.remove('active');
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
                
                // MENSAGEM DE ERRO DE SENHAS NÃO COINCIDIREM
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
                // Rola para o primeiro erro
                const firstError = document.querySelector('.error.active');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
        };
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
