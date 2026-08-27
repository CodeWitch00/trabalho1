package br.com.biblioteca.service;

import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.exception.LivroNaoEncontradoException;
import br.com.biblioteca.exception.ValidacaoException;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.StatusLivro;
import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LivroService {
    private static final int ANO_MINIMO = 1500;

    private final LivroDAO livroDAO;
    private final Clock relogio;

    public LivroService(LivroDAO livroDAO) {
        this(livroDAO, Clock.systemDefaultZone());
    }

    LivroService(LivroDAO livroDAO, Clock relogio) {
        this.livroDAO = Objects.requireNonNull(livroDAO, "LivroDAO é obrigatório");
        this.relogio = Objects.requireNonNull(relogio, "Relógio é obrigatório");
    }

    public Livro cadastrar(Livro livro) {
        validarENormalizar(livro, null);
        return livroDAO.inserir(livro);
    }

    public List<Livro> listar() {
        return livroDAO.listar();
    }

    public Livro buscarPorId(long id) {
        validarId(id);
        return livroDAO.buscarPorId(id)
            .orElseThrow(() -> new LivroNaoEncontradoException(id));
    }

    public List<Livro> pesquisar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listar();
        }
        return livroDAO.pesquisar(termo.trim());
    }

    public Livro atualizar(Livro livro) {
        if (livro == null || livro.getId() == null) {
            throw validacaoDeId();
        }

        long id = livro.getId();
        validarId(id);
        if (livroDAO.buscarPorId(id).isEmpty()) {
            throw new LivroNaoEncontradoException(id);
        }

        validarENormalizar(livro, id);
        if (!livroDAO.atualizar(livro)) {
            throw new LivroNaoEncontradoException(id);
        }
        return livro;
    }

    public void excluir(long id) {
        validarId(id);
        if (livroDAO.buscarPorId(id).isEmpty() || !livroDAO.excluir(id)) {
            throw new LivroNaoEncontradoException(id);
        }
    }

    private void validarENormalizar(Livro livro, Long idIgnorado) {
        Map<String, String> erros = new LinkedHashMap<>();

        if (livro == null) {
            erros.put("livro", "Os dados do livro são obrigatórios");
            throw new ValidacaoException(erros);
        }

        livro.setTitulo(normalizarTexto(livro.getTitulo()));
        livro.setAutor(normalizarTexto(livro.getAutor()));
        livro.setCategoria(normalizarTexto(livro.getCategoria()));
        livro.setIsbn(normalizarIsbn(livro.getIsbn()));

        validarObrigatorio("titulo", livro.getTitulo(), "O título é obrigatório", erros);
        validarObrigatorio("autor", livro.getAutor(), "O autor é obrigatório", erros);
        validarObrigatorio("categoria", livro.getCategoria(), "A categoria é obrigatória", erros);

        int anoAtual = LocalDate.now(relogio).getYear();
        if (livro.getAnoPublicacao() < ANO_MINIMO) {
            erros.put("anoPublicacao", "O ano deve ser igual ou posterior a " + ANO_MINIMO);
        } else if (livro.getAnoPublicacao() > anoAtual) {
            erros.put("anoPublicacao", "O ano não pode ser posterior ao ano atual");
        }

        if (livro.getQuantidadeExemplares() < 0) {
            erros.put("quantidadeExemplares", "A quantidade não pode ser negativa");
        }

        if (livro.getStatus() == null) {
            erros.put("status", "O status é obrigatório");
        } else if (
            livro.getStatus() == StatusLivro.DISPONIVEL
                && livro.getQuantidadeExemplares() == 0
        ) {
            erros.put(
                "quantidadeExemplares",
                "Um livro disponível deve possuir pelo menos um exemplar"
            );
        }

        if (livro.getIsbn() != null && !isbnValido(livro.getIsbn())) {
            erros.put("isbn", "O ISBN informado é inválido");
        }

        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }

        if (
            livro.getIsbn() != null
                && livroDAO.existePorIsbn(livro.getIsbn(), idIgnorado)
        ) {
            erros.put("isbn", "Já existe um livro cadastrado com este ISBN");
            throw new ValidacaoException(erros);
        }
    }

    private void validarId(long id) {
        if (id <= 0) {
            throw validacaoDeId();
        }
    }

    private ValidacaoException validacaoDeId() {
        return new ValidacaoException(Map.of("id", "O identificador do livro é inválido"));
    }

    private void validarObrigatorio(
        String campo,
        String valor,
        String mensagem,
        Map<String, String> erros
    ) {
        if (valor == null) {
            erros.put(campo, mensagem);
        }
    }

    private String normalizarTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private String normalizarIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }
        return isbn.replaceAll("[\\s-]", "").toUpperCase();
    }

    private boolean isbnValido(String isbn) {
        return isbn.length() == 10 ? isbn10Valido(isbn) : isbn13Valido(isbn);
    }

    private boolean isbn10Valido(String isbn) {
        if (!isbn.matches("\\d{9}[\\dX]")) {
            return false;
        }

        int soma = 0;
        for (int indice = 0; indice < 9; indice++) {
            soma += Character.digit(isbn.charAt(indice), 10) * (10 - indice);
        }

        int digitoVerificador = isbn.charAt(9) == 'X'
            ? 10
            : Character.digit(isbn.charAt(9), 10);
        soma += digitoVerificador;
        return soma % 11 == 0;
    }

    private boolean isbn13Valido(String isbn) {
        if (!isbn.matches("\\d{13}")) {
            return false;
        }

        int soma = 0;
        for (int indice = 0; indice < 12; indice++) {
            int digito = Character.digit(isbn.charAt(indice), 10);
            soma += digito * (indice % 2 == 0 ? 1 : 3);
        }

        int esperado = (10 - soma % 10) % 10;
        int informado = Character.digit(isbn.charAt(12), 10);
        return esperado == informado;
    }
}
