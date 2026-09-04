package br.com.biblioteca.filter;

import br.com.biblioteca.model.UsuarioSessao;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AutenticacaoFilter implements Filter {
    public static final String USUARIO_SESSAO = "usuarioAutenticado";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Object usuario = request.getSession(false) == null ? null
            : request.getSession(false).getAttribute(USUARIO_SESSAO);
        if (usuario instanceof UsuarioSessao) {
            chain.doFilter(request, response);
            return;
        }

        String destino = request.getRequestURI().substring(request.getContextPath().length());
        if (request.getQueryString() != null) {
            destino += "?" + request.getQueryString();
        }
        response.sendRedirect(request.getContextPath() + "/login?destino="
            + URLEncoder.encode(destino, StandardCharsets.UTF_8));
    }
}
