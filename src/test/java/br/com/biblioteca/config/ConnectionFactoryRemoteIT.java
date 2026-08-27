package br.com.biblioteca.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "test.remote", matches = "true")
class ConnectionFactoryRemoteIT {

    @Test
    void deveConectarAoSupabaseSomenteParaLeitura() throws Exception {
        ConnectionFactory connectionFactory = ConnectionFactory.doAmbiente();

        try (
            Connection conexao = connectionFactory.obterConexao();
            Statement consulta = conexao.createStatement()
        ) {
            conexao.setReadOnly(true);

            try (ResultSet resultado = consulta.executeQuery("SELECT version()")) {
                assertTrue(resultado.next());
                assertTrue(resultado.getString(1).contains("PostgreSQL 17.6"));
            }

            try (ResultSet resultado = consulta.executeQuery("SELECT COUNT(*) FROM livro")) {
                assertTrue(resultado.next());
                assertEquals(15, resultado.getInt(1));
            }
        }
    }
}
