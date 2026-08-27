# Estrutura do Repositorio

Este documento mostra onde cada parte do sistema fica e qual papel ela cumpre
na arquitetura MVC.

## Visao geral

```text
Trabalho_AAP/
|-- .env.example
|-- mise.toml
|-- docs/
|   |-- descricao/
|   |-- relatorio/
|   |-- estrategia-desenvolvimento.md
|   `-- estrutura-repositorio.md
`-- biblioteca/
    |-- pom.xml
    |-- README.md
    |-- database/
    |-- scripts/
    `-- src/
```

## Aplicacao

```text
biblioteca/src/main/
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

- `webapp/`: camada View. Contem HTML, CSS, JavaScript e futuramente JSPs.
- `controller/`: camada Controller. Vai conter os Servlets que recebem HTTP.
- `service/`: regras de negocio, validacoes e orquestracao dos casos de uso.
- `model/`: entidades do dominio, como `Livro`.
- `dao/`: acesso ao banco via JDBC e SQL parametrizado.
- `config/`: criacao de conexoes e configuracoes compartilhadas.
- `exception/`: erros especificos do dominio, validacao e persistencia.

## Banco de dados

```text
biblioteca/database/
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
biblioteca/src/test/
|-- java/br/com/biblioteca/
`-- resources/
```

A estrategia segue a piramide de testes:

- muitos testes unitarios para Model e Service;
- testes de integracao para DAO/JDBC e banco;
- poucos testes E2E quando os Controllers e a View dinamica estiverem prontos.

## Regra pratica

O navegador nunca acessa o PostgreSQL diretamente. O fluxo correto e:

```text
View -> Controller -> Service -> DAO -> PostgreSQL
```

Essa separacao deixa claro o uso do MVC e atende ao requisito de tratar
requisicoes HTTP no servidor com persistencia relacional.
