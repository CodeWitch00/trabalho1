package br.com.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.exception.LivroNaoEncontradoException;
import br.com.biblioteca.exception.ValidacaoException;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.StatusLivro;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {
    private static final Clock RELOGIO_FIXO = Clock.fixed(
        Instant.parse("2026-08-26T12:00:00Z"),
        ZoneOffset.UTC
    );

    @Mock
    private LivroDAO livroDAO;

    private LivroService livroService;

    @BeforeEach
    void preparar() {
        livroService = new LivroService(livroDAO, RELOGIO_FIXO);
    }

    @Test
    void deveNormalizarECadastrarLivroValido() {
        Livro livro = livroValido();
        livro.setTitulo("  Dom Casmurro  ");
        livro.setAutor("  Machado de Assis ");
        livro.setCategoria(" Literatura  ");
        livro.setIsbn("978-0-30640-615-7");
        when(livroDAO.inserir(livro)).thenReturn(livro);

        Livro cadastrado = livroService.cadastrar(livro);

        assertAll(
            () -> assertSame(livro, cadastrado),
            () -> assertEquals("Dom Casmurro", livro.getTitulo()),
            () -> assertEquals("Machado de Assis", livro.getAutor()),
            () -> assertEquals("Literatura", livro.getCategoria()),
            () -> assertEquals("9780306406157", livro.getIsbn())
        );
        verify(livroDAO).existePorIsbn("9780306406157", null);
        verify(livroDAO).inserir(livro);
    }

    @Test
    void deveTransformarIsbnVazioEmNulo() {
        Livro livro = livroValido();
        livro.setIsbn("  ");
        when(livroDAO.inserir(livro)).thenReturn(livro);

        livroService.cadastrar(livro);

        assertNull(livro.getIsbn());
        verify(livroDAO, never()).existePorIsbn(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveAceitarIsbn10ComDigitoX() {
        Livro livro = livroValido();
        livro.setIsbn("0-8044-2957-X");
        when(livroDAO.inserir(livro)).thenReturn(livro);

        livroService.cadastrar(livro);

        assertEquals("080442957X", livro.getIsbn());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "9780306406158", "0804429571", "ABCDEFGHIJKLM"})
    void deveRejeitarIsbnInvalido(String isbn) {
        Livro livro = livroValido();
        livro.setIsbn(isbn);

        ValidacaoException excecao = assertThrows(
            ValidacaoException.class,
            () -> livroService.cadastrar(livro)
        );

        assertEquals("O ISBN informado é inválido", excecao.getErros().get("isbn"));
        verify(livroDAO, never()).inserir(livro);
    }

    @Test
    void deveReunirErrosDeCamposObrigatoriosELimites() {
        Livro livro = new Livro(" ", null, "", null, 2027, -1, null);

        ValidacaoException excecao = assertThrows(
            ValidacaoException.class,
            () -> livroService.cadastrar(livro)
        );

        assertAll(
            () -> assertEquals("O título é obrigatório", excecao.getErros().get("titulo")),
            () -> assertEquals("O autor é obrigatório", excecao.getErros().get("autor")),
            () -> assertEquals("A categoria é obrigatória", excecao.getErros().get("categoria")),
            () -> assertEquals(
                "O ano não pode ser posterior ao ano atual",
                excecao.getErros().get("anoPublicacao")
            ),
            () -> assertEquals(
                "A quantidade não pode ser negativa",
                excecao.getErros().get("quantidadeExemplares")
            ),
            () -> assertEquals("O status é obrigatório", excecao.getErros().get("status"))
        );
        verify(livroDAO, never()).inserir(livro);
    }

    @Test
    void deveRejeitarAnoAnteriorAoMinimo() {
        Livro livro = livroValido();
        livro.setAnoPublicacao(1499);

        ValidacaoException excecao = assertThrows(
            ValidacaoException.class,
            () -> livroService.cadastrar(livro)
        );

        assertEquals(
            "O ano deve ser igual ou posterior a 1500",
            excecao.getErros().get("anoPublicacao")
        );
    }

    @Test
    void deveRejeitarLivroDisponivelSemExemplares() {
        Livro livro = livroValido();
        livro.setQuantidadeExemplares(0);

        ValidacaoException excecao = assertThrows(
            ValidacaoException.class,
            () -> livroService.cadastrar(livro)
        );

        assertEquals(
            "Um livro disponível deve possuir pelo menos um exemplar",
            excecao.getErros().get("quantidadeExemplares")
        );
    }

    @Test
    void deveRejeitarIsbnDuplicado() {
        Livro livro = livroValido();
        when(livroDAO.existePorIsbn("9780306406157", null)).thenReturn(true);

        ValidacaoException excecao = assertThrows(
            ValidacaoException.class,
            () -> livroService.cadastrar(livro)
        );

        assertEquals(
            "Já existe um livro cadastrado com este ISBN",
            excecao.getErros().get("isbn")
        );
        verify(livroDAO, never()).inserir(livro);
    }

    @Test
    void deveAtualizarLivroExistenteIgnorandoSeuProprioIsbn() {
        Livro livro = livroValido();
        livro.setId(7L);
        when(livroDAO.buscarPorId(7L)).thenReturn(Optional.of(livro));
        when(livroDAO.atualizar(livro)).thenReturn(true);

        Livro atualizado = livroService.atualizar(livro);

        assertSame(livro, atualizado);
        verify(livroDAO).existePorIsbn("9780306406157", 7L);
        verify(livroDAO).atualizar(livro);
    }

    @Test
    void deveRejeitarAtualizacaoDeLivroInexistente() {
        Livro livro = livroValido();
        livro.setId(99L);
        when(livroDAO.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(LivroNaoEncontradoException.class, () -> livroService.atualizar(livro));

        verify(livroDAO, never()).atualizar(livro);
    }

    @Test
    void deveExcluirLivroExistente() {
        Livro livro = livroValido();
        livro.setId(3L);
        when(livroDAO.buscarPorId(3L)).thenReturn(Optional.of(livro));
        when(livroDAO.excluir(3L)).thenReturn(true);

        livroService.excluir(3L);

        verify(livroDAO).excluir(3L);
    }

    @Test
    void deveRejeitarExclusaoDeLivroInexistente() {
        when(livroDAO.buscarPorId(50L)).thenReturn(Optional.empty());

        assertThrows(LivroNaoEncontradoException.class, () -> livroService.excluir(50L));

        verify(livroDAO, never()).excluir(50L);
    }

    @Test
    void deveListarQuandoPesquisaEstiverVazia() {
        List<Livro> livros = List.of(livroValido());
        when(livroDAO.listar()).thenReturn(livros);

        assertSame(livros, livroService.pesquisar("  "));

        verify(livroDAO).listar();
        verify(livroDAO, never()).pesquisar(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void deveRemoverEspacosDoTermoDePesquisa() {
        List<Livro> livros = List.of(livroValido());
        when(livroDAO.pesquisar("Machado")).thenReturn(livros);

        assertSame(livros, livroService.pesquisar("  Machado  "));

        verify(livroDAO).pesquisar("Machado");
    }

    private Livro livroValido() {
        return new Livro(
            "Dom Casmurro",
            "Machado de Assis",
            "Literatura",
            "9780306406157",
            1899,
            4,
            StatusLivro.DISPONIVEL
        );
    }
}
