package br.com.biblioteca.controller;

import br.com.biblioteca.config.AtributosAplicacao;
import br.com.biblioteca.exception.LivroNaoEncontradoException;
import br.com.biblioteca.exception.PersistenciaException;
import br.com.biblioteca.exception.ValidacaoException;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.StatusLivro;
import br.com.biblioteca.service.LivroService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LivroController extends HttpServlet {
    static final String VIEW_LISTA = "/WEB-INF/views/livros/lista.jsp";
    static final String VIEW_FORMULARIO = "/WEB-INF/views/livros/formulario.jsp";

    private LivroService livroService;

    @Override
    public void init() {
        Object service = getServletContext().getAttribute(AtributosAplicacao.LIVRO_SERVICE);
        if (!(service instanceof LivroService)) {
            throw new IllegalStateException("LivroService nao foi configurado no contexto");
        }
        this.livroService = (LivroService) service;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try {
            String acao = acao(request);
            if ("/novo".equals(acao)) {
                abrirFormularioNovo(request, response);
            } else if ("/editar".equals(acao)) {
                abrirFormularioEdicao(request, response);
            } else {
                listar(request, response);
            }
        } catch (ValidacaoException | LivroNaoEncontradoException excecao) {
            redirecionarComMensagem(request, response, "erro", excecao.getMessage());
        } catch (PersistenciaException excecao) {
            getServletContext().log("Falha ao listar livros", excecao);
            request.setAttribute("livros", List.of());
            request.setAttribute("busca", valorOuVazio(request.getParameter("busca")));
            request.setAttribute("totalLivros", 0);
            request.setAttribute("flashTipo", "erro");
            request.setAttribute(
                "flashMensagem",
                "Nao foi possivel acessar os livros. Tente novamente."
            );
            encaminhar(request, response, VIEW_LISTA);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try {
            String acao = acao(request);
            if ("/atualizar".equals(acao)) {
                atualizar(request, response);
            } else if ("/excluir".equals(acao)) {
                excluir(request, response);
            } else {
                cadastrar(request, response);
            }
        } catch (ValidacaoException excecao) {
            tratarErroFormulario(request, response, excecao);
        } catch (LivroNaoEncontradoException excecao) {
            redirecionarComMensagem(request, response, "erro", excecao.getMessage());
        } catch (PersistenciaException excecao) {
            redirecionarComMensagem(
                request,
                response,
                "erro",
                "Nao foi possivel salvar os dados do livro. Tente novamente."
            );
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String termo = request.getParameter("busca");
        var livros = livroService.pesquisar(termo);
        request.setAttribute("livros", livros);
        request.setAttribute("busca", valorOuVazio(termo));
        request.setAttribute("totalLivros", livros.size());
        request.setAttribute("flashTipo", consumirFlash(request, "flashTipo"));
        request.setAttribute("flashMensagem", consumirFlash(request, "flashMensagem"));
        encaminhar(request, response, VIEW_LISTA);
    }

    private void abrirFormularioNovo(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        prepararFormulario(request, new Livro(), "Cadastrar livro", "/livros");
        encaminhar(request, response, VIEW_FORMULARIO);
    }

    private void abrirFormularioEdicao(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        long id = lerId(request);
        Livro livro = livroService.buscarPorId(id);
        prepararFormulario(request, livro, "Editar livro", "/livros/atualizar");
        encaminhar(request, response, VIEW_FORMULARIO);
    }

    private void cadastrar(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        Livro livro = livroDoRequest(request);
        Livro cadastrado = livroService.cadastrar(livro);
        redirecionarComMensagem(
            request,
            response,
            "sucesso",
            "Livro \"" + cadastrado.getTitulo() + "\" cadastrado com sucesso."
        );
    }

    private void atualizar(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        Livro livro = livroDoRequest(request);
        livro.setId(lerId(request));
        Livro atualizado = livroService.atualizar(livro);
        redirecionarComMensagem(
            request,
            response,
            "sucesso",
            "Livro \"" + atualizado.getTitulo() + "\" atualizado com sucesso."
        );
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        long id = lerId(request);
        livroService.excluir(id);
        redirecionarComMensagem(request, response, "sucesso", "Livro excluido com sucesso.");
    }

    private Livro livroDoRequest(HttpServletRequest request) {
        Livro livro = new Livro();
        livro.setTitulo(request.getParameter("titulo"));
        livro.setAutor(request.getParameter("autor"));
        livro.setCategoria(request.getParameter("categoria"));
        livro.setIsbn(request.getParameter("isbn"));
        livro.setAnoPublicacao(lerInteiro(request, "anoPublicacao", "ano"));
        livro.setQuantidadeExemplares(lerInteiro(
            request,
            "quantidadeExemplares",
            "exemplares"
        ));
        livro.setStatus(lerStatus(request));
        return livro;
    }

    private int lerInteiro(HttpServletRequest request, String nomePrincipal, String nomeAlternativo) {
        String valor = request.getParameter(nomePrincipal);
        if (valor == null || valor.isBlank()) {
            valor = request.getParameter(nomeAlternativo);
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException | NullPointerException excecao) {
            throw new ValidacaoException(Map.of(nomePrincipal, "Informe um numero valido"));
        }
    }

    private StatusLivro lerStatus(HttpServletRequest request) {
        try {
            return StatusLivro.deTexto(request.getParameter("status"));
        } catch (IllegalArgumentException excecao) {
            throw new ValidacaoException(Map.of("status", "Informe um status valido"));
        }
    }

    private long lerId(HttpServletRequest request) {
        String valor = request.getParameter("id");
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException | NullPointerException excecao) {
            throw new ValidacaoException(Map.of("id", "O identificador do livro e invalido"));
        }
    }

    private void tratarErroFormulario(
        HttpServletRequest request,
        HttpServletResponse response,
        ValidacaoException excecao
    ) throws ServletException, IOException {
        Livro livro = livroDoRequestSemStatusObrigatorio(request);
        String acao = acao(request);
        if ("/atualizar".equals(acao)) {
            livro.setId(lerId(request));
            prepararFormulario(request, livro, "Editar livro", "/livros/atualizar");
        } else {
            prepararFormulario(request, livro, "Cadastrar livro", "/livros");
        }
        request.setAttribute("erros", excecao.getErros());
        request.setAttribute("flashTipo", "erro");
        request.setAttribute("flashMensagem", "Corrija os campos destacados.");
        encaminhar(request, response, VIEW_FORMULARIO);
    }

    private Livro livroDoRequestSemStatusObrigatorio(HttpServletRequest request) {
        Livro livro = new Livro();
        livro.setTitulo(request.getParameter("titulo"));
        livro.setAutor(request.getParameter("autor"));
        livro.setCategoria(request.getParameter("categoria"));
        livro.setIsbn(request.getParameter("isbn"));
        livro.setAnoPublicacao(lerInteiroOuZero(request, "anoPublicacao", "ano"));
        livro.setQuantidadeExemplares(lerInteiroOuZero(
            request,
            "quantidadeExemplares",
            "exemplares"
        ));
        try {
            livro.setStatus(StatusLivro.deTexto(request.getParameter("status")));
        } catch (IllegalArgumentException excecao) {
            livro.setStatus(null);
        }
        return livro;
    }

    private int lerInteiroOuZero(
        HttpServletRequest request,
        String nomePrincipal,
        String nomeAlternativo
    ) {
        String valor = request.getParameter(nomePrincipal);
        if (valor == null || valor.isBlank()) {
            valor = request.getParameter(nomeAlternativo);
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException | NullPointerException excecao) {
            return 0;
        }
    }

    private void prepararFormulario(
        HttpServletRequest request,
        Livro livro,
        String tituloPagina,
        String acaoFormulario
    ) {
        request.setAttribute("livro", livro);
        request.setAttribute("tituloPagina", tituloPagina);
        request.setAttribute("acaoFormulario", request.getContextPath() + acaoFormulario);
        request.setAttribute("statusLivros", Arrays.asList(StatusLivro.values()));
        request.setAttribute("anoAtual", LocalDate.now().getYear());
    }

    private String acao(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null || pathInfo.isBlank() ? "/" : pathInfo;
    }

    private void encaminhar(
        HttpServletRequest request,
        HttpServletResponse response,
        String view
    ) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(view);
        dispatcher.forward(request, response);
    }

    private void redirecionarComMensagem(
        HttpServletRequest request,
        HttpServletResponse response,
        String tipo,
        String mensagem
    ) throws IOException {
        HttpSession sessao = request.getSession();
        sessao.setAttribute("flashTipo", tipo);
        sessao.setAttribute("flashMensagem", mensagem);
        response.sendRedirect(request.getContextPath() + "/livros");
    }

    private String valorOuVazio(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private Object consumirFlash(HttpServletRequest request, String nome) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            return null;
        }
        Object valor = sessao.getAttribute(nome);
        sessao.removeAttribute(nome);
        return valor;
    }
}
