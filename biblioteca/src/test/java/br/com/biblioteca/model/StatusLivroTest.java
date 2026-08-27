package br.com.biblioteca.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StatusLivroTest {

    @Test
    void deveFornecerDescricoesEmPortugues() {
        assertAll(
            () -> assertEquals("Disponível", StatusLivro.DISPONIVEL.getDescricao()),
            () -> assertEquals("Emprestado", StatusLivro.EMPRESTADO.getDescricao()),
            () -> assertEquals("Reservado", StatusLivro.RESERVADO.getDescricao())
        );
    }

    @Test
    void deveConverterValoresDoFormularioEDoBanco() {
        assertAll(
            () -> assertEquals(StatusLivro.DISPONIVEL, StatusLivro.deTexto("Disponível")),
            () -> assertEquals(StatusLivro.DISPONIVEL, StatusLivro.deTexto("DISPONIVEL")),
            () -> assertEquals(StatusLivro.EMPRESTADO, StatusLivro.deTexto(" emprestado ")),
            () -> assertEquals(StatusLivro.RESERVADO, StatusLivro.deTexto("Reservado"))
        );
    }

    @Test
    void deveFornecerClasseCss() {
        assertEquals("status--disponivel", StatusLivro.DISPONIVEL.getClasseCss());
    }

    @Test
    void deveRejeitarStatusAusenteOuDesconhecido() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> StatusLivro.deTexto(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> StatusLivro.deTexto("  ")),
            () -> assertThrows(IllegalArgumentException.class, () -> StatusLivro.deTexto("Danificado"))
        );
    }
}
