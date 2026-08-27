package br.com.biblioteca.model;

import java.text.Normalizer;
import java.util.Locale;

public enum StatusLivro {
    DISPONIVEL("Disponível"),
    EMPRESTADO("Emprestado"),
    RESERVADO("Reservado");

    private final String descricao;

    StatusLivro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getClasseCss() {
        return "status--" + name().toLowerCase(Locale.ROOT);
    }

    public static StatusLivro deTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Status do livro é obrigatório");
        }

        String valorNormalizado = Normalizer
            .normalize(texto.trim(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replace(' ', '_')
            .toUpperCase(Locale.ROOT);

        try {
            return StatusLivro.valueOf(valorNormalizado);
        } catch (IllegalArgumentException excecao) {
            throw new IllegalArgumentException("Status do livro inválido: " + texto, excecao);
        }
    }
}
