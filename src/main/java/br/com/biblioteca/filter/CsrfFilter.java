package br.com.biblioteca.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class CsrfFilter implements Filter {
    public static final String TOKEN_SESSAO = "csrfToken";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        Object tokenSessao = request.getSession(false).getAttribute(TOKEN_SESSAO);
        String tokenFormulario = request.getParameter("_csrf");
        if (!(tokenSessao instanceof String) || !tokenSessao.equals(tokenFormulario)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solicitacao invalida");
            return;
        }
        chain.doFilter(request, response);
    }

    public static String novoToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
