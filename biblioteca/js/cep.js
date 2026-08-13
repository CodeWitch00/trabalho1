/*
 * cep.js
 *
 * Requisição assíncrona (fetch / Ajax) para preenchimento automático de
 * endereço a partir do CEP informado no Cadastro de leitor(a) — conceitos
 * de requisições assíncronas descritos por RIORDAN.
 *
 * Utiliza a API pública ViaCEP (https://viacep.com.br/). A chamada ocorre
 * sem recarregar a página: ao terminar, os campos de endereço são
 * preenchidos via manipulação do DOM, e uma mensagem de status
 * (carregando / sucesso / erro) é apresentada em uma região com
 * aria-live, para leitores de tela.
 *
 * Este script também conecta o resultado da busca ao envio do formulário
 * de leitor(a): ao cadastrar, a linha correspondente é adicionada à
 * tabela "Leitores cadastrados" sem recarregar a página.
 */


(function () {
    'use strict';

    var ENDPOINT_VIACEP = 'https://viacep.com.br/ws/01001000/json/';

    /* ---------------------------------------------------------------
       Utilitários
       --------------------------------------------------------------- */

    function normalizarCEP(valor) {
        return (valor || '').replace(/\D/g, '');
    }

    function definirStatusCEP(mensagem, estado) {
        var status = document.getElementById('cep-status');
        if (!status) return;
        status.textContent = mensagem;
        status.setAttribute('data-state', estado || '');
    }

    function preencherEndereco(dados) {
        var campos = {
            'leitor-logradouro': dados.logradouro || '',
            'leitor-bairro': dados.bairro || '',
            'leitor-cidade': dados.localidade || '',
            'leitor-uf': dados.uf || ''
        };

        Object.keys(campos).forEach(function (id) {
            var elemento = document.getElementById(id);
            if (elemento) {
                elemento.value = campos[id];
            }
        });
    }

    function limparEndereco() {
        preencherEndereco({});
    }

    /* ---------------------------------------------------------------
       Requisição assíncrona (fetch) à API ViaCEP
       --------------------------------------------------------------- */

    function buscarEndereco(cepDigitado) {
        var cep = normalizarCEP(cepDigitado);
        var botao = document.getElementById('buscar-cep');

        if (cep.length !== 8) {
            definirStatusCEP('Informe um CEP com 8 dígitos para buscar o endereço automaticamente.', 'erro');
            return Promise.resolve();
        }

        definirStatusCEP('Buscando endereço...', 'carregando');
        if (botao) {
            botao.disabled = true;
        }

        return fetch(ENDPOINT_VIACEP + cep + '/json/')
            .then(function (resposta) {
                if (!resposta.ok) {
                    throw new Error('Falha na comunicação com o serviço de CEP.');
                }
                return resposta.json();
            })
            .then(function (dados) {
                if (dados.erro) {
                    limparEndereco();
                    definirStatusCEP('CEP não encontrado. Verifique o número informado ou preencha o endereço manualmente.', 'erro');
                    return;
                }
                preencherEndereco(dados);
                definirStatusCEP('Endereço encontrado e preenchido automaticamente.', 'sucesso');
            })
            .catch(function () {
                definirStatusCEP('Não foi possível buscar o endereço agora. Preencha manualmente ou tente novamente.', 'erro');
            })
            .finally(function () {
                if (botao) {
                    botao.disabled = false;
                }
            });
    }

    /* ---------------------------------------------------------------
       Cadastro de leitor(a): adiciona uma linha à tabela sem recarregar
       a página, após validação bem-sucedida.
       --------------------------------------------------------------- */

    function adicionarLinhaLeitor(tabela, leitor) {
        var linha = document.createElement('tr');

        var cidadeUf = leitor.cidade
            ? leitor.cidade + (leitor.uf ? '/' + leitor.uf : '')
            : 'Não informado';

        var valores = [
            { texto: leitor.nome, rotulo: 'Nome' },
            { texto: leitor.email, rotulo: 'E-mail' },
            { texto: cidadeUf, rotulo: 'Cidade/UF' }
        ];

        valores.forEach(function (item) {
            var celula = document.createElement('td');
            celula.setAttribute('data-label', item.rotulo);
            celula.textContent = item.texto; // textContent: nunca innerHTML com dados do usuário
            linha.appendChild(celula);
        });

        tabela.appendChild(linha);
    }

    function mostrarFeedbackLeitor(elemento, mensagem, estado) {
        if (!elemento) return;
        elemento.textContent = mensagem;
        elemento.setAttribute('data-state', estado);
    }

    /* ---------------------------------------------------------------
       Inicialização
       --------------------------------------------------------------- */

    document.addEventListener('DOMContentLoaded', function () {
        var campoCEP = document.getElementById('leitor-cep');
        var botaoBuscar = document.getElementById('buscar-cep');
        var formLeitor = document.getElementById('form-leitor');
        var tabelaLeitores = document.getElementById('tabela-leitores');
        var feedbackLeitor = document.getElementById('leitor-feedback');

        /* Busca automática ao sair do campo CEP (evento blur) */
        if (campoCEP) {
            campoCEP.addEventListener('blur', function () {
                if (normalizarCEP(campoCEP.value).length === 8) {
                    buscarEndereco(campoCEP.value);
                }
            });
        }

        /* Busca manual, para quem prefere usar o botão */
        if (botaoBuscar && campoCEP) {
            botaoBuscar.addEventListener('click', function () {
                buscarEndereco(campoCEP.value);
            });
        }

        /* Envio do formulário de leitor(a) */
        if (formLeitor && tabelaLeitores) {
            formLeitor.addEventListener('submit', function (evento) {
                evento.preventDefault();

                var valido = window.Validacao
                    ? window.Validacao.validarFormularioLeitor(formLeitor)
                    : formLeitor.checkValidity();

                if (!valido) {
                    mostrarFeedbackLeitor(feedbackLeitor, 'Corrija os campos destacados antes de cadastrar.', 'erro');
                    return;
                }

                var leitor = {
                    nome: formLeitor.elements.nome.value.trim(),
                    email: formLeitor.elements.email.value.trim(),
                    cidade: formLeitor.elements.cidade.value.trim(),
                    uf: formLeitor.elements.uf.value.trim()
                };

                adicionarLinhaLeitor(tabelaLeitores, leitor);
                mostrarFeedbackLeitor(feedbackLeitor, 'Leitor(a) "' + leitor.nome + '" cadastrado(a) com sucesso.', 'sucesso');

                formLeitor.reset();
                definirStatusCEP('', '');
            });
        }
    });

    /* Exposto para eventual reuso/teste manual no console */
    window.CEP = {
        buscarEndereco: buscarEndereco
    };

})();
