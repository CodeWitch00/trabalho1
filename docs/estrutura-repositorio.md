# Estrutura do Repositorio

Este documento mostra onde cada parte do sistema fica depois da reorganizacao
do repositorio e qual papel ela cumpre na arquitetura MVC.

## Visao geral

```text
.
|-- .env.example
|-- mise.toml
|-- pom.xml
|-- README.md
|-- docs/
|   |-- descricao/
|   |-- relatorio/
|   |-- estrategia-desenvolvimento.md
|   `-- estrutura-repositorio.md
|-- database/
|-- scripts/
`-- src/
```

## Aplicacao

```text
src/main/
|-- java/br/com/biblioteca/
|   |-- config/
|   |-- controller/
|   |-- dao/
|   |-- exception/
|   |-- model/
|   `-- service/
`-- webapp/
    |-- index.html
    |-- css/
    |-- js/
    `-- WEB-INF/
        |-- views/
        `-- web.xml
```

## Papel de cada camada

- `webapp/`: camada View. Contem HTML, CSS, JavaScript e JSPs.
- `controller/`: camada Controller. Contem os Servlets que recebem HTTP.
- `service/`: regras de negocio, validacoes e orquestracao dos casos de uso.
- `model/`: entidades do dominio, como `Livro`.
- `dao/`: acesso ao banco via JDBC e SQL parametrizado.
- `config/`: criacao de conexoes e configuracoes compartilhadas.
- `exception/`: erros especificos do dominio, validacao e persistencia.

## Banco de dados

```text
database/
|-- schema.sql
|-- dados-iniciais.sql
|-- teste-schema.sql
`-- README.md
```

- `schema.sql`: estrutura da tabela e restricoes.
- `dados-iniciais.sql`: carga inicial dos 15 livros.
- `teste-schema.sql`: validacoes SQL do schema e das regras de banco.
- `README.md`: instrucoes especificas do banco.

## Testes

```text
src/test/
|-- java/br/com/biblioteca/
`-- resources/
```

A estrategia segue a piramide de testes:

- muitos testes unitarios para Model e Service;
- testes de integracao para DAO/JDBC e banco;
- testes de Controller para validar os fluxos HTTP principais.

## Estado funcional

O CRUD de livros ja esta implementado na rota `/livros`, com Controller,
Service, DAO/JDBC, JSPs em `WEB-INF/views` e PostgreSQL. A proxima evolucao
funcional prevista e o Trabalho 5: autenticacao, autorizacao e testes de
seguranca.

## Regra pratica

O navegador nunca acessa o PostgreSQL diretamente. O fluxo correto e:

```text
View -> Controller -> Service -> DAO -> PostgreSQL
```

Essa separacao deixa claro o uso do MVC e atende ao requisito de tratar
requisicoes HTTP no servidor com persistencia relacional.
