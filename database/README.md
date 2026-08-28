# Banco de dados

Esta pasta contém a estrutura, a carga inicial e o teste de integração do banco
PostgreSQL da aplicação.

## Arquivos

- `schema.sql`: cria a tabela `livro` e suas restrições;
- `dados-iniciais.sql`: insere os 15 livros originalmente exibidos no HTML;
- `teste-schema.sql`: verifica carga, CRUD e restrições dentro de uma transação
  finalizada com `ROLLBACK`.

## Teste local descartável

Na raiz do repositório, execute:

```bash
mise install
mise run test-db
```

A tarefa cria um cluster PostgreSQL 17.6 em um diretório temporário, executa os
três arquivos e remove o cluster ao terminar. Ela não usa credenciais nem altera
o projeto no Supabase.

## Aplicação no Supabase

No SQL Editor do projeto `biblioteca-aap`, execute os arquivos nesta ordem:

1. `schema.sql`;
2. `dados-iniciais.sql`;
3. `teste-schema.sql`.

Os dois primeiros arquivos persistem a estrutura e a carga inicial. O terceiro
realiza operações apenas para teste e termina com `ROLLBACK`, não conservando os
registros temporários.

Após a execução, esta consulta deve retornar `15`:

```sql
SELECT COUNT(*) FROM livro;
```

Os scripts de estrutura e carga foram planejados para um banco vazio. Não os
execute novamente sobre uma tabela já criada, pois a criação falhará e a carga
poderá duplicar registros.
