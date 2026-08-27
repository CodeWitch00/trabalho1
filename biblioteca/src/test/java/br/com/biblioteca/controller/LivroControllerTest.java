package br.com.biblioteca.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.biblioteca.config.AtributosAplicacao;
import br.com.biblioteca.exception.ValidacaoException;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.StatusLivro;
import br.com.biblioteca.service.LivroService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LivroControllerTest {
    private LivroService livroService;
    private LivroController controller;

    @BeforeEach
    void configurarController() throws Exception {
        livroService = mock(LivroService.class);

        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute(AtributosAplicacao.LIVRO_SERVICE))
            .thenReturn(livroService);

        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        controller = new LivroController();
        controller.init(servletConfig);
    }

    @Test
    void deveListarLivros() throws Exception {
        Livro livro = livro(1L, "Dom Casmurro");
        when(livroService.pesquisar("Dom")).thenReturn(List.of(livro));

        HttpServletRequest request = requestComDispatcher(LivroController.VIEW_LISTA);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn(null);
        when(request.getParameter("busca")).thenReturn("Dom");

        controller.doGet(request, response);

        verify(livroService).pesquisar("Dom");
        verify(request).setAttribute("livros", List.of(livro));
        verify(request).setAttribute("totalLivros", 1);
    }

    @Test
    void deveAbrirFormularioDeNovoLivro() throws Exception {
        HttpServletRequest request = requestComDispatcher(LivroController.VIEW_FORMULARIO);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("/novo");
        when(request.getContextPath()).thenReturn("/biblioteca");

        controller.doGet(request, response);

        verify(request).setAttribute(eq("livro"), any(Livro.class));
        verify(request).setAttribute("tituloPagina", "Cadastrar livro");
        verify(request).setAttribute("acaoFormulario", "/biblioteca/livros");
    }

    @Test
    void deveAbrirFormularioDeEdicao() throws Exception {
        Livro livro = livro(7L, "1984");
        when(livroService.buscarPorId(7L)).thenReturn(livro);

        HttpServletRequest request = requestComDispatcher(LivroController.VIEW_FORMULARIO);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("/editar");
        when(request.getParameter("id")).thenReturn("7");
        when(request.getContextPath()).thenReturn("/biblioteca");

        controller.doGet(request, response);

        verify(livroService).buscarPorId(7L);
        verify(request).setAttribute("livro", livro);
        verify(request).setAttribute("tituloPagina", "Editar livro");
        verify(request).setAttribute("acaoFormulario", "/biblioteca/livros/atualizar");
    }

    @Test
    void deveCadastrarLivroERedirecionar() throws Exception {
        Livro livroCadastrado = livro(10L, "O Hobbit");
        when(livroService.cadastrar(any(Livro.class))).thenReturn(livroCadastrado);

        HttpSession sessao = mock(HttpSession.class);
        HttpServletRequest request = requestDeFormulario(sessao);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn(null);
        when(request.getContextPath()).thenReturn("/biblioteca");

        controller.doPost(request, response);

        verify(livroService).cadastrar(any(Livro.class));
        verify(sessao).setAttribute("flashTipo", "sucesso");
        verify(response).sendRedirect("/biblioteca/livros");
    }

    @Test
    void deveExcluirLivroERedirecionar() throws Exception {
        HttpSession sessao = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("/excluir");
        when(request.getParameter("id")).thenReturn("5");
        when(request.getContextPath()).thenReturn("/biblioteca");
        when(request.getSession()).thenReturn(sessao);

        controller.doPost(request, response);

        verify(livroService).excluir(5L);
        verify(sessao).setAttribute("flashMensagem", "Livro excluido com sucesso.");
        verify(response).sendRedirect("/biblioteca/livros");
    }

    @Test
    void deveVoltarParaFormularioQuandoServiceRejeitaCadastro() throws Exception {
        when(livroService.cadastrar(any(Livro.class)))
            .thenThrow(new ValidacaoException(Map.of("titulo", "O titulo e obrigatorio")));

        HttpSession sessao = mock(HttpSession.class);
        HttpServletRequest request = requestDeFormulario(sessao);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn(null);
        when(request.getContextPath()).thenReturn("/biblioteca");
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getRequestDispatcher(LivroController.VIEW_FORMULARIO)).thenReturn(dispatcher);

        controller.doPost(request, response);

        verify(request).setAttribute(eq("erros"), any(Map.class));
        verify(request).setAttribute("flashTipo", "erro");
        verify(dispatcher).forward(request, response);
    }

    private HttpServletRequest requestComDispatcher(String view) throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession sessao = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getSession(false)).thenReturn(sessao);
        when(request.getRequestDispatcher(view)).thenReturn(dispatcher);
        return request;
    }

    private HttpServletRequest requestDeFormulario(HttpSession sessao) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(sessao);
        when(request.getParameter("titulo")).thenReturn("O Hobbit");
        when(request.getParameter("autor")).thenReturn("J.R.R. Tolkien");
        when(request.getParameter("categoria")).thenReturn("Fantasia");
        when(request.getParameter("isbn")).thenReturn("9788595084742");
        when(request.getParameter("anoPublicacao")).thenReturn("1937");
        when(request.getParameter("quantidadeExemplares")).thenReturn("4");
        when(request.getParameter("status")).thenReturn("DISPONIVEL");
        return request;
    }

    private Livro livro(Long id, String titulo) {
        Livro livro = new Livro(
            titulo,
            "Autor",
            "Categoria",
            null,
            2000,
            1,
            StatusLivro.DISPONIVEL
        );
        livro.setId(id);
        return livro;
    }
}
