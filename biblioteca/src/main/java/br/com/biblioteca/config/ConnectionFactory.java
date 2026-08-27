package br.com.biblioteca.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionFactory implements ConnectionProvider {
    private static final String DRIVER_POSTGRESQL = "org.postgresql.Driver";

    private final String url;
    private final String usuario;
    private final String senha;

    public ConnectionFactory(String url, String usuario, String senha) {
        this.url = exigirValor(url, "URL do banco");
        this.usuario = exigirValor(usuario, "Usuário do banco");
        this.senha = Objects.requireNonNull(senha, "Senha do banco é obrigatória");
    }

    public static ConnectionFactory doAmbiente() {
        return new ConnectionFactory(
            System.getenv("DB_URL"),
            System.getenv("DB_USER"),
            System.getenv("DB_PASSWORD")
        );
    }

    @Override
    public Connection obterConexao() throws SQLException {
        carregarDriver();
        return DriverManager.getConnection(url, usuario, senha);
    }

    private void carregarDriver() {
        try {
            Class.forName(DRIVER_POSTGRESQL);
        } catch (ClassNotFoundException excecao) {
            throw new IllegalStateException("Driver JDBC do PostgreSQL nao encontrado", excecao);
        }
    }

    private static String exigirValor(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(nome + " é obrigatório(a)");
        }
        return valor.trim();
    }
}
