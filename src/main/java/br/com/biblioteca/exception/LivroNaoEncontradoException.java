package br.com.biblioteca.exception;

public class LivroNaoEncontradoException extends RuntimeException {

    public LivroNaoEncontradoException(long id) {
        super("Livro não encontrado: " + id);
    }
}
