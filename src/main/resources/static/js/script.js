    function confirmarExclusao(elemento) {
    const clienteId = elemento.getAttribute("data-id");
    const clienteNome = elemento.closest('tr').querySelector('td:nth-child(2)').textContent;
    
    const modal = document.getElementById("modalConfirmacao");
    const clienteNomeSpan = document.getElementById("clienteNome");
    const clienteIdSpan = document.getElementById("clienteId");
    const confirmarBtn = document.getElementById("btnConfirmarExclusao");
    
    clienteNomeSpan.textContent = clienteNome;
    clienteIdSpan.textContent = clienteId;
    
    confirmarBtn.onclick = function() {
        window.location.href = `/clientes/excluir/${clienteId}`;
    };
    
    modal.style.display = "flex";
}

function cancelarExclusao() {
    document.getElementById("modalConfirmacao").style.display = "none";
}

window.addEventListener('click', function(event) {
    const modalConfirmacao = document.getElementById("modalConfirmacao");
    const modalCliente = document.getElementById("modalCliente");
    
    if (event.target === modalConfirmacao) {
        cancelarExclusao();
    }
    if (event.target === modalCliente) {
        fecharModalCliente();
    }
});

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        cancelarExclusao();
        fecharModalCliente();
    }
});

    window.onclick = function(event) {
        const modal = document.getElementById("modalConfirmacao");
        if (event.target === modal) {
            cancelarExclusao();
        }
    }

function abrirModalCliente(elemento) {
    const clienteId = elemento.getAttribute("data-id");
    fetch(`/clientes/detalhes/${clienteId}`)
        .then(response => response.json())
        .then(cliente => {
            const conteudo = document.getElementById("conteudoCliente");
            
            const enderecoCompleto = cliente.endereco ? 
                `${cliente.endereco.tipoResidencia} ${cliente.endereco.logradouro}, ${cliente.endereco.numero}, ${cliente.endereco.bairro}, ${cliente.endereco.cidade} - ${cliente.endereco.estado}, ${cliente.endereco.cep}` : 
                'Não informado';
            
            conteudo.innerHTML = `
                <div class="info-section">
                    <div class="section-title"><i class="fas fa-user"></i> Informações Pessoais</div>
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-label">Nome completo</div>
                            <div class="info-value">${cliente.nome}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">E-mail</div>
                            <div class="info-value">${cliente.email}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Telefone</div>
                            <div class="info-value">${cliente.telefone}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">CPF</div>
                            <div class="info-value">${cliente.cpf}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Gênero</div>
                            <div class="info-value">${cliente.genero}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Data de Nascimento</div>
                            <div class="info-value">${cliente.dataNascimento}</div>
                        </div>
                    </div>
                </div>
                
                <div class="info-section">
                    <div class="section-title"><i class="fas fa-map-marker-alt"></i> Endereço</div>
                    <div class="info-item">
                        <div class="info-value">${enderecoCompleto}</div>
                    </div>
                </div>
                
                <div class="info-section">
                    <div class="section-title"><i class="fas fa-credit-card"></i> Informações de Pagamento</div>
                    ${cliente.cartao ? `
                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-label">Bandeira do Cartão</div>
                                <div class="info-value">${cliente.cartao.bandeira}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Número do Cartão</div>
                                <div class="info-value">${cliente.cartao.numeroCartao}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Nome no Cartão</div>
                                <div class="info-value">${cliente.cartao.nomeCartao}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Validade</div>
                                <div class="info-value">${cliente.cartao.dataValidade || 'Não informado'}</div>
                            </div>
                        </div>
                    ` : '<div class="info-value">Não informado</div>'}
                </div>
            `;

            document.getElementById("modalCliente").style.display = "flex";
        });
}

function fecharModalCliente() {
    document.getElementById("modalCliente").style.display = "none";
}

window.onclick = function(event) {
    const modal = document.getElementById("modalCliente");
    if (event.target === modal) {
        fecharModalCliente();
    }
};

// Funções para notificações toast (já adicionadas anteriormente)
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