# Estratégia de Desenvolvimento — Sistema de Gestão de Biblioteca

## 1. Objetivo

Transformar o front-end existente na pasta `biblioteca` em uma aplicação web
funcional, com operações CRUD, separação em camadas segundo o padrão MVC,
tratamento de requisições e respostas HTTP no servidor e persistência em banco
de dados relacional.

Este documento orienta o desenvolvimento, mas não encerra decisões que ainda
precisam ser tomadas. Antes de cada etapa, as alternativas, consequências e
critérios de aceitação serão apresentados e aprovados em conjunto.

## 2. Estado atual

O projeto possui uma interface estática construída com HTML, CSS e JavaScript.
Ela contém:

- formulário de cadastro de livros;
- visualização e pesquisa do acervo;
- formulário de leitores;
- consulta assíncrona de CEP pela API ViaCEP;
- indicadores do acervo;
- validações no navegador;
- responsividade, acessibilidade e temas claro e escuro.

Atualmente, livros e leitores são inseridos apenas no DOM. Os dados desaparecem
quando a página é recarregada. Ainda não existem servidor Java, Controllers,
camada de serviço, DAO, JDBC ou persistência em banco de dados. Também não há
operações funcionais de atualização e exclusão.

## 3. Arquitetura em avaliação

A arquitetura inicial proposta, sujeita à aprovação antes da implementação, é:

```text
View
HTML + CSS + JavaScript + JSP
              |
              | requisições e respostas HTTP
              v
Controller
Jakarta Servlets
              |
              v
Service
Validações e regras de negócio
              |
              v
DAO
JDBC + SQL parametrizado
              |
              v
Banco de dados
PostgreSQL hospedado no Supabase
```

Responsabilidades previstas:

- **View:** apresentar dados, coletar entradas e oferecer feedback ao usuário;
- **Controller:** receber requisições HTTP, interpretar parâmetros, coordenar o
  fluxo e produzir a resposta adequada;
- **Service:** aplicar validações e regras de negócio;
- **Model:** representar as entidades e os dados do domínio;
- **DAO:** executar exclusivamente as operações de persistência;
- **PostgreSQL:** armazenar os dados e garantir sua integridade.

O JavaScript do navegador não deverá acessar o banco diretamente. A comunicação
com o PostgreSQL ocorrerá no servidor por meio de DAO e JDBC, evidenciando as
camadas exigidas pela atividade.

## 4. Tecnologias aprovadas

As seguintes tecnologias e linhas de versão foram aprovadas para a preparação
do ambiente:

- Java 17;
- Jakarta Servlet;
- JSP e JSTL;
- JDBC e driver PostgreSQL;
- PostgreSQL hospedado no Supabase;
- Maven 3.9;
- Apache Tomcat 10.1;
- mise para instalar as ferramentas e executar tarefas reproduzíveis;
- HTML, CSS e JavaScript existentes;
- JUnit para testes automatizados;
- uma biblioteca de objetos simulados, caso necessária;
- Git e GitHub.

As versões exatas resolvidas pelo mise deverão ser verificadas e poderão ser
registradas em arquivo de lock. As versões das dependências da aplicação serão
definidas e aprovadas durante a criação do `pom.xml`.

## 5. Escopo inicial aprovado

Em 26 de agosto de 2026, foi aprovado o seguinte escopo inicial:

- CRUD completo de livros;
- listagem e pesquisa do acervo;
- resumo calculado a partir dos dados persistidos;
- manutenção das validações do navegador;
- validação definitiva no servidor;
- PostgreSQL no Supabase;
- transformação dos livros estáticos atuais em dados iniciais do banco;
- manutenção da interface de leitores e da consulta ViaCEP, sem persistência
  nesta primeira etapa;
- autenticação e perfis planejados para uma etapa posterior de segurança.

O CRUD de leitores será implementado somente depois da conclusão e validação do
CRUD completo de livros.

## 6. Estratégia de implementação

### Etapa 1 — Confirmar escopo e regras

Definir as entidades e operações que farão parte da entrega. Para livros,
discutir título, autor, categoria, ISBN, ano, quantidade de exemplares, status,
unicidade, exclusão e demais regras.

**Resultado esperado:** escopo e regras registrados e aprovados.

### Etapa 2 — Preparar e verificar o ambiente

Verificar Java, Maven e Git; escolher versões compatíveis do Java e Tomcat;
confirmar a ferramenta de desenvolvimento; criar ou configurar o projeto no
Supabase.

**Resultado esperado:** ambiente reproduzível e ferramentas verificadas.

### Etapa 3 — Modelar o banco de dados

Definir tabelas, colunas, tipos, chaves, restrições, relacionamentos e índices.
Criar os arquivos:

```text
database/schema.sql
database/dados-iniciais.sql
```

**Resultado esperado:** banco recriável por scripts versionados.

### Etapa 4 — Converter o projeto em aplicação Maven Web

Reorganizar o código no formato de uma aplicação Java Web, preservando o
frontend atual sempre que possível:

```text
biblioteca/
|-- pom.xml
|-- database/
`-- src/
    |-- main/
    |   |-- java/br/com/biblioteca/
    |   `-- webapp/
    |       |-- WEB-INF/views/
    |       |-- css/
    |       `-- js/
    `-- test/
        `-- java/br/com/biblioteca/
```

**Resultado esperado:** projeto compilável e executável no servidor escolhido.

### Etapa 5 — Configurar JDBC

Criar uma fábrica ou provedor de conexões. URL, usuário e senha serão recebidos
por variáveis de ambiente e não serão armazenados no repositório.

Variáveis previstas:

```text
DB_URL
DB_USER
DB_PASSWORD
```

**Resultado esperado:** conexão validada sem exposição de credenciais.

### Etapa 6 — Implementar Model e DAO

Criar a entidade `Livro` e o contrato de persistência com operações de criação,
listagem, busca por identificador, pesquisa, atualização e exclusão. A
implementação usará `PreparedStatement` e gerenciamento seguro dos recursos
JDBC.

**Resultado esperado:** todas as operações SQL funcionando independentemente da
interface.

### Etapa 7 — Implementar a camada de serviço

Centralizar regras de negócio e validações do servidor. Possíveis regras, ainda
sujeitas à decisão, incluem:

- título, autor e categoria obrigatórios;
- quantidade de exemplares não negativa;
- ano de publicação dentro de uma faixa válida;
- ISBN não duplicado quando informado;
- status pertencente aos valores permitidos;
- existência do livro antes de atualizar ou excluir.

**Resultado esperado:** regras independentes de Servlet, JSP e JDBC.

### Etapa 8 — Implementar Controllers HTTP

Criar Servlets para os fluxos CRUD. Rotas iniciais em avaliação:

```text
GET  /livros
POST /livros
GET  /livros/editar?id={id}
POST /livros/atualizar
POST /livros/excluir
```

Serão tratados parâmetros inválidos, registros inexistentes, falhas de banco e
mensagens de sucesso. Operações de escrita deverão seguir o padrão
Post/Redirect/Get quando adequado.

**Resultado esperado:** ciclo HTTP funcional e erros tratados no servidor.

### Etapa 9 — Integrar as Views

Adaptar o HTML existente para JSP, substituir registros fixos pelos dados
fornecidos pelo Controller e acrescentar ações de edição e exclusão. Preservar
responsividade, acessibilidade, tema e integração ViaCEP.

**Resultado esperado:** CRUD utilizável pelo navegador com dados persistentes.

### Etapa 10 — Revisar o JavaScript

Remover a criação exclusivamente local de registros no DOM. Manter o JavaScript
responsável pela experiência do usuário, como validação imediata, navegação,
tema, ViaCEP e confirmação visual. A segurança e a validação definitiva
continuarão no servidor.

**Resultado esperado:** ausência de conflito entre o estado visual e o banco.

### Etapa 11 — Documentar e preparar a entrega

Atualizar o README com arquitetura, tecnologias, configuração do Supabase,
variáveis de ambiente, criação do banco, compilação, execução, rotas e testes.

**Resultado esperado:** outra pessoa consegue configurar e executar o projeto a
partir do repositório.

## 7. Estratégia de testes — Pirâmide de testes

Todo comportamento implementado deverá ser testado. A estratégia seguirá a
pirâmide de testes: muitos testes unitários, uma quantidade menor de testes de
integração e poucos testes de ponta a ponta.

```text
                    /\
                   /  \
                  / E2E\       Poucos e focados nos fluxos críticos
                 /------\
                / Integração\  DAO, JDBC, banco, Controllers e HTTP
               /------------\
              /   Unitários   \ Muitos, rápidos e isolados
             /________________\
```

### 7.1 Testes unitários — base da pirâmide

Devem ser rápidos, independentes e executados sem servidor ou banco externo.
Cobrirão principalmente:

- regras do `LivroService`;
- validação de título, autor, ano, quantidade, ISBN e status;
- comportamento para livro inexistente;
- conversões e utilitários;
- JavaScript com lógica isolável, se adotarmos uma ferramenta compatível.

Dependências do Service poderão ser substituídas por objetos simulados quando
isso ajudar a testar somente a regra de negócio.

### 7.2 Testes de integração — meio da pirâmide

Verificarão se componentes reais funcionam em conjunto. Cobrirão:

- `LivroDAO` com um PostgreSQL de teste;
- scripts `schema.sql` e `dados-iniciais.sql`;
- conexão e transações JDBC;
- comandos `INSERT`, `SELECT`, `UPDATE` e `DELETE`;
- restrições como ISBN único e quantidade não negativa;
- Controllers, parâmetros HTTP, redirecionamentos e encaminhamentos;
- serialização ou atributos enviados às Views, conforme a arquitetura aprovada.

O ambiente de integração não deverá alterar dados de produção. Será decidido se
usaremos um projeto/esquema de teste no PostgreSQL ou um banco PostgreSQL
descartável executado durante os testes.

SQLite não substituirá PostgreSQL nos testes de integração, pois diferenças de
SQL, tipos e restrições poderiam ocultar defeitos que surgiriam no banco real.

### 7.3 Testes de ponta a ponta — topo da pirâmide

Poucos testes validarão os principais percursos pelo sistema completo:

- abrir a página e listar livros persistidos;
- cadastrar um livro válido;
- impedir um cadastro inválido;
- editar um livro;
- excluir um livro após confirmação;
- pesquisar o acervo;
- recarregar a aplicação e confirmar que os dados permanecem.

A ferramenta e o grau de automação desses testes serão decididos depois que o
fluxo das Views estiver definido. Verificações manuais poderão complementar,
mas não substituir sem justificativa os testes automatizados dos comportamentos
críticos.

### 7.4 Testes não funcionais e verificações complementares

Além da pirâmide, serão feitas verificações direcionadas de:

- responsividade em diferentes larguras;
- navegação por teclado e atributos de acessibilidade;
- funcionamento nos navegadores definidos para o projeto;
- ausência de credenciais no repositório;
- uso de SQL parametrizado;
- tratamento de indisponibilidade do banco;
- mensagens compreensíveis de sucesso e erro;
- compilação limpa e execução reproduzível.

## 8. Relação entre etapas e testes

| Etapa | Evidência mínima de teste |
|---|---|
| Ambiente | versões verificadas e aplicação mínima compilada |
| Banco | scripts executados em banco de teste e restrições verificadas |
| Model | testes de construção e comportamento relevante |
| DAO/JDBC | testes de integração para todo o CRUD |
| Service | testes unitários para regras válidas, limites e erros |
| Controller | testes HTTP de parâmetros, respostas e redirecionamentos |
| View/JavaScript | testes dos fluxos críticos e verificações de interface |
| Integração completa | testes E2E de Create, Read, Update e Delete |
| Documentação | execução reproduzida seguindo apenas o README |

## 9. Definição de concluído

Uma funcionalidade somente será considerada concluída quando:

1. seu comportamento e suas regras tiverem sido aprovados;
2. o código respeitar a separação MVC, Service e DAO;
3. os testes adequados ao nível da pirâmide tiverem sido escritos;
4. todos os testes existentes estiverem passando;
5. erros esperados forem tratados;
6. nenhuma credencial ou dado sensível estiver versionado;
7. a documentação afetada estiver atualizada;
8. o resultado tiver sido apresentado para validação.

Correções de defeitos deverão, sempre que possível, começar por um teste que
reproduza a falha. Depois da correção, toda a suíte será executada para detectar
regressões.

## 10. Forma de colaboração e pontos de decisão

O desenvolvimento será incremental. Antes de cada mudança material:

1. será explicado o objetivo da etapa;
2. serão apresentadas as alternativas relevantes;
3. serão discutidos impactos e limitações;
4. a decisão será registrada;
5. a implementação será realizada;
6. os testes correspondentes serão executados;
7. os resultados serão apresentados antes de avançar.

Os principais pontos de decisão serão:

- escopo do CRUD de livros e leitores;
- regras de negócio;
- versões do Java, Tomcat e dependências;
- modelo relacional;
- estrutura das rotas HTTP;
- uso de JSP tradicional ou respostas JSON em partes da interface;
- estratégia do banco PostgreSQL de teste;
- ferramentas de automação dos testes de integração e E2E;
- momento de implementar autenticação e controle de acesso.

## 11. Próxima decisão

Em 26 de agosto de 2026, foram aprovadas as primeiras regras de `Livro`:

- `id` gerado pelo PostgreSQL;
- título, autor e categoria obrigatórios;
- ISBN opcional, normalizado sem espaços ou hífens e único quando informado;
- ano de publicação entre 1500 e o ano corrente;
- quantidade de exemplares inteira e não negativa;
- status limitado a `DISPONIVEL`, `EMPRESTADO` ou `RESERVADO`;
- status disponível permitido somente quando houver pelo menos um exemplar;
- pesquisa parcial por título, autor ou categoria, sem diferenciar maiúsculas e
  minúsculas;
- confirmação do usuário antes da exclusão;
- atualização ou exclusão de identificador inexistente tratada como livro não
  encontrado;
- carga inicial formada pelos 15 livros já exibidos no HTML.

Também foi aprovado o modelo simples da interface atual, com uma quantidade e
um status geral por livro. Quando empréstimos forem implementados, esse modelo
será revisto para representar exemplares ou quantidades disponíveis.

A próxima decisão será a aprovação do modelo físico PostgreSQL e dos respectivos
casos de teste antes da criação de `schema.sql` e `dados-iniciais.sql`.
