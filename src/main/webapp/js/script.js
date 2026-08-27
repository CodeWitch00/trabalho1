/*
 * script.js
 * JavaScript complementar do Sistema de Gestão de Biblioteca.
 * Adiciona cadastro dinâmico, busca no acervo, alternância de tema claro/escuro
 * e controle de exibição de fichas empilhadas.A validação detalhada dos campos fica em validacao.js,
 * e a busca de endereço por CEP fica em cep.js.
 */

document.addEventListener('DOMContentLoaded', function () {

    var form = document.getElementById('form-cadastro');
    var tabela = document.getElementById('tabela-acervo');
    var formFeedback = document.getElementById('form-feedback');
    var campoBusca = document.getElementById('campo-busca');
    var botaoLimparBusca = document.getElementById('limpar-busca');
    var contadorResultados = document.getElementById('contador-resultados');
    var semResultados = document.getElementById('sem-resultados');
    var botaoTema = document.getElementById('alternar-tema');

    /* ---------------------------------------------------------------
       1. Cadastro de novo livro
       --------------------------------------------------------------- */

    if (form && tabela) {
        form.addEventListener('submit', function (evento) {
            evento.preventDefault();

            var valido = window.Validacao
                ? window.Validacao.validarFormularioLivro(form)
                : form.checkValidity();

            if (!valido) {
                if (!window.Validacao) {
                    form.reportValidity();
                }
                mostrarFeedback(formFeedback, 'Preencha todos os campos obrigatórios antes de cadastrar.', 'erro');
                return;
            }

            var livro = {
                titulo: form.titulo.value.trim(),
                autor: form.autor.value.trim(),
                categoria: form.categoria.value,
                ano: form.ano.value,
                exemplares: form.exemplares.value,
                status: form.status.value
            };

            adicionarLinha(livro);
            atualizarResumo();
            aplicarFiltro();
            mostrarFeedback(formFeedback, 'Livro "' + livro.titulo + '" cadastrado com sucesso.', 'sucesso');

            form.reset();
        });
    }

    function adicionarLinha(livro) {
        var linha = document.createElement('tr');
        linha.setAttribute('data-status', livro.status);

        var colunas = [
            { chave: 'titulo', rotulo: 'Título' },
            { chave: 'autor', rotulo: 'Autor(a)' },
            { chave: 'categoria', rotulo: 'Categoria' },
            { chave: 'ano', rotulo: 'Ano' },
            { chave: 'exemplares', rotulo: 'Exemplares' }
        ];

        colunas.forEach(function (coluna) {
            var celula = document.createElement('td');
            celula.setAttribute('data-label', coluna.rotulo);
            celula.textContent = livro[coluna.chave];
            linha.appendChild(celula);
        });

        var celulaStatus = document.createElement('td');
        celulaStatus.setAttribute('data-label', 'Status');

        var badge = document.createElement('span');
        badge.className = 'status ' + classeStatus(livro.status);
        badge.textContent = livro.status;

        celulaStatus.appendChild(badge);
        linha.appendChild(celulaStatus);

        tabela.appendChild(linha);
    }

    function classeStatus(status) {
        if (status === 'Disponível') return 'status--disponivel';
        if (status === 'Emprestado') return 'status--emprestado';
        if (status === 'Reservado') return 'status--reservado';
        return '';
    }

    /* ---------------------------------------------------------------
       2. Resumo do acervo
       --------------------------------------------------------------- */

    function atualizarResumo() {
        var linhas = tabela.querySelectorAll('tr');
        var total = linhas.length;
        var disponiveis = 0;
        var emprestados = 0;
        var reservados = 0;
        var categorias = new Set();
        var autores = new Set();

        linhas.forEach(function (linha) {
            var status = linha.getAttribute('data-status');
            var categoria = linha.children[2] ? linha.children[2].textContent.trim() : '';
            var autor = linha.children[1] ? linha.children[1].textContent.trim() : '';

            if (status === 'Disponível') disponiveis += 1;
            if (status === 'Emprestado') emprestados += 1;
            if (status === 'Reservado') reservados += 1;
            if (categoria) categorias.add(categoria);
            if (autor) autores.add(autor);
        });

        definirTexto('resumo-total', total);
        definirTexto('resumo-disponiveis', disponiveis);
        definirTexto('resumo-emprestados', emprestados);
        definirTexto('resumo-reservados', reservados);
        definirTexto('resumo-categorias', categorias.size);
        definirTexto('resumo-autores', autores.size);
    }

    function definirTexto(id, valor) {
        var elemento = document.getElementById(id);
        if (elemento) {
            elemento.textContent = valor;
        }
    }

    /* ---------------------------------------------------------------
       3. Busca no acervo
       --------------------------------------------------------------- */

    function aplicarFiltro() {
        if (!tabela || !campoBusca) {
            return;
        }

        var termo = campoBusca.value.trim().toLocaleLowerCase('pt-BR');
        var linhas = tabela.querySelectorAll('tr');
        var visiveis = 0;

        linhas.forEach(function (linha) {
            var titulo = linha.children[0] ? linha.children[0].textContent.toLocaleLowerCase('pt-BR') : '';
            var autor = linha.children[1] ? linha.children[1].textContent.toLocaleLowerCase('pt-BR') : '';
            var categoria = linha.children[2] ? linha.children[2].textContent.toLocaleLowerCase('pt-BR') : '';

            var corresponde = termo === '' ||
                titulo.indexOf(termo) !== -1 ||
                autor.indexOf(termo) !== -1 ||
                categoria.indexOf(termo) !== -1;

            if (corresponde) {
                linha.hidden = false;
                visiveis += 1;
            } else {
                linha.hidden = true;
            }
        });

        if (contadorResultados) {
            contadorResultados.textContent = visiveis === 1
                ? '1 livro encontrado.'
                : visiveis + ' livros encontrados.';
        }

        if (semResultados) {
            semResultados.hidden = visiveis !== 0;
        }
    }

    if (campoBusca) {
        campoBusca.addEventListener('input', aplicarFiltro);
    }

    if (botaoLimparBusca) {
        botaoLimparBusca.addEventListener('click', function () {
            campoBusca.value = '';
            campoBusca.focus();
            aplicarFiltro();
        });
    }

    /* ---------------------------------------------------------------
       4. Alternância de Fichas (Exibe apenas a ficha selecionada)
       --------------------------------------------------------------- */

    var botoesNav = document.querySelectorAll('.nav-btn');
    var fichas = document.querySelectorAll('.ficha');

    function exibirFicha(alvoId) {
        if (!fichas.length) return;

        fichas.forEach(function (ficha) {
            if (alvoId === 'todas') {
                ficha.hidden = false;
                ficha.classList.remove('ficha--oculta');
            } else if (ficha.id === alvoId) {
                ficha.hidden = false;
                ficha.classList.remove('ficha--oculta');
            } else {
                ficha.hidden = true;
                ficha.classList.add('ficha--oculta');
            }
        });

        botoesNav.forEach(function (btn) {
            var target = btn.getAttribute('data-target');
            var ativo = (target === alvoId);

            if (ativo) {
                btn.classList.add('active');
                btn.setAttribute('aria-selected', 'true');
            } else {
                btn.classList.remove('active');
                btn.setAttribute('aria-selected', 'false');
            }
        });
    }

    botoesNav.forEach(function (btn) {
        btn.addEventListener('click', function () {
            var target = btn.getAttribute('data-target');
            if (target) {
                exibirFicha(target);
            }
        });
    });

    // Detectar hash na URL (ex: #acervo) se houver
    var hash = window.location.hash.replace('#', '');
    if (hash && (hash === 'cadastro' || hash === 'acervo' || hash === 'leitor' || hash === 'resumo')) {
        exibirFicha(hash);
    } else {
        // Exibe apenas a Ficha 1 por padrão
        exibirFicha('cadastro');
    }

    /* ---------------------------------------------------------------
       5. Tema claro / escuro
       --------------------------------------------------------------- */

    var CHAVE_TEMA = 'biblioteca-tema';

    function aplicarTema(tema) {
        if (tema === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
        } else {
            document.documentElement.removeAttribute('data-theme');
        }
        atualizarBotaoTema(tema);
    }

    function atualizarBotaoTema(tema) {
        if (!botaoTema) return;
        var icone = botaoTema.querySelector('.botao-tema__icone');
        var texto = botaoTema.querySelector('.botao-tema__texto');
        var escuro = tema === 'dark';

        botaoTema.setAttribute('aria-pressed', escuro ? 'true' : 'false');
        if (icone) icone.textContent = escuro ? '☀️' : '🌙';
        if (texto) texto.textContent = escuro ? 'Tema claro' : 'Tema escuro';
    }

    function temaSalvo() {
        try {
            return localStorage.getItem(CHAVE_TEMA);
        } catch (erro) {
            return null;
        }
    }

    function salvarTema(tema) {
        try {
            localStorage.setItem(CHAVE_TEMA, tema);
        } catch (erro) {
            /* localStorage indisponível */
        }
    }

    var temaInicial = temaSalvo() || 'light';
    aplicarTema(temaInicial);

    if (botaoTema) {
        botaoTema.addEventListener('click', function () {
            var atual = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
            var novo = atual === 'dark' ? 'light' : 'dark';
            aplicarTema(novo);
            salvarTema(novo);
        });
    }

    /* ---------------------------------------------------------------
       6. Mensagens de feedback
       --------------------------------------------------------------- */

    function mostrarFeedback(elemento, mensagem, estado) {
        if (!elemento) return;
        elemento.textContent = mensagem;
        elemento.setAttribute('data-state', estado);
    }

});
