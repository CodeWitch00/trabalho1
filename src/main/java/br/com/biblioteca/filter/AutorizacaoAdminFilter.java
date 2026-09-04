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

public class AutorizacaoAdminFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        UsuarioSessao usuario = (UsuarioSessao) request.getSession(false)
            .getAttribute(AutenticacaoFilter.USUARIO_SESSAO);
        boolean operacaoMutavel = "POST".equalsIgnoreCase(request.getMethod());
        String path = request.getPathInfo();
        boolean telaAdministrativa = "/novo".equals(path) || "/editar".equals(path);

        if (operacaoMutavel || telaAdministrativa) {
            if (!usuario.isAdministrador()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso restrito a administradores");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
