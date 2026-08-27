package br.com.biblioteca.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ConnectionFactoryTest {

    @Test
    void deveRejeitarConfiguracaoAusente() {
        assertAll(
            () -> assertThrows(
                IllegalStateException.class,
                () -> new ConnectionFactory(null, "usuario", "senha")
            ),
            () -> assertThrows(
                IllegalStateException.class,
                () -> new ConnectionFactory("  ", "usuario", "senha")
            ),
            () -> assertThrows(
                IllegalStateException.class,
                () -> new ConnectionFactory("jdbc:postgresql://localhost/banco", " ", "senha")
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> new ConnectionFactory("jdbc:postgresql://localhost/banco", "usuario", null)
            )
        );
    }
}
