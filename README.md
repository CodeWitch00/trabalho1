# Trabalho AAP - Sistema de Biblioteca

Repositorio do projeto academico de gestao de biblioteca.

## Onde esta cada coisa

- `biblioteca/`: aplicacao web Java/Maven.
- `biblioteca/src/main/java/br/com/biblioteca/`: codigo Java organizado por MVC.
- `biblioteca/src/main/webapp/`: front-end da aplicacao.
- `biblioteca/database/`: scripts SQL do PostgreSQL.
- `biblioteca/scripts/`: scripts auxiliares de teste.
- `docs/`: estrategia, descricoes e relatorios.

## Arquitetura

O fluxo aprovado para a aplicacao e:

```text
View -> Controller -> Service -> DAO -> PostgreSQL
```

Mais detalhes:

- `docs/estrategia-desenvolvimento.md`
- `docs/estrutura-repositorio.md`
- `biblioteca/README.md`

## Comandos principais

```bash
mise run versions
mise run test
mise run test-db
mise run package
```
