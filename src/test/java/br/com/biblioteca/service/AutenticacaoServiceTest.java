package br.com.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import br.com.biblioteca.dao.UsuarioDAO;
import br.com.biblioteca.model.PerfilUsuario;
import br.com.biblioteca.model.Usuario;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {
    @Mock
    private UsuarioDAO usuarioDAO;

    private AutenticacaoService service;

    @BeforeEach
    void preparar() {
        service = new AutenticacaoService(usuarioDAO);
    }

    @Test
    void deveAutenticarSenhaComHashPbkdf2() {
        String hash = service.gerarHash("uma-senha-segura");
        when(usuarioDAO.buscarPorEmail("admin@boaleitura.local")).thenReturn(Optional.of(
            new Usuario(1L, "Administrador", "admin@boaleitura.local", hash, PerfilUsuario.ADMIN, true)
        ));

        var usuario = service.autenticar(" ADMIN@BOALEITURA.LOCAL ", "uma-senha-segura");

        assertTrue(usuario.isPresent());
        assertEquals(PerfilUsuario.ADMIN, usuario.get().getPerfil());
        assertTrue(usuario.get().isAdministrador());
    }

    @Test
    void deveRejeitarSenhaIncorretaEUsuarioInativo() {
        String hash = service.gerarHash("uma-senha-segura");
        when(usuarioDAO.buscarPorEmail("leitor@boaleitura.local")).thenReturn(Optional.of(
            new Usuario(2L, "Leitor", "leitor@boaleitura.local", hash, PerfilUsuario.USUARIO, false)
        ));

        assertFalse(service.autenticar("leitor@boaleitura.local", "senha-errada").isPresent());
        assertFalse(service.autenticar("leitor@boaleitura.local", "uma-senha-segura").isPresent());
    }

    @Test
    void deveGerarHashNaoReversivel() {
        String hash = service.gerarHash("uma-senha-segura");

        assertFalse(hash.contains("uma-senha-segura"));
        assertTrue(hash.startsWith("pbkdf2-sha256$"));
    }
}
