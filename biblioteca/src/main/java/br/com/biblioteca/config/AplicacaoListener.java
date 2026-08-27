package br.com.biblioteca.config;

import br.com.biblioteca.dao.LivroJdbcDAO;
import br.com.biblioteca.service.LivroService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AplicacaoListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        if (contexto.getAttribute(AtributosAplicacao.LIVRO_SERVICE) == null) {
            ConnectionFactory connectionFactory = ConnectionFactory.doAmbiente();
            LivroJdbcDAO livroDAO = new LivroJdbcDAO(connectionFactory);
            contexto.setAttribute(
                AtributosAplicacao.LIVRO_SERVICE,
                new LivroService(livroDAO)
            );
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DriverManager.drivers()
            .filter(driver -> driver.getClass().getClassLoader() == classLoader)
            .forEach(driver -> desregistrarDriver(evento.getServletContext(), driver));
    }

    private void desregistrarDriver(ServletContext contexto, Driver driver) {
        try {
            DriverManager.deregisterDriver(driver);
        } catch (SQLException excecao) {
            contexto.log("Falha ao desregistrar driver JDBC", excecao);
        }
    }
}
