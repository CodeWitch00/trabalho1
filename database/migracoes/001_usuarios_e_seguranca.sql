-- Migração para bases que já possuem a tabela livro.
-- Execute uma única vez, antes de publicar a versão com autenticação.

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

COMMIT;
