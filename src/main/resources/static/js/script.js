document.addEventListener('DOMContentLoaded', function() {

  
    const selectTamanho = document.getElementById('tamanhoPagina');
    if (selectTamanho) {
        const currentSize = selectTamanho.getAttribute('data-current');
        if (currentSize) {
            selectTamanho.value = currentSize;
        }

        selectTamanho.addEventListener('change', function() {
            const url = new URL(window.location.href);
            url.searchParams.set('tamanho', this.value);
            url.searchParams.set('pagina', '0');
            window.location.href = url.toString();
        });
    }

  
    const modalConfirmacao = document.getElementById('modalConfirmacao');
    const modalDetalhes = document.getElementById('modalDetalhes');

    setupModalConfirmacao();
    setupModalDetalhes();


    function setupModalConfirmacao() {
        const btnCancelar = document.getElementById('cancelarExclusao');
        const btnConfirmar = document.getElementById('confirmarExclusao');
        const spanNome = document.getElementById('clienteNomeModal');
        const spanId = document.getElementById('clienteId');

        if (!modalConfirmacao || !btnCancelar || !btnConfirmar) return;

        let clienteIdAtual = null;

        // Abrir modal
        document.querySelectorAll('.btn-action.btn-delete').forEach(btn => {
            btn.addEventListener('click', function() {
                clienteIdAtual = this.getAttribute('data-id');
                const clienteNome = this.getAttribute('data-nome');

                spanNome.textContent = clienteNome;
                spanId.textContent = clienteIdAtual;

                modalConfirmacao.style.display = 'flex';
            });
        });

        btnConfirmar.addEventListener('click', () => {
            if (clienteIdAtual) {
                window.location.href = `/clientes/excluir/${clienteIdAtual}`;
            }
        });

        btnCancelar.addEventListener('click', () => {
            fecharModalConfirmacao();
        });

        modalConfirmacao.addEventListener('click', (e) => {
            if (e.target === modalConfirmacao) {
                fecharModalConfirmacao();
            }
        });

        function fecharModalConfirmacao() {
            modalConfirmacao.style.display = 'none';
            clienteIdAtual = null;
        }
    }

    function setupModalDetalhes() {
        document.querySelectorAll('.btn-action.btn-view').forEach(btn => {
            btn.addEventListener('click', function() {
                const clienteId = this.getAttribute('data-id');

                fetch(`/clientes/detalhes/${clienteId}`)
                    .then(response => {
                        if (!response.ok) throw new Error();
                        return response.json();
                    })
                    .then(cliente => {

                        const enderecoCompleto = cliente.endereco
                            ? `${cliente.endereco.tipoResidencia}, ${cliente.endereco.numero}, ${cliente.endereco.bairro}, ${cliente.endereco.cidade} - ${cliente.endereco.estado}, ${cliente.endereco.cep}`
                            : 'Não informado';

                        document.getElementById('detalhesCliente').innerHTML = `
                            <div class="info-section">
                                <div class="section-title">
                                    <i class="fas fa-user"></i> Informações Pessoais
                                </div>
                                <div class="info-grid">
                                    <div class="info-item"><div class="info-label">Nome completo</div><div class="info-value">${cliente.nome}</div></div>
                                    <div class="info-item"><div class="info-label">E-mail</div><div class="info-value">${cliente.email}</div></div>
                                    <div class="info-item"><div class="info-label">Telefone</div><div class="info-value">${cliente.telefone}</div></div>
                                    <div class="info-item"><div class="info-label">CPF</div><div class="info-value">${cliente.cpf}</div></div>
                                    <div class="info-item"><div class="info-label">Gênero</div><div class="info-value">${cliente.genero}</div></div>
                                    <div class="info-item"><div class="info-label">Data de Nascimento</div><div class="info-value">${cliente.dataNascimento}</div></div>
                                </div>
                            </div>

                            <div class="info-section">
                                <div class="section-title">
                                    <i class="fas fa-map-marker-alt"></i> Endereço
                                </div>
                                <div class="info-value">${enderecoCompleto}</div>
                            </div>

                            <div class="info-section">
                                <div class="section-title">
                                    <i class="fas fa-credit-card"></i> Informações de Pagamento
                                </div>
                                ${
                                    cliente.cartao
                                        ? `
                                            <div class="info-grid">
                                                <div class="info-item"><div class="info-label">Bandeira</div><div class="info-value">${cliente.cartao.bandeira}</div></div>
                                                <div class="info-item"><div class="info-label">Número</div><div class="info-value">${cliente.cartao.numeroCartao}</div></div>
                                                <div class="info-item"><div class="info-label">Nome no Cartão</div><div class="info-value">${cliente.cartao.nomeCartao}</div></div>
                                                <div class="info-item"><div class="info-label">Validade</div><div class="info-value">${cliente.cartao.validade || 'Não informado'}</div></div>
                                            </div>
                                        `
                                        : `<div class="info-value">Não informado</div>`
                                }
                            </div>
                        `;

                        modalDetalhes.style.display = 'flex';
                    })
                    .catch(() => alert('Erro ao carregar detalhes do cliente'));
            });
        });

        document.getElementById('fecharDetalhes').addEventListener('click', () => {
            modalDetalhes.style.display = 'none';
        });

        modalDetalhes.addEventListener('click', (e) => {
            if (e.target === modalDetalhes) {
                modalDetalhes.style.display = 'none';
            }
        });
    }
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            if (modalConfirmacao.style.display === 'flex') {
                modalConfirmacao.style.display = 'none';
            }
            if (modalDetalhes.style.display === 'flex') {
                modalDetalhes.style.display = 'none';
            }
        }
    });

});