-- Teste de integração do esquema PostgreSQL.
-- Pré-condições: schema.sql e dados-iniciais.sql executados.
-- O ROLLBACK final impede que os dados deste teste sejam conservados.

BEGIN;

DO $teste$
DECLARE
    livro_id BIGINT;
    total_inicial INTEGER;
    valor_atualizado INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_inicial FROM livro;

    IF total_inicial <> 15 THEN
        RAISE EXCEPTION
            'Carga inicial inválida: esperados 15 livros, encontrados %',
            total_inicial;
    END IF;

    -- CREATE e geração automática do identificador.
    INSERT INTO livro (
        titulo,
        autor,
        categoria,
        isbn,
        ano_publicacao,
        quantidade_exemplares,
        status
    ) VALUES (
        'Livro de teste',
        'Autor de teste',
        'Tecnologia',
        '9781234567890',
        2020,
        2,
        'DISPONIVEL'
    ) RETURNING id INTO livro_id;

    IF livro_id IS NULL THEN
        RAISE EXCEPTION 'O PostgreSQL não gerou o identificador do livro';
    END IF;

    -- READ.
    IF NOT EXISTS (
        SELECT 1 FROM livro
        WHERE id = livro_id AND titulo = 'Livro de teste'
    ) THEN
        RAISE EXCEPTION 'O livro inserido não foi encontrado';
    END IF;

    -- UPDATE.
    UPDATE livro
       SET quantidade_exemplares = 3,
           atualizado_em = CURRENT_TIMESTAMP
     WHERE id = livro_id;

    SELECT quantidade_exemplares
      INTO valor_atualizado
      FROM livro
     WHERE id = livro_id;

    IF valor_atualizado <> 3 THEN
        RAISE EXCEPTION 'A atualização do livro não foi persistida';
    END IF;

    -- ISBN único.
    BEGIN
        INSERT INTO livro (
            titulo, autor, categoria, isbn, ano_publicacao,
            quantidade_exemplares, status
        ) VALUES (
            'ISBN repetido', 'Autor', 'Categoria', '9781234567890', 2020,
            1, 'DISPONIVEL'
        );
        RAISE EXCEPTION 'Um ISBN duplicado foi aceito';
    EXCEPTION
        WHEN unique_violation THEN NULL;
    END;

    -- Campos textuais obrigatórios e não vazios.
    BEGIN
        INSERT INTO livro (
            titulo, autor, categoria, ano_publicacao,
            quantidade_exemplares, status
        ) VALUES ('  ', 'Autor', 'Categoria', 2020, 1, 'DISPONIVEL');
        RAISE EXCEPTION 'Um título vazio foi aceito';
    EXCEPTION
        WHEN check_violation THEN NULL;
    END;

    BEGIN
        INSERT INTO livro (
            titulo, autor, categoria, ano_publicacao,
            quantidade_exemplares, status
        ) VALUES ('Título', '  ', 'Categoria', 2020, 1, 'DISPONIVEL');
        RAISE EXCEPTION 'Um autor vazio foi aceito';
    EXCEPTION
        WHEN check_violation THEN NULL;
    END;

    BEGIN
        INSERT INTO livro (
            titulo, autor, categoria, ano_publicacao,
            quantidade_exemplares, status
        ) VALUES ('Título', 'Autor', '  ', 2020, 1, 'DISPONIVEL');
        RAISE EXCEPTION 'Uma categoria vazia foi aceita';
    EXCEPTION
        WHEN check_violation THEN NULL;
    END;

    -- Limites numéricos e valores de status.
    BEGIN
        INSERT INTO livro (
            titulo, autor, categoria, ano_publicacao,
            quantidade_exemplares, status
        ) VALUES ('Título', 'Autor', 'Categoria', 1499, 1, 'DISPONIVEL');
        RAISE EXCEPTION 'Um ano anterior a 1500 foi aceito';
    EXCEPTION
        WHEN check_violation THEN NULL;
    END;

    BEGIN
        INSERT INTO livro (
            titulo, autor, categoria, ano_publicacao,
            quantidade_exemplares, status
        ) VALUES ('Título', 'Autor', 'Categoria', 2020, -1, 'EMPRESTADO');
        RAISE EXCEPTION 'Uma quantidade negativa foi aceita';
    EXCEPTION
        WHEN check_violation THEN NULL;
    END;

    BEGIN
        INSERT INTO livro (
            titulo, autor, categoria, ano_publicacao,
            quantidade_exemplares, status
        ) VALUES ('Título', 'Autor', 'Categoria', 2020, 1, 'DESCONHECIDO');
        RAISE EXCEPTION 'Um status desconhecido foi aceito';
    EXCEPTION
        WHEN check_violation THEN NULL;
    END;

    BEGIN
        INSERT INTO livro (
            titulo, autor, categoria, ano_publicacao,
            quantidade_exemplares, status
        ) VALUES ('Título', 'Autor', 'Categoria', 2020, 0, 'DISPONIVEL');
        RAISE EXCEPTION 'Um livro disponível sem exemplares foi aceito';
    EXCEPTION
        WHEN check_violation THEN NULL;
    END;

    -- ISBN nulo é permitido em mais de um livro.
    INSERT INTO livro (
        titulo, autor, categoria, isbn, ano_publicacao,
        quantidade_exemplares, status
    ) VALUES
        ('Sem ISBN 1', 'Autor', 'Categoria', NULL, 2020, 0, 'EMPRESTADO'),
        ('Sem ISBN 2', 'Autor', 'Categoria', NULL, 2020, 0, 'RESERVADO');

    -- DELETE.
    DELETE FROM livro WHERE id = livro_id;

    IF EXISTS (SELECT 1 FROM livro WHERE id = livro_id) THEN
        RAISE EXCEPTION 'O livro não foi excluído';
    END IF;
END
$teste$;

ROLLBACK;
