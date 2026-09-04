package br.com.biblioteca.dao;

import br.com.biblioteca.config.ConnectionProvider;
import br.com.biblioteca.exception.PersistenciaException;
import br.com.biblioteca.model.PerfilUsuario;
import br.com.biblioteca.model.Usuario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioJdbcDAO implements UsuarioDAO {
    private static final String BUSCAR_POR_EMAIL = """
        SELECT id, nome, email, senha_hash, perfil, ativo
          FROM usuario
         WHERE email = ?
        """;

    private final ConnectionProvider connectionProvider;

    public UsuarioJdbcDAO(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        try (var connection = connectionProvider.obterConexao();
             var statement = connection.prepareStatement(BUSCAR_POR_EMAIL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapear(resultSet)) : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw new PersistenciaException("Falha ao consultar usuario", excecao);
        }
    }

    private Usuario mapear(ResultSet resultSet) throws SQLException {
        return new Usuario(
            resultSet.getLong("id"),
            resultSet.getString("nome"),
            resultSet.getString("email"),
            resultSet.getString("senha_hash"),
            PerfilUsuario.valueOf(resultSet.getString("perfil")),
            resultSet.getBoolean("ativo")
        );
    }
}
