package br.com.biblioteca.model;

import java.time.OffsetDateTime;

public class Livro {
    private Long id;
    private String titulo;
    private String autor;
    private String categoria;
    private String isbn;
    private int anoPublicacao;
    private int quantidadeExemplares;
    private StatusLivro status;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public Livro() {
    }

    public Livro(
        String titulo,
        String autor,
        String categoria,
        String isbn,
        int anoPublicacao,
        int quantidadeExemplares,
        StatusLivro status
    ) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeExemplares = quantidadeExemplares;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public int getQuantidadeExemplares() {
        return quantidadeExemplares;
    }

    public void setQuantidadeExemplares(int quantidadeExemplares) {
        this.quantidadeExemplares = quantidadeExemplares;
    }

    public StatusLivro getStatus() {
        return status;
    }

    public void setStatus(StatusLivro status) {
        this.status = status;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(OffsetDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
