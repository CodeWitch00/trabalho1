-- Estrutura inicial do Sistema de Gestão de Biblioteca.
-- Execute este arquivo em um banco PostgreSQL vazio antes de dados-iniciais.sql.

BEGIN;

CREATE TABLE usuario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_usuario_nome CHECK (btrim(nome) <> ''),
    CONSTRAINT ck_usuario_email CHECK (btrim(email) <> ''),
    CONSTRAINT ck_usuario_senha_hash CHECK (senha_hash LIKE 'pbkdf2-sha256$%$%$%'),
    CONSTRAINT ck_usuario_perfil CHECK (perfil IN ('ADMIN', 'USUARIO'))
);

CREATE TABLE livro (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(200) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    isbn VARCHAR(13) UNIQUE,
    ano_publicacao SMALLINT NOT NULL,
    quantidade_exemplares INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_livro_titulo
        CHECK (btrim(titulo) <> ''),

    CONSTRAINT ck_livro_autor
        CHECK (btrim(autor) <> ''),

    CONSTRAINT ck_livro_categoria
        CHECK (btrim(categoria) <> ''),

    CONSTRAINT ck_livro_ano
        CHECK (ano_publicacao >= 1500),

    CONSTRAINT ck_livro_quantidade
        CHECK (quantidade_exemplares >= 0),

    CONSTRAINT ck_livro_status
        CHECK (status IN ('DISPONIVEL', 'EMPRESTADO', 'RESERVADO')),

    CONSTRAINT ck_livro_disponivel
        CHECK (status <> 'DISPONIVEL' OR quantidade_exemplares > 0)
);

COMMIT;
