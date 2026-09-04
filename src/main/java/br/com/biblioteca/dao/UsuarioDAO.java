package br.com.biblioteca.dao;

import br.com.biblioteca.model.Usuario;
import java.util.Optional;

public interface UsuarioDAO {
    Optional<Usuario> buscarPorEmail(String email);
}
