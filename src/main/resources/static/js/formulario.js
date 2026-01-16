    function mostrarLoaderCEP() {
    const cepInput = document.getElementById('cep');
    if (!cepInput) return;
    
    esconderLoaderCEP();
    
    atualizarEstadoCEP(true);
    
    const loader = document.createElement('div');
    loader.id = 'cep-loader';
    loader.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Buscando endereço...';
    loader.style.cssText = `
        position: absolute;
        bottom: 100%;
        left: 0;
        right: 0;
        background: linear-gradient(135deg, var(--correios-light-blue), #ffffff);
        padding: 10px 15px;
        border-radius: var(--border-radius);
        font-size: 0.85rem;
        color: var(--correios-blue);
        margin-bottom: 8px;
        z-index: 10;
        display: flex;
        align-items: center;
        gap: 10px;
        border: 2px solid var(--correios-blue);
        box-shadow: 0 4px 12px rgba(0, 51, 160, 0.15);
        font-weight: 500;
    `;
    
    const parent = cepInput.parentNode;
    parent.style.position = 'relative';
    parent.appendChild(loader);
}

function esconderLoaderCEP() {
    const loader = document.getElementById('cep-loader');
    if (loader) {
        loader.remove();
    }
    
    atualizarEstadoCEP(false);
}

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

let enderecoIndex = 0;

function adicionarEndereco() {
    const container = document.getElementById('enderecos-container');
    const template = document.getElementById('endereco-template');
    
    if (!container || !template) return;
    
    const enderecoItems = container.querySelectorAll('.endereco-item');
    enderecoIndex = enderecoItems.length;
    
    const clone = template.content.cloneNode(true);
    const enderecoItem = clone.querySelector('.endereco-item');
    
    const inputs = enderecoItem.querySelectorAll('input, select');
    inputs.forEach(input => {
        const name = input.getAttribute('name');
        if (name) {
            input.setAttribute('name', name.replace('[0]', `[${enderecoIndex}]`));
        }
        
        if (input.classList.contains('cep')) {
            const cepId = `cep_${enderecoIndex}`;
            input.setAttribute('id', cepId);
            
            input.addEventListener('input', formatarCEP);
            input.addEventListener('blur', function() {
                const cepLimpo = this.value.replace(/\D/g, '');
                if (cepLimpo.length === 8 && !window.buscandoCEP) {
                    buscarCEPdinamico(this);
                }
            });
        }
    });
    
    const numeroSpan = enderecoItem.querySelector('.endereco-numero');
    if (numeroSpan) {
        numeroSpan.textContent = enderecoIndex + 1;
    }
    
    container.appendChild(enderecoItem);
    
    enderecoItem.style.opacity = '0';
    enderecoItem.style.transform = 'translateY(20px)';
    
    setTimeout(() => {
        enderecoItem.style.transition = 'all 0.3s ease';
        enderecoItem.style.opacity = '1';
        enderecoItem.style.transform = 'translateY(0)';
    }, 10);
    
    setTimeout(() => {
        enderecoItem.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 300);
    
    showToast('success', 'Endereço adicionado', 'Novo endereço foi adicionado ao formulário.');
}


let enderecoParaExcluir = null;

function configurarModalConfirmacao() {
    const modal = document.getElementById('modalConfirmacao');
    const btnCancelar = document.getElementById('cancelarExclusaoEndereco');
    const btnConfirmar = document.getElementById('confirmarExclusaoEndereco');
    
    if (!modal || !btnCancelar || !btnConfirmar) return;
    
    btnCancelar.addEventListener('click', function() {
        modal.style.display = 'none';
        enderecoParaExcluir = null;
    });
    
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            modal.style.display = 'none';
            enderecoParaExcluir = null;
        }
    });
    
    btnConfirmar.addEventListener('click', function() {
        if (enderecoParaExcluir) {
            removerEndereco(enderecoParaExcluir);
            modal.style.display = 'none';
            enderecoParaExcluir = null;
        }
    });
    
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && modal.style.display === 'flex') {
            modal.style.display = 'none';
            enderecoParaExcluir = null;
        }
    });
}

function mostrarModalExclusaoEndereco(botaoRemover) {
    const modal = document.getElementById('modalConfirmacao');
    if (!modal) return;
    
    const enderecoItem = botaoRemover.closest('.endereco-item');
    if (!enderecoItem) return;
    
    const enderecoEntrega = enderecoItem.querySelector('.enderecoEntrega')?.value || 'Não especificado';
    const numero = enderecoItem.querySelector('.numero')?.value || 'Não especificado';
    const bairro = enderecoItem.querySelector('.bairro')?.value || 'Não especificado';
    const cidade = enderecoItem.querySelector('.cidade')?.value || 'Não especificado';
    
    const enderecoInfo = `${enderecoEntrega}, ${bairro}, ${cidade}`;
    
    document.getElementById('enderecoInfoModal').textContent = enderecoInfo;
    document.getElementById('enderecoNumeroModal').textContent = numero;
    
    enderecoParaExcluir = botaoRemover;
    
    modal.style.display = 'flex';
}

function removerEndereco(botaoRemover) {
    const enderecoItem = botaoRemover.closest('.endereco-item');
    const enderecoItems = document.querySelectorAll('.endereco-item');
    
    if (!enderecoItem) return;
    
    if (enderecoItems.length <= 1) {
        showToast('warning', 'Atenção', 'É necessário pelo menos um endereço.');
        return;
    }
    
    enderecoItem.style.transition = 'all 0.3s ease';
    enderecoItem.style.opacity = '0';
    enderecoItem.style.transform = 'translateY(-20px)';
    enderecoItem.style.maxHeight = '0';
    enderecoItem.style.overflow = 'hidden';
    enderecoItem.style.marginBottom = '0';
    enderecoItem.style.paddingBottom = '0';
    enderecoItem.style.paddingTop = '0';
    
    setTimeout(() => {
        enderecoItem.remove();
        
        reindexarEnderecos();
        
        showToast('success', 'Endereço removido', 'O endereço foi removido com sucesso.');
    }, 300);
}


function reindexarEnderecos() {
    const container = document.getElementById('enderecos-container');
    const enderecoItems = container.querySelectorAll('.endereco-item');
    
    enderecoItems.forEach((item, index) => {
        const numeroSpan = item.querySelector('.endereco-numero');
        if (numeroSpan) {
            numeroSpan.textContent = index + 1;
        }
        
        const inputs = item.querySelectorAll('input, select');
        inputs.forEach(input => {
            const oldName = input.getAttribute('name');
            if (oldName) {
                const matches = oldName.match(/enderecos\[(\d+)\]\.(.+)/);
                if (matches) {
                    const newName = `enderecos[${index}].${matches[2]}`;
                    input.setAttribute('name', newName);
                    
                    if (input.classList.contains('cep')) {
                        const newId = `cep_${index}`;
                        input.setAttribute('id', newId);
                    }
                }
            }
        });
    });
    
    enderecoIndex = enderecoItems.length;
}

function buscarCEPdinamico(cepInput) {
    const cepLimpo = cepInput.value.replace(/\D/g, '');
    
    if (cepLimpo.length !== 8) {
        return;
    }
    
    if (window.buscandoCEP) return;
    window.buscandoCEP = true;
    
    const parent = cepInput.parentNode;
    parent.style.position = 'relative';
    
    const loader = document.createElement('div');
    loader.className = 'cep-loader-dinamico';
    loader.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Buscando endereço...';
    loader.style.cssText = `
        position: absolute;
        bottom: 100%;
        left: 0;
        right: 0;
        background: linear-gradient(135deg, var(--correios-light-blue), #ffffff);
        padding: 10px 15px;
        border-radius: var(--border-radius);
        font-size: 0.85rem;
        color: var(--correios-blue);
        margin-bottom: 8px;
        z-index: 10;
        display: flex;
        align-items: center;
        gap: 10px;
        border: 2px solid var(--correios-blue);
        box-shadow: 0 4px 12px rgba(0, 51, 160, 0.15);
        font-weight: 500;
    `;
    
    parent.appendChild(loader);
    
    cepInput.style.borderColor = 'var(--correios-yellow)';
    cepInput.style.backgroundColor = '#fffdf0';
    
    const url = `https://viacep.com.br/ws/${cepLimpo}/json/`;
    
    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error('Erro na requisição');
            return response.json();
        })
        .then(data => {
            const loader = parent.querySelector('.cep-loader-dinamico');
            if (loader) loader.remove();
            
            cepInput.style.borderColor = '';
            cepInput.style.backgroundColor = '';
            
            if (data.erro) {
                showToast('error', 'CEP não encontrado', 'O CEP informado não foi encontrado.');
                window.buscandoCEP = false;
                return;
            }
            
            preencherEnderecoDinamico(cepInput, data);
            window.buscandoCEP = false;
        })
        .catch(error => {
            console.error('Erro ao buscar CEP:', error);
            
            const loader = parent.querySelector('.cep-loader-dinamico');
            if (loader) loader.remove();
            
            cepInput.style.borderColor = '';
            cepInput.style.backgroundColor = '';
            
            showToast('error', 'Erro ao buscar CEP', 'Não foi possível buscar o endereço. Tente novamente.');
            window.buscandoCEP = false;
        });
}

function preencherEnderecoDinamico(cepInput, data) {
    const enderecoItem = cepInput.closest('.endereco-item');
    
    if (!enderecoItem) return;
    
    const logradouroInput = enderecoItem.querySelector('.enderecoEntrega');
    const bairroInput = enderecoItem.querySelector('.bairro');
    const cidadeInput = enderecoItem.querySelector('.cidade');
    const estadoSelect = enderecoItem.querySelector('.estado');
    
    let camposPreenchidos = 0;
    
    if (logradouroInput) {
        logradouroInput.value = data.logradouro || '';
        if (data.logradouro) camposPreenchidos++;
    }
    
    if (bairroInput) {
        bairroInput.value = data.bairro || '';
        if (data.bairro) camposPreenchidos++;
    }
    
    if (cidadeInput) {
        cidadeInput.value = data.localidade || '';
        if (data.localidade) camposPreenchidos++;
    }
    
    if (estadoSelect) {
        const estadoValue = data.uf || '';
        estadoSelect.value = estadoValue;
        
        const floatingLabel = estadoSelect.parentNode.querySelector('.floating-label');
        if (estadoValue && floatingLabel) {
            floatingLabel.classList.add('filled');
            camposPreenchidos++;
        }
    }
    
    if (camposPreenchidos > 0) {
        showToast('success', 'CEP encontrado!', 'Endereço preenchido automaticamente.');
    }
}

function formatarCEP(e) {
    let value = e.target.value.replace(/\D/g, '');
    
    if (value.length > 8) value = value.substring(0, 8);
    
    if (value.length > 5) {
        value = value.replace(/(\d{5})(\d{1,3})/, '$1-$2');
    }
    
    e.target.value = value;
}

document.addEventListener('DOMContentLoaded', function() {
    const enderecoItems = document.querySelectorAll('.endereco-item');
    enderecoIndex = enderecoItems.length;
    
    document.querySelectorAll('.cep').forEach(cepInput => {
        cepInput.addEventListener('input', formatarCEP);
        
        cepInput.addEventListener('blur', function() {
            const cepLimpo = this.value.replace(/\D/g, '');
            if (cepLimpo.length === 8 && !window.buscandoCEP) {
                buscarCEPdinamico(this);
            }
        });
    });
    
    const toastType = document.body.getAttribute('data-toast-type');
    const toastTitle = document.body.getAttribute('data-toast-title');
    const toastMessage = document.body.getAttribute('data-toast-message');
    
    if (toastType && toastTitle && toastMessage) {
        showToast(toastType, toastTitle, toastMessage);
    }
    
    document.body.removeAttribute('data-toast-type');
    document.body.removeAttribute('data-toast-title');
    document.body.removeAttribute('data-toast-message');
    
    const form = document.getElementById('formCadastro');
    if (!form) return;

    const isEditMode = form.getAttribute('action').includes('/editar/');
    
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
        
        const senhaValue = senhaInput.value;
        const confirmarSenhaValue = confirmarSenhaInput.value;
        
        if (!isEditMode) {
            if (senhaValue.length < 6 && senhaValue.length > 0) {
                senhaError.textContent = 'A senha deve ter pelo menos 6 caracteres';
                senhaError.classList.add('active');
            } else {
                senhaError.classList.remove('active');
            }
            
            if (confirmarSenhaValue !== senhaValue && confirmarSenhaValue.length > 0) {
                confirmarSenhaError.textContent = 'As senhas não coincidem';
                confirmarSenhaError.classList.add('active');
            } else {
                confirmarSenhaError.classList.remove('active');
            }
        } else {
            if (senhaValue.length > 0 && senhaValue.length < 6) {
                senhaError.textContent = 'A senha deve ter pelo menos 6 caracteres';
                senhaError.classList.add('active');
            } else {
                senhaError.classList.remove('active');
            }
            
            if (senhaValue.length > 0 && confirmarSenhaValue !== senhaValue) {
                confirmarSenhaError.textContent = 'As senhas não coincidem';
                confirmarSenhaError.classList.add('active');
            } else {
                confirmarSenhaError.classList.remove('active');
            }
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

        const enderecoItems = document.querySelectorAll('.endereco-item');
        if (enderecoItems.length === 0) {
            isValid = false;
            showToast('error', 'Erro!', 'É necessário pelo menos um endereço.');
        }

        if (senhaInput && confirmarSenhaInput) {
            if (!isEditMode) {
                if (senhaInput.value.length < 6) {
                    isValid = false;
                    showToast('error', 'Erro!', 'A senha deve ter pelo menos 6 caracteres.');
                    senhaInput.focus();
                }
                
                if (senhaInput.value !== confirmarSenhaInput.value) {
                    isValid = false;
                    showToast('error', 'Erro!', 'As senhas não coincidem.');
                    confirmarSenhaInput.focus();
                }
            } else {
                if (senhaInput.value.length > 0 || confirmarSenhaInput.value.length > 0) {
                    if (senhaInput.value.length < 6) {
                        isValid = false;
                        showToast('error', 'Erro!', 'A senha deve ter pelo menos 6 caracteres.');
                        senhaInput.focus();
                    }
                    
                    if (senhaInput.value !== confirmarSenhaInput.value) {
                        isValid = false;
                        showToast('error', 'Erro!', 'As senhas não coincidem.');
                        confirmarSenhaInput.focus();
                    }
                }
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
        
        const dataNascimentoInput = document.getElementById('dataNascimento');
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

    const dataNascimentoInput = document.getElementById('dataNascimento');
    
    if (dataNascimentoInput) {
        const hoje = new Date().toISOString().split('T')[0];
        dataNascimentoInput.setAttribute('max', hoje);
        
        dataNascimentoInput.addEventListener('change', function() {
            const value = this.value;
            
            if (!value) return;
            
            const dataNascimento = new Date(value);
            const dataAtual = new Date();
            dataAtual.setHours(0, 0, 0, 0);
            
            if (dataNascimento > dataAtual) {
                showToast('error', 'Data inválida', 'Data de nascimento não pode ser futura.');
                this.value = '';
                this.focus();
            }
        });
    }

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
        
        .password-info {
            font-size: 0.85rem;
            color: #666;
            margin-top: 5px;
            font-style: italic;
        }
        
        .optional-field {
            font-size: 0.8rem;
            color: #999;
            font-weight: normal;
        }
        
        .endereco-item {
            background: #f9f9f9;
            border-radius: var(--border-radius);
            padding: 20px;
            margin-bottom: 20px;
            border: 1px solid #e0e0e0;
            transition: all 0.3s ease;
        }
        
        .endereco-item:hover {
            border-color: var(--correios-blue);
            box-shadow: 0 2px 8px rgba(0, 51, 160, 0.1);
        }
        
        .endereco-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .endereco-title {
            margin: 0;
            font-size: 1.1rem;
            color: var(--correios-blue);
        }
        
        .endereco-numero {
            background: var(--correios-blue);
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.9rem;
            margin-left: 5px;
        }
        
        .btn-add-endereco {
            background: var(--correios-green);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: var(--border-radius);
            cursor: pointer;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }
        
        .btn-add-endereco:hover {
            background: var(--correios-dark-green);
            transform: translateY(-2px);
        }
        
        .btn-remove-endereco {
            background: #e74c3c;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: var(--border-radius);
            cursor: pointer;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 5px;
            transition: all 0.3s ease;
        }
        
        .btn-remove-endereco:hover {
            background: #c0392b;
        }
        
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
    `;
    document.head.appendChild(style);

    if (isEditMode && senhaInput && confirmarSenhaInput) {
        const senhaLabel = document.querySelector('label[for="senha"]');
        const confirmarSenhaLabel = document.querySelector('label[for="confirmarSenha"]');
        
        if (senhaLabel) {
            const optionalSpan = document.createElement('span');
            optionalSpan.className = 'optional-field';
            senhaLabel.appendChild(optionalSpan);
        }
        
        if (confirmarSenhaLabel) {
            const optionalSpan = document.createElement('span');
            optionalSpan.className = 'optional-field';
            confirmarSenhaLabel.appendChild(optionalSpan);
        }
        
        const infoDiv = document.createElement('div');
        infoDiv.className = 'password-info';
        infoDiv.textContent = 'A senha atual será mantida se os campos ficarem em branco.';
        confirmarSenhaInput.parentNode.insertBefore(infoDiv, confirmarSenhaInput.nextSibling);
    }

        configurarModalConfirmacao();
});

function buscarCep(cep) {
    const cepLimpo = cep.replace(/\D/g, '');
    
    if (cepLimpo.length !== 8) {
        return;
    }
    
    if (window.buscandoCEP) return;
    window.buscandoCEP = true;
    
    mostrarLoaderCEP();
    
    const url = `https://viacep.com.br/ws/${cepLimpo}/json/`;
    
    const startTime = Date.now();
    
    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error('Erro na requisição');
            return response.json();
        })
        .then(data => {
            const elapsedTime = Date.now() - startTime;
            const remainingTime = Math.max(0, 1500 - elapsedTime);
            
            setTimeout(() => {
                esconderLoaderCEP();
                
                if (data.erro) {
                    showToast('error', 'CEP não encontrado', 'O CEP informado não foi encontrado.');
                    window.buscandoCEP = false;
                    return;
                }
                
                preencherEnderecoComCEP(data);
                window.buscandoCEP = false;
            }, remainingTime);
        })
        .catch(error => {
            console.error('Erro ao buscar CEP:', error);
            
            const elapsedTime = Date.now() - startTime;
            const remainingTime = Math.max(0, 1500 - elapsedTime);
            
            setTimeout(() => {
                esconderLoaderCEP();
                showToast('error', 'Erro ao buscar CEP', 'Não foi possível buscar o endereço. Tente novamente.');
                window.buscandoCEP = false;
            }, remainingTime);
        });
}

function preencherEnderecoComCEP(data) {
    const enderecoItems = document.querySelectorAll('.endereco-item');
    if (enderecoItems.length > 0) {
        const primeiroCEP = enderecoItems[0].querySelector('.cep');
        if (primeiroCEP) {
            buscarCEPdinamico(primeiroCEP, data);
        }
    }
}

function atualizarEstadoCEP(buscando) {
}