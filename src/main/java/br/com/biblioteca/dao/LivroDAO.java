package br.com.biblioteca.dao;

import br.com.biblioteca.model.Livro;
import java.util.List;
import java.util.Optional;

public interface LivroDAO {
    Livro inserir(Livro livro);

    List<Livro> listar();

    Optional<Livro> buscarPorId(long id);

    List<Livro> pesquisar(String termo);

    boolean existePorIsbn(String isbn, Long idIgnorado);

    boolean atualizar(Livro livro);

    boolean excluir(long id);
}
