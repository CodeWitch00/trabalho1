package br.com.biblioteca.service;

import br.com.biblioteca.dao.UsuarioDAO;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.model.UsuarioSessao;
import java.util.Locale;
import java.util.Optional;

public class AutenticacaoService {
    private static final String HASH_FICTICIO = "pbkdf2-sha256$210000$AAAAAAAAAAAAAAAAAAAAAA==$AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
    private final UsuarioDAO usuarioDAO;

    public AutenticacaoService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Optional<UsuarioSessao> autenticar(String email, String senha) {
        String emailNormalizado = normalizarEmail(email);
        if (emailNormalizado == null || senha == null || senha.isEmpty()) {
            HashSenha.verificar(senha == null ? "" : senha, HASH_FICTICIO);
            return Optional.empty();
        }

        Optional<Usuario> usuario = usuarioDAO.buscarPorEmail(emailNormalizado);
        String hash = usuario.map(Usuario::senhaHash).orElse(HASH_FICTICIO);
        boolean senhaValida = HashSenha.verificar(senha, hash);
        if (usuario.isEmpty() || !usuario.get().ativo() || !senhaValida) {
            return Optional.empty();
        }

        Usuario autenticado = usuario.get();
        return Optional.of(new UsuarioSessao(
            autenticado.id(), autenticado.nome(), autenticado.perfil()
        ));
    }

    public String gerarHash(String senha) {
        if (senha == null || senha.length() < 12) {
            throw new IllegalArgumentException("A senha deve possuir ao menos 12 caracteres");
        }
        return HashSenha.gerar(senha);
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
