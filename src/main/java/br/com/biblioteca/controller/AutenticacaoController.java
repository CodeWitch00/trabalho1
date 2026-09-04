package br.com.biblioteca.controller;

import br.com.biblioteca.config.AtributosAplicacao;
import br.com.biblioteca.filter.AutenticacaoFilter;
import br.com.biblioteca.filter.CsrfFilter;
import br.com.biblioteca.service.AutenticacaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AutenticacaoController extends HttpServlet {
    private static final String VIEW_LOGIN = "/WEB-INF/views/autenticacao/login.jsp";
    private AutenticacaoService autenticacaoService;

    @Override
    public void init() {
        Object service = getServletContext().getAttribute(AtributosAplicacao.AUTENTICACAO_SERVICE);
        if (!(service instanceof AutenticacaoService)) {
            throw new IllegalStateException("AutenticacaoService nao foi configurado no contexto");
        }
        autenticacaoService = (AutenticacaoService) service;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var usuario = autenticacaoService.autenticar(
            request.getParameter("email"), request.getParameter("senha")
        );
        if (usuario.isEmpty()) {
            HttpSession sessao = request.getSession();
            sessao.setAttribute("erroLogin", "E-mail ou senha invalidos.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        HttpSession sessao = request.getSession(true);
        sessao.setMaxInactiveInterval(30 * 60);
        sessao.setAttribute(AutenticacaoFilter.USUARIO_SESSAO, usuario.get());
        sessao.setAttribute(CsrfFilter.TOKEN_SESSAO, CsrfFilter.novoToken());
        response.sendRedirect(request.getContextPath() + destinoSeguro(request.getParameter("destino")));
    }

    private String destinoSeguro(String destino) {
        return destino != null && destino.startsWith("/") && !destino.startsWith("//")
            ? destino : "/livros";
    }
}
