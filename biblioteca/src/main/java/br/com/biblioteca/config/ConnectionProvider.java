package br.com.biblioteca.config;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionProvider {
    Connection obterConexao() throws SQLException;
}
