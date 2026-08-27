package br.com.biblioteca.dao;

import br.com.biblioteca.config.ConnectionProvider;
import br.com.biblioteca.exception.PersistenciaException;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.StatusLivro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LivroJdbcDAO implements LivroDAO {
    private static final String COLUNAS = """
        id, titulo, autor, categoria, isbn, ano_publicacao,
        quantidade_exemplares, status, criado_em, atualizado_em
        """;

    private final ConnectionProvider connectionProvider;

    public LivroJdbcDAO(ConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(
            connectionProvider,
            "Provedor de conexão é obrigatório"
        );
    }

    @Override
    public Livro inserir(Livro livro) {
        String sql = """
            INSERT INTO livro (
                titulo, autor, categoria, isbn, ano_publicacao,
                quantidade_exemplares, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id, criado_em, atualizado_em
            """;

        try (
            Connection conexao = connectionProvider.obterConexao();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            preencherDados(comando, livro);
            try (ResultSet resultado = comando.executeQuery()) {
                if (!resultado.next()) {
                    throw new PersistenciaException("O banco não retornou o livro inserido", null);
                }
                livro.setId(resultado.getLong("id"));
                livro.setCriadoEm(resultado.getObject("criado_em", OffsetDateTime.class));
                livro.setAtualizadoEm(resultado.getObject("atualizado_em", OffsetDateTime.class));
                return livro;
            }
        } catch (SQLException excecao) {
            throw new PersistenciaException("Não foi possível cadastrar o livro", excecao);
        }
    }

    @Override
    public List<Livro> listar() {
        String sql = "SELECT " + COLUNAS + " FROM livro ORDER BY titulo, id";
        return consultarLista(sql, null);
    }

    @Override
    public Optional<Livro> buscarPorId(long id) {
        String sql = "SELECT " + COLUNAS + " FROM livro WHERE id = ?";

        try (
            Connection conexao = connectionProvider.obterConexao();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            comando.setLong(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next()
                    ? Optional.of(mapearLivro(resultado))
                    : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw new PersistenciaException("Não foi possível consultar o livro", excecao);
        }
    }

    @Override
    public List<Livro> pesquisar(String termo) {
        String sql = """
            SELECT %s
              FROM livro
             WHERE titulo ILIKE ?
                OR autor ILIKE ?
                OR categoria ILIKE ?
             ORDER BY titulo, id
            """.formatted(COLUNAS);
        return consultarLista(sql, "%" + termo + "%");
    }

    @Override
    public boolean existePorIsbn(String isbn, Long idIgnorado) {
        String sql = idIgnorado == null
            ? "SELECT EXISTS (SELECT 1 FROM livro WHERE isbn = ?)"
            : "SELECT EXISTS (SELECT 1 FROM livro WHERE isbn = ? AND id <> ?)";

        try (
            Connection conexao = connectionProvider.obterConexao();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            comando.setString(1, isbn);
            if (idIgnorado != null) {
                comando.setLong(2, idIgnorado);
            }
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() && resultado.getBoolean(1);
            }
        } catch (SQLException excecao) {
            throw new PersistenciaException("Não foi possível verificar o ISBN", excecao);
        }
    }

    @Override
    public boolean atualizar(Livro livro) {
        String sql = """
            UPDATE livro
               SET titulo = ?,
                   autor = ?,
                   categoria = ?,
                   isbn = ?,
                   ano_publicacao = ?,
                   quantidade_exemplares = ?,
                   status = ?,
                   atualizado_em = CURRENT_TIMESTAMP
             WHERE id = ?
         RETURNING atualizado_em
            """;

        try (
            Connection conexao = connectionProvider.obterConexao();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            preencherDados(comando, livro);
            comando.setLong(8, livro.getId());
            try (ResultSet resultado = comando.executeQuery()) {
                if (!resultado.next()) {
                    return false;
                }
                livro.setAtualizadoEm(resultado.getObject("atualizado_em", OffsetDateTime.class));
                return true;
            }
        } catch (SQLException excecao) {
            throw new PersistenciaException("Não foi possível atualizar o livro", excecao);
        }
    }

    @Override
    public boolean excluir(long id) {
        String sql = "DELETE FROM livro WHERE id = ?";

        try (
            Connection conexao = connectionProvider.obterConexao();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            comando.setLong(1, id);
            return comando.executeUpdate() == 1;
        } catch (SQLException excecao) {
            throw new PersistenciaException("Não foi possível excluir o livro", excecao);
        }
    }

    private List<Livro> consultarLista(String sql, String filtro) {
        try (
            Connection conexao = connectionProvider.obterConexao();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            if (filtro != null) {
                comando.setString(1, filtro);
                comando.setString(2, filtro);
                comando.setString(3, filtro);
            }

            try (ResultSet resultado = comando.executeQuery()) {
                List<Livro> livros = new ArrayList<>();
                while (resultado.next()) {
                    livros.add(mapearLivro(resultado));
                }
                return livros;
            }
        } catch (SQLException excecao) {
            throw new PersistenciaException("Não foi possível listar os livros", excecao);
        }
    }

    private void preencherDados(PreparedStatement comando, Livro livro) throws SQLException {
        comando.setString(1, livro.getTitulo());
        comando.setString(2, livro.getAutor());
        comando.setString(3, livro.getCategoria());
        if (livro.getIsbn() == null) {
            comando.setNull(4, Types.VARCHAR);
        } else {
            comando.setString(4, livro.getIsbn());
        }
        comando.setInt(5, livro.getAnoPublicacao());
        comando.setInt(6, livro.getQuantidadeExemplares());
        comando.setString(7, livro.getStatus().name());
    }

    private Livro mapearLivro(ResultSet resultado) throws SQLException {
        Livro livro = new Livro();
        livro.setId(resultado.getLong("id"));
        livro.setTitulo(resultado.getString("titulo"));
        livro.setAutor(resultado.getString("autor"));
        livro.setCategoria(resultado.getString("categoria"));
        livro.setIsbn(resultado.getString("isbn"));
        livro.setAnoPublicacao(resultado.getInt("ano_publicacao"));
        livro.setQuantidadeExemplares(resultado.getInt("quantidade_exemplares"));
        livro.setStatus(StatusLivro.deTexto(resultado.getString("status")));
        livro.setCriadoEm(resultado.getObject("criado_em", OffsetDateTime.class));
        livro.setAtualizadoEm(resultado.getObject("atualizado_em", OffsetDateTime.class));
        return livro;
    }
}
