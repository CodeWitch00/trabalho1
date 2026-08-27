/*
 * validacao.js
 *
 * Validações do lado do cliente para os formulários de Cadastro de livro
 * e de Cadastro de leitor(a), com base em manipulação do DOM e eventos
 * (blur, input, submit) — abordagem descrita por MICHAEL.
 *
 * Este script:
 *   - valida cada campo individualmente, exibindo mensagens de erro
 *     específicas próximas ao campo (sem depender apenas de cor);
 *   - reavalia o campo em tempo real (evento "input") somente depois que
 *     ele já foi marcado como inválido, evitando incomodar o usuário
 *     enquanto ele ainda está digitando pela primeira vez;
 *   - expõe funções reutilizáveis em window.Validacao, usadas tanto por
 *     script.js (cadastro de livro) quanto por cep.js (cadastro de leitor).
 *
 * Os atributos nativos do HTML5 (required, type, min, max) continuam
 * ativos; estas validações os complementam com regras que o HTML puro
 * não expressa (ex.: formato de ISBN, ano não futuro, e-mail bem
 * formado, CEP com 8 dígitos).
 */

(function () {
    'use strict';

    /* ---------------------------------------------------------------
       Utilitários de exibição de erro (manipulação do DOM)
       --------------------------------------------------------------- */

    function definirErro(input, mensagem) {
        if (!input) return;

        var campo = input.closest('.form-field');
        var erroEl = document.getElementById(input.id + '-erro');

        if (erroEl) {
            erroEl.textContent = mensagem || '';
        }
        if (campo) {
            campo.classList.toggle('invalido', Boolean(mensagem));
        }
        input.setAttribute('aria-invalid', mensagem ? 'true' : 'false');
    }

    function textoPreenchido(valor) {
        return typeof valor === 'string' && valor.trim() !== '';
    }

    /* ---------------------------------------------------------------
       Validadores — Cadastro de livro
       --------------------------------------------------------------- */

    function validarTitulo(input) {
        if (!textoPreenchido(input.value)) {
            definirErro(input, 'Informe o título da obra.');
            return false;
        }
        definirErro(input, '');
        return true;
    }

    function validarAutor(input) {
        if (!textoPreenchido(input.value)) {
            definirErro(input, 'Informe o nome do(a) autor(a).');
            return false;
        }
        definirErro(input, '');
        return true;
    }

    function validarCategoria(input) {
        if (!input.value) {
            definirErro(input, 'Selecione uma categoria.');
            return false;
        }
        definirErro(input, '');
        return true;
    }

    function validarISBN(input) {
        var valor = input.value.trim();

        if (valor === '') {
            definirErro(input, ''); // campo opcional
            return true;
        }

        var limpo = valor.replace(/[-\s]/g, '');
        var padrao = /^(\d{9}[\dXx]|\d{13})$/;

        if (!padrao.test(limpo)) {
            definirErro(input, 'Informe um ISBN válido, com 10 ou 13 dígitos.');
            return false;
        }

        definirErro(input, '');
        return true;
    }

    function validarAno(input) {
        var valor = input.value.trim();
        var anoAtual = new Date().getFullYear();

        if (valor === '') {
            definirErro(input, 'Informe o ano de publicação.');
            return false;
        }

        var numero = Number(valor);

        if (!Number.isInteger(numero) || numero < 1500 || numero > anoAtual) {
            definirErro(input, 'Informe um ano entre 1500 e ' + anoAtual + '.');
            return false;
        }

        definirErro(input, '');
        return true;
    }

    function validarExemplares(input) {
        var valor = input.value.trim();

        if (valor === '') {
            definirErro(input, 'Informe a quantidade de exemplares.');
            return false;
        }

        var numero = Number(valor);

        if (!Number.isInteger(numero) || numero < 0) {
            definirErro(input, 'Informe um número inteiro igual ou maior que zero.');
            return false;
        }

        definirErro(input, '');
        return true;
    }

    function validarStatus(input) {
        if (!input.value) {
            definirErro(input, 'Selecione o status do livro.');
            return false;
        }
        definirErro(input, '');
        return true;
    }

    /* ---------------------------------------------------------------
       Validadores — Cadastro de leitor(a)
       --------------------------------------------------------------- */

    function validarNomeLeitor(input) {
        if (!textoPreenchido(input.value)) {
            definirErro(input, 'Informe o nome completo.');
            return false;
        }
        definirErro(input, '');
        return true;
    }

    function validarEmail(input) {
        var valor = input.value.trim();
        var padrao = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (valor === '' || !padrao.test(valor)) {
            definirErro(input, 'Informe um e-mail válido (ex.: nome@dominio.com).');
            return false;
        }

        definirErro(input, '');
        return true;
    }

    function validarCEPCampo(input) {
        var valor = input.value.trim();
        var padrao = /^\d{5}-?\d{3}$/;

        if (!padrao.test(valor)) {
            definirErro(input, 'Informe um CEP válido, no formato 00000-000.');
            return false;
        }

        definirErro(input, '');
        return true;
    }

    /* ---------------------------------------------------------------
       Ligação dos eventos (blur / input) a cada campo
       --------------------------------------------------------------- */

    function ligarValidacaoTempoReal(input, validador) {
        if (!input) return;

        input.addEventListener('blur', function () {
            validador(input);
        });

        input.addEventListener('input', function () {
            var campo = input.closest('.form-field');
            if (campo && campo.classList.contains('invalido')) {
                validador(input);
            }
        });
    }

    /* ---------------------------------------------------------------
       Validação completa de cada formulário (usada no evento submit)
       --------------------------------------------------------------- */

    function executarValidacoes(lista) {
        var valido = true;
        var primeiroInvalido = null;

        lista.forEach(function (item) {
            var ok = item.validador(item.input);
            if (!ok) {
                valido = false;
                if (!primeiroInvalido) {
                    primeiroInvalido = item.input;
                }
            }
        });

        if (primeiroInvalido) {
            primeiroInvalido.focus();
        }

        return valido;
    }

    function validarFormularioLivro(form) {
        return executarValidacoes([
            { input: form.elements.titulo, validador: validarTitulo },
            { input: form.elements.autor, validador: validarAutor },
            { input: form.elements.categoria, validador: validarCategoria },
            { input: form.elements.isbn, validador: validarISBN },
            { input: form.elements.ano, validador: validarAno },
            { input: form.elements.exemplares, validador: validarExemplares },
            { input: form.elements.status, validador: validarStatus }
        ]);
    }

    function validarFormularioLeitor(form) {
        return executarValidacoes([
            { input: form.elements.nome, validador: validarNomeLeitor },
            { input: form.elements.email, validador: validarEmail },
            { input: form.elements.cep, validador: validarCEPCampo }
        ]);
    }

    function iniciarValidacaoLivro(form) {
        ligarValidacaoTempoReal(form.elements.titulo, validarTitulo);
        ligarValidacaoTempoReal(form.elements.autor, validarAutor);
        ligarValidacaoTempoReal(form.elements.categoria, validarCategoria);
        ligarValidacaoTempoReal(form.elements.isbn, validarISBN);
        ligarValidacaoTempoReal(form.elements.ano, validarAno);
        ligarValidacaoTempoReal(form.elements.exemplares, validarExemplares);
        ligarValidacaoTempoReal(form.elements.status, validarStatus);
    }

    function iniciarValidacaoLeitor(form) {
        ligarValidacaoTempoReal(form.elements.nome, validarNomeLeitor);
        ligarValidacaoTempoReal(form.elements.email, validarEmail);
        ligarValidacaoTempoReal(form.elements.cep, validarCEPCampo);
    }

    function limparValidacao(form) {
        var campos = form.querySelectorAll('.form-field');
        var entradas = form.querySelectorAll('[aria-invalid]');
        var erros = form.querySelectorAll('.erro-campo');

        Array.prototype.forEach.call(campos, function (campo) {
            campo.classList.remove('invalido');
        });
        Array.prototype.forEach.call(entradas, function (input) {
            input.setAttribute('aria-invalid', 'false');
        });
        Array.prototype.forEach.call(erros, function (erro) {
            erro.textContent = '';
        });
    }

    /* ---------------------------------------------------------------
       API pública do módulo
       --------------------------------------------------------------- */

    window.Validacao = {
        validarFormularioLivro: validarFormularioLivro,
        validarFormularioLeitor: validarFormularioLeitor,
        iniciarValidacaoLivro: iniciarValidacaoLivro,
        iniciarValidacaoLeitor: iniciarValidacaoLeitor,
        limparValidacao: limparValidacao
    };

    /* ---------------------------------------------------------------
       Inicialização: liga a validação em tempo real assim que a
       página carrega, para os dois formulários.
       --------------------------------------------------------------- */

    document.addEventListener('DOMContentLoaded', function () {
        var formLivro = document.getElementById('form-cadastro');
        var formLeitor = document.getElementById('form-leitor');

        if (formLivro) {
            iniciarValidacaoLivro(formLivro);
            formLivro.addEventListener('reset', function () {
                window.setTimeout(function () {
                    limparValidacao(formLivro);
                }, 0);
            });
        }

        if (formLeitor) {
            iniciarValidacaoLeitor(formLeitor);
            formLeitor.addEventListener('reset', function () {
                window.setTimeout(function () {
                    limparValidacao(formLeitor);
                }, 0);
            });
        }
    });

})();
