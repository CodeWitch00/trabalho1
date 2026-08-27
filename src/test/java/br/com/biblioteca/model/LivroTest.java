package br.com.biblioteca.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class LivroTest {

    @Test
    void deveArmazenarAsPropriedadesDoLivro() {
        OffsetDateTime criadoEm = OffsetDateTime.parse("2026-08-26T20:00:00-03:00");
        OffsetDateTime atualizadoEm = OffsetDateTime.parse("2026-08-26T21:00:00-03:00");
        Livro livro = new Livro();

        livro.setId(10L);
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        livro.setCategoria("Literatura");
        livro.setIsbn("9788535902778");
        livro.setAnoPublicacao(1899);
        livro.setQuantidadeExemplares(4);
        livro.setStatus(StatusLivro.DISPONIVEL);
        livro.setCriadoEm(criadoEm);
        livro.setAtualizadoEm(atualizadoEm);

        assertAll(
            () -> assertEquals(10L, livro.getId()),
            () -> assertEquals("Dom Casmurro", livro.getTitulo()),
            () -> assertEquals("Machado de Assis", livro.getAutor()),
            () -> assertEquals("Literatura", livro.getCategoria()),
            () -> assertEquals("9788535902778", livro.getIsbn()),
            () -> assertEquals(1899, livro.getAnoPublicacao()),
            () -> assertEquals(4, livro.getQuantidadeExemplares()),
            () -> assertEquals(StatusLivro.DISPONIVEL, livro.getStatus()),
            () -> assertEquals(criadoEm, livro.getCriadoEm()),
            () -> assertEquals(atualizadoEm, livro.getAtualizadoEm())
        );
    }

    @Test
    void deveCriarLivroComConstrutorDeCadastro() {
        Livro livro = new Livro(
            "O Hobbit",
            "J.R.R. Tolkien",
            "Fantasia",
            null,
            1937,
            4,
            StatusLivro.DISPONIVEL
        );

        assertAll(
            () -> assertEquals("O Hobbit", livro.getTitulo()),
            () -> assertEquals("J.R.R. Tolkien", livro.getAutor()),
            () -> assertEquals("Fantasia", livro.getCategoria()),
            () -> assertEquals(1937, livro.getAnoPublicacao()),
            () -> assertEquals(4, livro.getQuantidadeExemplares()),
            () -> assertEquals(StatusLivro.DISPONIVEL, livro.getStatus())
        );
    }
}
