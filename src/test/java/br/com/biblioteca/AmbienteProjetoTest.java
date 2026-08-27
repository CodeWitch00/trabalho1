package br.com.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AmbienteProjetoTest {

    @Test
    void deveExecutarComJava17() {
        assertEquals(17, Runtime.version().feature());
    }
}
