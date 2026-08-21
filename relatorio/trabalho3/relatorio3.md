# Especificação Arquitetural — Sistema de Gestão de Biblioteca

**Disciplina:** AAP IV — Programação para Internet  
**Atividade:** Mapeamento de arquitetura *back-end* com MVC e HTTP  
**Aluna:** Rayana Santos  
**Professor:** Vander Elme  

## 1. Objetivo e contexto

Este documento especifica a arquitetura proposta para acrescentar autenticação ao Sistema de Gestão de Biblioteca. Atualmente, a aplicação é composta por HTML5, CSS3 e JavaScript executados no navegador, sem *back-end*, banco de dados ou controle de acesso. A evolução planejada adotará o padrão **Model-View-Controller (MVC)** com Java Servlets e JSP, separando apresentação, controle das requisições e regras de negócio. Essa separação facilita manutenção, testes e futuras operações de cadastro, consulta, atualização e exclusão do acervo.

No cenário proposto, um bibliotecário acessará uma página de login para entrar na área administrativa. As credenciais serão tratadas no servidor, e apenas usuários autenticados poderão acessar recursos protegidos, como cadastro de livros e leitores. O protocolo HTTP será utilizado no ciclo de requisição e resposta, enquanto `HttpSession` e o cookie de sessão manterão o estado de autenticação entre requisições distintas.

## 2. Componentes da arquitetura MVC

### 2.1 View — JSP/HTML

A **View** será responsável somente pela apresentação e coleta de dados. `login.jsp` exibirá o formulário de autenticação e eventuais mensagens de erro; as páginas internas exibirão acervo, cadastros e informações preparadas pelos Controllers. O formulário enviará login e senha por `POST`, pois credenciais não devem aparecer na URL. As JSPs protegidas deverão permanecer em `WEB-INF/views`, evitando seu acesso direto e obrigando a passagem pelo Controller. A View não deverá executar SQL, validar senhas nem implementar regras de negócio.

### 2.2 Controller — Servlets e filtro

O **Controller** receberá as requisições HTTP e coordenará o fluxo. `LoginServlet` lerá os parâmetros enviados, fará validações básicas e solicitará ao serviço de autenticação que confira as credenciais. Diante de falha, colocará uma mensagem genérica no objeto `request` e encaminhará novamente para `login.jsp`. Diante de sucesso, criará a sessão, registrará nela a identidade mínima do usuário e redirecionará para a área administrativa. `LogoutServlet` invalidará a sessão e redirecionará para o login.

Um `AuthenticationFilter` interceptará as URLs protegidas. Se encontrar uma sessão válida e um usuário autenticado, permitirá que a requisição alcance o Controller correspondente; caso contrário, redirecionará para o login. A centralização dessa verificação evita duplicação de código e reduz o risco de uma rota interna ficar desprotegida.

### 2.3 Model — domínio, serviço e persistência

O **Model** concentrará os dados e as regras da aplicação. A entidade `Usuario` representará a identidade e o perfil do usuário; `AutenticacaoService` aplicará as regras de autenticação; e `UsuarioDAO` fará a consulta ao banco por JDBC. O banco armazenará um *hash* seguro da senha, nunca a senha em texto puro. O Model devolverá ao Controller apenas o resultado necessário, sem depender de JSP ou de elementos visuais.

## 3. Diagrama e fluxo MVC de autenticação

```text
┌─────────────┐  GET /login   ┌─────────────────┐  forward  ┌───────────┐
│ Navegador   │ ─────────────> │ LoginServlet    │ ────────> │ login.jsp │
│ do usuário  │ <───────────── │ (Controller)    │ <──────── │  (View)   │
└──────┬──────┘   formulário   └─────────────────┘           └───────────┘
       │
       │ POST /login: identificador e senha
       ▼
┌─────────────────┐     autenticar()      ┌──────────────────────┐
│ LoginServlet    │ ────────────────────> │ AutenticacaoService  │
│ (Controller)    │                       │ + UsuarioDAO (Model) │
└────────┬────────┘ <──────────────────── └──────────┬───────────┘
         │           válido / inválido               │ SQL/JDBC
         │                                           ▼
         │                                    ┌──────────────┐
         │                                    │ Banco de     │
         │                                    │ dados        │
         │                                    └──────────────┘
         ├── inválido: forward para login.jsp com mensagem
         │
         └── válido: cria HttpSession, renova o identificador e redireciona
                     para /admin; requisições futuras passam pelo filtro
```

O primeiro acesso usa `GET /login`: o Controller encaminha a requisição para a View. Após o envio do formulário, `POST /login` chega ao mesmo Controller, que delega a validação ao Model. Se as credenciais forem inválidas, utiliza-se *forward*, preservando a mensagem somente naquela requisição. Se forem válidas, utiliza-se redirecionamento após o `POST`, evitando reenvio do formulário ao atualizar a página.

Nas próximas ações, o navegador enviará automaticamente o cookie identificador da sessão. O filtro consultará a sessão no servidor antes de liberar o acesso. Depois dessa verificação, Controllers específicos poderão chamar o Model e encaminhar resultados às respectivas Views, mantendo o mesmo fluxo MVC.

## 4. Gestão de sessões e cookies

O HTTP é um protocolo sem estado: uma requisição não conhece, por si só, as anteriores. Conforme a abordagem de controle de estado apresentada por Basham, Sierra e Bates, a sessão permite associar diversas requisições ao mesmo cliente sem enviar novamente as credenciais. Após a autenticação, o servidor criará uma `HttpSession` e armazenará apenas informações essenciais, como identificador, nome de exibição e perfil. Objetos desnecessários, dados sensíveis e a senha não serão mantidos na sessão.

O contêiner Servlet enviará ao navegador um cookie de sessão, normalmente chamado `JSESSIONID`. Esse cookie conterá um identificador opaco, não os dados pessoais do usuário. Em cada nova requisição ao mesmo domínio, o navegador devolverá o cookie; o servidor localizará a `HttpSession` correspondente e reconhecerá o usuário. Assim, **o estado permanece no servidor, enquanto o cookie transporta somente a chave que referencia esse estado**.

O cookie deverá ser configurado com `HttpOnly`, reduzindo sua exposição a scripts no navegador; `Secure`, para transmissão exclusiva por HTTPS em produção; e `SameSite`, como proteção complementar contra requisições iniciadas por outros sites. A aplicação deverá renovar o identificador da sessão após o login para reduzir o risco de fixação de sessão. HTTPS será obrigatório em produção, pois os atributos do cookie não substituem a criptografia do transporte.

A sessão terá tempo máximo de inatividade configurado no servidor. Um valor inicial de **20 minutos** é adequado para a área administrativa da Biblioteca: limita o período em que uma sessão abandonada pode ser usada sem impor autenticações excessivamente frequentes. Esse valor poderá ser revisto conforme o perfil de risco e o contexto de uso. Quando a sessão expirar, o filtro redirecionará o usuário ao login e apresentará uma mensagem apropriada. No logout, `LogoutServlet` chamará `invalidate()`, encerrando imediatamente a sessão; o cookie expirado ou sem correspondência não dará acesso ao sistema.

Também serão adotadas mensagens genéricas, como “Usuário ou senha inválidos”, para não revelar quais contas existem; senhas armazenadas como *hash* com algoritmo apropriado; consultas parametrizadas no DAO; e autorização por perfil nas operações administrativas. A sessão comprova que houve autenticação, mas não substitui a verificação de permissão para cada recurso.

## 5. Parecer técnico

A adoção de sessão mantida no servidor, referenciada por cookie, é indicada porque integra-se ao contêiner Servlet, evita o reenvio de credenciais e reduz a exposição de dados no cliente. Em comparação com guardar informações de autenticação diretamente em cookies, a `HttpSession` permite invalidar o acesso no servidor, controlar inatividade e manter dados de identidade fora do navegador. Seu custo de memória é aceitável para o escopo acadêmico e pode ser controlado com dados mínimos e tempo de expiração.

Conclui-se que a combinação de MVC, `AuthenticationFilter`, `HttpSession` e cookie seguro oferece separação clara de responsabilidades e controle de acesso coerente com o ciclo HTTP. A proposta permite evoluir a interface existente para um sistema com *back-end* sem misturar apresentação, regras de autenticação e persistência, além de preparar a aplicação para o futuro CRUD do acervo.

## Referência

BASHAM, Bryan; SIERRA, Kathy; BATES, Bert. *Head First Servlets and JSP: Passing the Sun Certified Web Component Developer Exam*. 2. ed. Sebastopol: O’Reilly Media, 2008.
