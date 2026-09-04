package br.com.biblioteca.model;

public record Usuario(
    long id,
    String nome,
    String email,
    String senhaHash,
    PerfilUsuario perfil,
    boolean ativo
) {
}
