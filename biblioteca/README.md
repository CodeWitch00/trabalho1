# Sistema de Gestao de Biblioteca

Aplicacao web academica para gestao de biblioteca, organizada em MVC e
preparada para CRUD de livros com Java, Servlets, JDBC e PostgreSQL.

## Objetivo

Transformar a interface web existente em uma aplicacao funcional, integrando:

- front-end com HTML, CSS e JavaScript;
- camada Controller para requisicoes e respostas HTTP;
- camada Service para regras de negocio;
- camada DAO/JDBC para acesso ao banco;
- persistencia em PostgreSQL.

## Tecnologias

- Java 17
- Jakarta Servlet 6
- JSP/JSTL, em etapa posterior de View dinamica
- JDBC
- PostgreSQL 17.6 no Supabase
- Maven 3.9
- Apache Tomcat 10.1
- mise
- HTML, CSS e JavaScript vanilla
- JUnit 5 e Mockito

## Estrutura MVC

```text
biblioteca/
|-- pom.xml
|-- database/
|   |-- schema.sql
|   |-- dados-iniciais.sql
|   `-- teste-schema.sql
|-- scripts/
|   `-- testar-banco.sh
`-- src/
    |-- main/
    |   |-- java/br/com/biblioteca/
    |   |   |-- config/       # configuracao de conexao
    |   |   |-- controller/   # Servlets HTTP (proxima etapa)
    |   |   |-- dao/          # persistencia com JDBC
    |   |   |-- exception/    # excecoes da aplicacao
    |   |   |-- model/        # entidades do dominio
    |   |   `-- service/      # regras de negocio
    |   `-- webapp/
    |       |-- index.html    # View estatica atual
    |       |-- css/
    |       |-- js/
    |       `-- WEB-INF/
    |           |-- views/    # JSPs protegidas (proxima etapa)
    |           `-- web.xml
    `-- test/
        |-- java/br/com/biblioteca/
        `-- resources/
```

## Funcionalidades atuais

- **Ficha 1 — Cadastro**: formulário para registrar um novo livro
  (título, autor(a), categoria, ISBN, ano, exemplares e status), com
  validação nativa do HTML5 e validação complementar em JavaScript
  (mensagens de erro específicas por campo, exibidas em tempo real).
- **Ficha 2 — Acervo**: tabela semântica com os livros cadastrados,
  incluindo dados de exemplo, busca por título/autor(a)/categoria e
  contador de resultados.
- **Ficha 3 — Leitor**: formulário de cadastro de leitores(as), com
  busca automática de endereço a partir do CEP (requisição assíncrona
  à API pública ViaCEP), sem recarregar a página.
- **Ficha 4 — Resumo**: indicadores do acervo total (livros, disponíveis,
  emprestados, reservados, categorias e autores).
- Alternância entre tema claro e tema escuro, com preferência salva no
  navegador.

## Como executar

Na fase atual, o projeto ja possui base Maven Web, Model, Service, DAO/JDBC e
testes automatizados. A View ainda esta em HTML estatico e sera integrada aos
Controllers na proxima etapa.

Verificar ferramentas:

```bash
mise run versions
```

Executar testes unitarios:

```bash
mise run test
```

Executar testes com PostgreSQL local temporario:

```bash
mise run test-db
```

Gerar o arquivo WAR:

```bash
mise run package
```

## Variaveis de ambiente

As credenciais do banco nao devem ser versionadas. Use `.env.example` como
referencia para criar `.env` na raiz do repositorio:

```text
DB_URL=jdbc:postgresql://HOST_DO_POOLER:5432/postgres?sslmode=require
DB_USER=postgres.REFERENCIA_DO_PROJETO
DB_PASSWORD=SUA_SENHA_DO_BANCO
```

## Documentacao

A estrategia geral esta em `../docs/estrategia-desenvolvimento.md`.
A organizacao do repositorio esta em `../docs/estrutura-repositorio.md`.
