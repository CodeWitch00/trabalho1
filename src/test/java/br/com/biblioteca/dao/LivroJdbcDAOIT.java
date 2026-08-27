package br.com.biblioteca.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.biblioteca.config.ConnectionFactory;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.StatusLivro;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "test.db.url", matches = ".+")
class LivroJdbcDAOIT {
    private LivroJdbcDAO livroDAO;

    @BeforeEach
    void preparar() {
        ConnectionFactory connectionFactory = new ConnectionFactory(
            System.getProperty("test.db.url"),
            System.getProperty("test.db.user"),
            System.getProperty("test.db.password")
        );
        livroDAO = new LivroJdbcDAO(connectionFactory);
    }

    @Test
    void deveExecutarCrudCompletoEMapearOsDados() {
        Livro livro = new Livro(
            "Algoritmos",
            "Thomas H. Cormen",
            "Tecnologia",
            "9780262046305",
            2022,
            2,
            StatusLivro.DISPONIVEL
        );

        livroDAO.inserir(livro);

        assertNotNull(livro.getId());
        assertNotNull(livro.getCriadoEm());
        assertNotNull(livro.getAtualizadoEm());
        assertTrue(livroDAO.existePorIsbn("9780262046305", null));
        assertFalse(livroDAO.existePorIsbn("9780262046305", livro.getId()));

        Livro encontrado = livroDAO.buscarPorId(livro.getId()).orElseThrow();
        assertEquals("Algoritmos", encontrado.getTitulo());
        assertEquals(StatusLivro.DISPONIVEL, encontrado.getStatus());
        assertEquals(2, encontrado.getQuantidadeExemplares());

        List<Livro> pesquisa = livroDAO.pesquisar("cOrMeN");
        assertTrue(pesquisa.stream().anyMatch(item -> item.getId().equals(livro.getId())));

        livro.setTitulo("Algoritmos — Edição atualizada");
        livro.setQuantidadeExemplares(3);
        assertTrue(livroDAO.atualizar(livro));

        Livro atualizado = livroDAO.buscarPorId(livro.getId()).orElseThrow();
        assertEquals("Algoritmos — Edição atualizada", atualizado.getTitulo());
        assertEquals(3, atualizado.getQuantidadeExemplares());

        assertTrue(livroDAO.excluir(livro.getId()));
        assertTrue(livroDAO.buscarPorId(livro.getId()).isEmpty());
        assertFalse(livroDAO.excluir(livro.getId()));
    }

    @Test
    void deveListarACargaInicialOrdenadaPorTitulo() {
        List<Livro> livros = livroDAO.listar();

        assertEquals(15, livros.size());
        for (int indice = 1; indice < livros.size(); indice++) {
            String anterior = livros.get(indice - 1).getTitulo();
            String atual = livros.get(indice).getTitulo();
            assertTrue(anterior.compareTo(atual) <= 0);
        }
    }
}
