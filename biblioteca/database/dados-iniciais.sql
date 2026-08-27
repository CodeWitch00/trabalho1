-- Carga inicial baseada nos 15 livros que já faziam parte do index.html.
-- Execute uma única vez, depois de schema.sql.

BEGIN;

INSERT INTO livro
    (titulo, autor, categoria, isbn, ano_publicacao, quantidade_exemplares, status)
VALUES
    ('Dom Casmurro', 'Machado de Assis', 'Literatura', NULL, 1899, 4, 'DISPONIVEL'),
    ('O Cortiço', 'Aluísio Azevedo', 'Literatura', NULL, 1890, 3, 'EMPRESTADO'),
    ('A Hora da Estrela', 'Clarice Lispector', 'Literatura', NULL, 1977, 2, 'RESERVADO'),
    ('Memórias Póstumas de Brás Cubas', 'Machado de Assis', 'Literatura', NULL, 1881, 3, 'DISPONIVEL'),
    ('Grande Sertão: Veredas', 'Guimarães Rosa', 'Literatura', NULL, 1956, 2, 'EMPRESTADO'),
    ('1984', 'George Orwell', 'Ficção', NULL, 1949, 5, 'DISPONIVEL'),
    ('O Hobbit', 'J.R.R. Tolkien', 'Fantasia', NULL, 1937, 4, 'DISPONIVEL'),
    ('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 'Infantil', NULL, 1943, 6, 'DISPONIVEL'),
    ('Sapiens', 'Yuval Noah Harari', 'Ciências Sociais', NULL, 2011, 3, 'EMPRESTADO'),
    ('Clean Code', 'Robert C. Martin', 'Tecnologia', NULL, 2008, 2, 'RESERVADO'),
    ('O Mundo de Sofia', 'Jostein Gaarder', 'Filosofia', NULL, 1991, 3, 'DISPONIVEL'),
    ('Uma Breve História do Tempo', 'Stephen Hawking', 'Ciência', NULL, 1988, 2, 'DISPONIVEL'),
    ('Vidas Secas', 'Graciliano Ramos', 'Literatura', NULL, 1938, 3, 'EMPRESTADO'),
    ('Admirável Mundo Novo', 'Aldous Huxley', 'Ficção', NULL, 1932, 2, 'DISPONIVEL'),
    ('Capitães da Areia', 'Jorge Amado', 'Literatura', NULL, 1937, 3, 'RESERVADO');

COMMIT;
