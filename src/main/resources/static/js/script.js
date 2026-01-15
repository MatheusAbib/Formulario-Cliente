document.addEventListener('DOMContentLoaded', function() {
    const tableLoader = document.getElementById('tableLoader');
    const modalConfirmacao = document.getElementById('modalConfirmacao');
    const modalDetalhes = document.getElementById('modalDetalhes');
    const filtroForm = document.getElementById('filtroForm');

    let loaderStartTime = 0;

    function configurarSelectTamanho() {
        const selectTamanho = document.getElementById('tamanhoPagina');
        if (selectTamanho) {
            const currentSize = selectTamanho.getAttribute('data-current');
            if (currentSize) {
                selectTamanho.value = currentSize;
            }

            selectTamanho.addEventListener('change', function() {
                const url = new URL(window.location.href);
                const params = {};
                
                url.searchParams.forEach((value, key) => {
                    params[key] = value;
                });
                
                params.tamanho = this.value;
                params.pagina = 0;
                
                carregarClientes(params);
            });
        }
    }

    if (filtroForm) {
        filtroForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(filtroForm);
            const params = {};
            
            for (const [key, value] of formData.entries()) {
                if (value) params[key] = value;
            }
            
            aplicarMascarasENumeros(params);
            
            params.pagina = 0;
            
            carregarClientes(params);
        });
        
        const btnLimpar = filtroForm.querySelector('.btn-clear');
        if (btnLimpar) {
            btnLimpar.addEventListener('click', function(e) {
                e.preventDefault();
                
                filtroForm.reset();
                
                const selectTamanho = document.getElementById('tamanhoPagina');
                if (selectTamanho) {
                    selectTamanho.value = '10';
                }
                
                carregarClientes({ pagina: 0, tamanho: 10 });
            });
        }
    }

    function carregarClientes(params = {}) {
        loaderStartTime = Date.now();
        
        if (tableLoader) {
            tableLoader.style.display = 'flex';
        }
        
        const queryString = new URLSearchParams(params).toString();
        const url = `/clientes?${queryString}`;
        
        fetch(url)
            .then(response => response.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');
                
                const novaTabela = doc.querySelector('.table-scroll-container table');
                const novoPagination = doc.querySelector('.pagination-with-size');
                const novoFiltersInfo = doc.querySelector('.filters-info');
                
                if (novaTabela) {
                    const tabelaAtual = document.querySelector('.table-scroll-container table');
                    tabelaAtual.parentNode.replaceChild(novaTabela, tabelaAtual);
                }
                
                if (novoPagination) {
                    const paginationAtual = document.querySelector('.pagination-with-size');
                    if (paginationAtual && novoPagination) {
                        paginationAtual.parentNode.replaceChild(novoPagination, paginationAtual);
                    }
                }
                
                if (novoFiltersInfo) {
                    const filtersInfoAtual = document.querySelector('.filters-info');
                    if (filtersInfoAtual && novoFiltersInfo) {
                        filtersInfoAtual.parentNode.replaceChild(novoFiltersInfo, filtersInfoAtual);
                    } else if (novoFiltersInfo) {
                        const filtersContainer = document.querySelector('.filters-container form');
                        if (filtersContainer) {
                            filtersContainer.parentNode.insertBefore(novoFiltersInfo, filtersContainer.nextSibling);
                        }
                    }
                } else {
                    const filtersInfoAtual = document.querySelector('.filters-info');
                    if (filtersInfoAtual) {
                        filtersInfoAtual.remove();
                    }
                }
                
                atualizarParametrosFormulario(params);
                
                const elapsedTime = Date.now() - loaderStartTime;
                const remainingTime = Math.max(0, 1000 - elapsedTime);
                
                setTimeout(() => {
                    reconfigurarEventListeners();
                    configurarSelectTamanho();
                    
                    if (tableLoader) {
                        tableLoader.style.display = 'none';
                    }
                    
                    window.history.pushState(params, '', url);
                }, remainingTime);
            })
            .catch(error => {
                console.error('Erro ao carregar clientes:', error);
                if (tableLoader) {
                    tableLoader.style.display = 'none';
                }
            });
    }

    function aplicarMascarasENumeros(params) {
        if (params.telefone) {
            params.telefone = params.telefone.replace(/\D/g, '');
        }
        if (params.cpf) {
            params.cpf = params.cpf.replace(/\D/g, '');
        }
        if (params.cep) {
            params.cep = params.cep.replace(/\D/g, '');
        }
    }

    function atualizarParametrosFormulario(params) {
        if (filtroForm) {
            const inputs = filtroForm.querySelectorAll('input[name]');
            inputs.forEach(input => {
                if (params[input.name] !== undefined) {
                    input.value = params[input.name];
                }
            });
            
            const selectTamanho = document.getElementById('tamanhoPagina');
            if (selectTamanho && params.tamanho) {
                selectTamanho.value = params.tamanho;
            }
        }
    }

  function reconfigurarEventListeners() {
    const linksPaginacao = document.querySelectorAll('.pagination-btn:not(.disabled), .pagination-number:not(.active)');
    linksPaginacao.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            const url = new URL(this.href);
            const params = {};
            
            url.searchParams.forEach((value, key) => {
                params[key] = value;
            });
            
            carregarClientes(params);
        });
    });
    
    const botoesDelete = document.querySelectorAll('.btn-action.btn-delete');
    botoesDelete.forEach(btn => {
        btn.addEventListener('click', function() {
            const clienteIdAtual = this.getAttribute('data-id');
            const clienteNome = this.getAttribute('data-nome');
            
            const spanNome = document.getElementById('clienteNomeModal');
            const spanId = document.getElementById('clienteId');
            
            if (spanNome && spanId) {
                spanNome.textContent = clienteNome;
                spanId.textContent = clienteIdAtual;
            }
            
            if (modalConfirmacao) {
                modalConfirmacao.style.display = 'flex';
                
                const btnConfirmar = document.getElementById('confirmarExclusao');
                const btnCancelar = document.getElementById('cancelarExclusao');
                
                if (btnConfirmar) {
                    btnConfirmar.onclick = function() {
                        excluirCliente(clienteIdAtual);
                    };
                }
                
                if (btnCancelar) {
                    btnCancelar.onclick = function() {
                        modalConfirmacao.style.display = 'none';
                    };
                }
            }
        });
    });
    
    const botoesView = document.querySelectorAll('.btn-action.btn-view');
    botoesView.forEach(btn => {
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

                    if (modalDetalhes) {
                        modalDetalhes.style.display = 'flex';
                    }
                })
                .catch(() => alert('Erro ao carregar detalhes do cliente'));
        });
    });
}

function excluirCliente(clienteId) {
    modalConfirmacao.style.display = 'none';
    
    loaderStartTime = Date.now();
    
    if (tableLoader) {
        tableLoader.style.display = 'flex';
    }
    
    fetch(`/clientes/excluir/${clienteId}`, {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Erro na exclusão');
        return response.text();
    })
    .then(html => {
        const url = new URL(window.location.href);
        const params = {};
        
        url.searchParams.forEach((value, key) => {
            params[key] = value;
        });
        
        params.pagina = 0;
        
        const elapsedTime = Date.now() - loaderStartTime;
        const remainingTime = Math.max(0, 0 - elapsedTime);
        
        setTimeout(() => {
            carregarClientes(params);
        }, remainingTime);
    })
    .catch(error => {
        console.error('Erro ao excluir cliente:', error);
        if (tableLoader) {
            tableLoader.style.display = 'none';
        }
        alert('Erro ao excluir cliente. Tente novamente.');
    });
}

    setupModalConfirmacao();
    setupModalDetalhes();
    aplicarMascarasInputs();
    adicionarDecoracoesFiltros();
    
    function setupModalConfirmacao() {
        const btnCancelar = document.getElementById('cancelarExclusao');
        const btnConfirmar = document.getElementById('confirmarExclusao');

        if (!modalConfirmacao || !btnCancelar || !btnConfirmar) return;

        btnCancelar.addEventListener('click', () => {
            modalConfirmacao.style.display = 'none';
        });

        modalConfirmacao.addEventListener('click', (e) => {
            if (e.target === modalConfirmacao) {
                modalConfirmacao.style.display = 'none';
            }
        });
    }

    function setupModalDetalhes() {
        const btnFecharDetalhes = document.getElementById('fecharDetalhes');
        if (btnFecharDetalhes) {
            btnFecharDetalhes.addEventListener('click', () => {
                modalDetalhes.style.display = 'none';
            });
        }

        if (modalDetalhes) {
            modalDetalhes.addEventListener('click', (e) => {
                if (e.target === modalDetalhes) {
                    modalDetalhes.style.display = 'none';
                }
            });
        }
    }

    function aplicarMascarasInputs() {
        const telefoneInput = document.getElementById('filtroTelefone');
        if (telefoneInput) {
            telefoneInput.addEventListener('input', function(e) {
                let value = e.target.value.replace(/\D/g, '');
                if (value.length > 11) value = value.substring(0, 11);
                
                if (value.length <= 10) {
                    value = value.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
                } else {
                    value = value.replace(/(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3');
                }
                
                e.target.value = value;
            });
        }
        
        const cpfInput = document.getElementById('filtroCpf');
        if (cpfInput) {
            cpfInput.addEventListener('input', function(e) {
                let value = e.target.value.replace(/\D/g, '');
                if (value.length > 11) value = value.substring(0, 11);
                
                value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4');
                
                e.target.value = value;
            });
        }
        
        const cepInput = document.getElementById('filtroCep');
        if (cepInput) {
            cepInput.addEventListener('input', function(e) {
                let value = e.target.value.replace(/\D/g, '');
                if (value.length > 8) value = value.substring(0, 8);
                
                value = value.replace(/(\d{5})(\d{0,3})/, '$1-$2');
                
                e.target.value = value;
            });
        }
    }

    function adicionarDecoracoesFiltros() {
        const filtersContainer = document.querySelector('.filters-container');
        if (filtersContainer) {
            const corners = ['top-left', 'top-right', 'bottom-left', 'bottom-right'];
            corners.forEach(corner => {
                const div = document.createElement('div');
                div.className = `corner-decoration ${corner}`;
                filtersContainer.appendChild(div);
            });
        }
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

    reconfigurarEventListeners();
    configurarSelectTamanho();
});