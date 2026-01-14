// Funções para notificações toast (reutilizando do script.js)
function showToast(type, title, message) {
    const container = document.getElementById('toast-container');
    if (!container) return;
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    const icon = type === 'success' ? '✓' : 
                 type === 'error' ? '✗' : 
                 type === 'warning' ? '⚠' : 'ℹ';
    
    toast.innerHTML = `
        <div class="toast-icon">${icon}</div>
        <div class="toast-content">
            <div class="toast-title">${title}</div>
            <div class="toast-message">${message}</div>
        </div>
        <button class="toast-close" onclick="this.parentElement.remove()">&times;</button>
        <div class="toast-progress"></div>
    `;
    
    container.appendChild(toast);
    
    // Remove automaticamente após 5 segundos
    setTimeout(() => {
        if (toast.parentElement) {
            toast.classList.add('fade-out');
            setTimeout(() => {
                if (toast.parentElement) {
                    toast.remove();
                }
            }, 300);
        }
    }, 5000);
}

// Mostra notificações quando a página carrega
document.addEventListener('DOMContentLoaded', function() {
    // Verifica se há flash attributes
    const toastType = document.body.getAttribute('data-toast-type');
    const toastTitle = document.body.getAttribute('data-toast-title');
    const toastMessage = document.body.getAttribute('data-toast-message');
    
    if (toastType && toastTitle && toastMessage) {
        showToast(toastType, toastTitle, toastMessage);
    }
    
    // Remove os atributos após mostrar
    document.body.removeAttribute('data-toast-type');
    document.body.removeAttribute('data-toast-title');
    document.body.removeAttribute('data-toast-message');
});

// Validação em tempo real do formulário
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formCadastro');
    if (!form) return;

    // Validação de CPF
    const cpfInput = document.getElementById('cpf');
    if (cpfInput) {
        cpfInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            
            if (value.length > 11) value = value.substring(0, 11);
            
            if (value.length > 9) {
                value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
            } else if (value.length > 6) {
                value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
            } else if (value.length > 3) {
                value = value.replace(/(\d{3})(\d{1,3})/, '$1.$2');
            }
            
            e.target.value = value;
        });
    }

    // Validação de telefone
    const telefoneInput = document.getElementById('telefone');
    if (telefoneInput) {
        telefoneInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            
            if (value.length > 11) value = value.substring(0, 11);
            
            if (value.length > 10) {
                value = value.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
            } else if (value.length > 6) {
                value = value.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
            } else if (value.length > 2) {
                value = value.replace(/(\d{2})(\d{0,5})/, '($1) $2');
            } else if (value.length > 0) {
                value = value.replace(/(\d{0,2})/, '($1');
            }
            
            e.target.value = value;
        });
    }

    // Validação de CEP
    const cepInput = document.getElementById('cep');
    if (cepInput) {
        cepInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            
            if (value.length > 8) value = value.substring(0, 8);
            
            if (value.length > 5) {
                value = value.replace(/(\d{5})(\d{1,3})/, '$1-$2');
            }
            
            e.target.value = value;
        });
    }

    // Validação de número do cartão
    const numeroCartaoInput = document.getElementById('numeroCartao');
    if (numeroCartaoInput) {
        numeroCartaoInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            
            if (value.length > 16) value = value.substring(0, 16);
            
            if (value.length > 12) {
                value = value.replace(/(\d{4})(\d{4})(\d{4})(\d{1,4})/, '$1 $2 $3 $4');
            } else if (value.length > 8) {
                value = value.replace(/(\d{4})(\d{4})(\d{1,4})/, '$1 $2 $3');
            } else if (value.length > 4) {
                value = value.replace(/(\d{4})(\d{1,4})/, '$1 $2');
            }
            
            e.target.value = value;
        });
    }

    // Validação de validade do cartão
    // Validação de validade do cartão
    const validadeInput = document.getElementById('validade');
    const validadeError = document.getElementById('validadeError');
    
    if (validadeInput) {
        validadeInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            
            if (value.length > 6) value = value.substring(0, 6);
            
            if (value.length > 2) {
                value = value.replace(/(\d{2})(\d{1,4})/, '$1/$2');
            }
            
            e.target.value = value;
            
            validateValidade();
        });
        
        validadeInput.addEventListener('blur', validateValidade);
    }

    function validateValidade() {
        if (!validadeInput || !validadeError) return;
        
        const value = validadeInput.value.trim();
        
        if (!value) {
            validadeError.textContent = '';
            validadeError.classList.remove('active');
            return;
        }
        
        const regex = /^(0[1-9]|1[0-2])\/(2[0-9]{3}|3[0-9]{3})$/;
        
        if (!regex.test(value)) {
            validadeError.textContent = 'Formato inválido. Use MM/AAAA';
            validadeError.classList.add('active');
            return;
        }
        
        const partes = value.split('/');
        const mes = parseInt(partes[0], 10);
        const ano = parseInt(partes[1], 10);
        
        const dataAtual = new Date();
        const anoAtual = dataAtual.getFullYear();
        const mesAtual = dataAtual.getMonth() + 1;
        
        if (ano < anoAtual || (ano === anoAtual && mes < mesAtual)) {
            validadeError.textContent = 'Cartão vencido. Data já passou';
            validadeError.classList.add('active');
            return;
        }
        
        if (ano > anoAtual + 20) {
            validadeError.textContent = 'Ano muito distante no futuro';
            validadeError.classList.add('active');
            return;
        }
        
        validadeError.textContent = '';
        validadeError.classList.remove('active');
    }

    // Validação de senha
    const senhaInput = document.getElementById('senha');
    const confirmarSenhaInput = document.getElementById('confirmarSenha');
    
    if (senhaInput && confirmarSenhaInput) {
        confirmarSenhaInput.addEventListener('input', function() {
            validatePassword();
        });
        
        senhaInput.addEventListener('input', function() {
            validatePassword();
        });
    }

    function validatePassword() {
        const senhaError = document.getElementById('senhaError');
        const confirmarSenhaError = document.getElementById('confirmarSenhaError');
        
        if (senhaInput.value.length < 6 && senhaInput.value.length > 0) {
            senhaError.textContent = 'A senha deve ter pelo menos 6 caracteres';
            senhaError.classList.add('active');
        } else {
            senhaError.classList.remove('active');
        }
        
        if (confirmarSenhaInput.value !== senhaInput.value && confirmarSenhaInput.value.length > 0) {
            confirmarSenhaError.textContent = 'As senhas não coincidem';
            confirmarSenhaError.classList.add('active');
        } else {
            confirmarSenhaError.classList.remove('active');
        }
    }

       form.addEventListener('submit', function(e) {
        let isValid = true;
        
        const requiredFields = form.querySelectorAll('[required]');
        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                isValid = false;
                field.style.borderColor = '#e74c3c';
                field.classList.add('shake');
                setTimeout(() => {
                    field.classList.remove('shake');
                }, 500);
            } else {
                field.style.borderColor = '';
            }
        });

        if (senhaInput && confirmarSenhaInput) {
            if (senhaInput.value !== confirmarSenhaInput.value) {
                isValid = false;
                showToast('error', 'Erro!', 'As senhas não coincidem.');
                confirmarSenhaInput.focus();
            }
            
            if (senhaInput.value.length < 6) {
                isValid = false;
                showToast('error', 'Erro!', 'A senha deve ter pelo menos 6 caracteres.');
                senhaInput.focus();
            }
        }
        
        if (validadeInput && validadeInput.value.trim()) {
            const value = validadeInput.value.trim();
            const regex = /^(0[1-9]|1[0-2])\/(2[0-9]{3}|3[0-9]{3})$/;
            
            if (!regex.test(value)) {
                isValid = false;
                showToast('error', 'Erro!', 'Formato de validade inválido. Use MM/AAAA.');
                validadeInput.focus();
            } else {
                const partes = value.split('/');
                const mes = parseInt(partes[0], 10);
                const ano = parseInt(partes[1], 10);
                
                const dataAtual = new Date();
                const anoAtual = dataAtual.getFullYear();
                const mesAtual = dataAtual.getMonth() + 1;
                
                if (ano < anoAtual || (ano === anoAtual && mes < mesAtual)) {
                    isValid = false;
                    showToast('error', 'Erro!', 'Cartão vencido. Data já passou.');
                    validadeInput.focus();
                }
                
                if (ano > anoAtual + 20) {
                    isValid = false;
                    showToast('error', 'Erro!', 'Ano muito distante no futuro.');
                    validadeInput.focus();
                }
            }
        }
        
        if (dataNascimentoInput && dataNascimentoInput.value) {
            const dataNascimento = new Date(dataNascimentoInput.value);
            const dataAtual = new Date();
            dataAtual.setHours(0, 0, 0, 0);
            
            if (dataNascimento > dataAtual) {
                isValid = false;
                showToast('error', 'Erro!', 'Data de nascimento não pode ser futura.');
                dataNascimentoInput.focus();
            }
        }

        if (!isValid) {
            e.preventDefault();
            showToast('error', 'Formulário inválido', 'Por favor, corrija os erros acima.');
            
            const firstError = form.querySelector('[required]:invalid');
            if (firstError) {
                firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }
    });

    form.querySelectorAll('[required]').forEach(field => {
        field.addEventListener('input', function() {
            if (this.value.trim()) {
                this.style.borderColor = '';
            }
        });
    });
});

const style = document.createElement('style');
style.textContent = `
    .shake {
        animation: shake 0.5s ease-in-out;
    }
    
    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        25% { transform: translateX(-5px); }
        75% { transform: translateX(5px); }
    }
`;
document.head.appendChild(style);


    const dataNascimentoInput = document.getElementById('dataNascimento');
    
    if (dataNascimentoInput) {
        dataNascimentoInput.addEventListener('change', validateDataNascimento);
        dataNascimentoInput.addEventListener('blur', validateDataNascimento);
        
        const hoje = new Date().toISOString().split('T')[0];
        dataNascimentoInput.setAttribute('max', hoje);
    }

    function validateDataNascimento() {
        if (!dataNascimentoInput) return;
        
        const value = dataNascimentoInput.value;
        
        if (!value) return;
        
        const dataNascimento = new Date(value);
        const dataAtual = new Date();
        dataAtual.setHours(0, 0, 0, 0);
        
        if (dataNascimento > dataAtual) {
            showToast('error', 'Data inválida', 'Data de nascimento não pode ser futura.');
            dataNascimentoInput.value = '';
            dataNascimentoInput.focus();
        }
    }