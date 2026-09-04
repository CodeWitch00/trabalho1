package br.com.biblioteca.model;

import java.io.Serializable;

public final class UsuarioSessao implements Serializable {
    private final long id;
    private final String nome;
    private final PerfilUsuario perfil;

    public UsuarioSessao(long id, String nome, PerfilUsuario perfil) {
        this.id = id;
        this.nome = nome;
        this.perfil = perfil;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public boolean isAdministrador() {
        return perfil == PerfilUsuario.ADMIN;
    }
}
