# Relatório Acadêmico — Sistema de Gestão de Biblioteca

*Trabalho assíncrono: desenvolvimento de uma interface gráfica de usuário (GUI) semântica, responsiva e acessível para o front-end de uma aplicação web.*

## 1. Introdução

O objetivo deste trabalho foi desenvolver a interface de um Sistema de Gestão de Biblioteca, permitindo o cadastro, a busca e a visualização de livros de um acervo. A atividade tem propósito acadêmico: não se pretendeu construir um sistema completo, com backend, banco de dados ou autenticação, mas sim demonstrar o domínio de HTML5 semântico e CSS3 na construção de uma interface bem estruturada, responsiva e acessível.

As tecnologias utilizadas foram HTML5 para a estrutura da página, CSS3 para a apresentação visual, o tema claro/escuro e a responsividade, e JavaScript apenas como recurso complementar — responsável pelo cadastro dinâmico de livros, pela busca no acervo e pela alternância de tema. A interface permanece completa e utilizável mesmo com o JavaScript desativado, já que a tabela, os campos do formulário e os dados de exemplo existem integralmente no HTML.

## 2. Estrutura semântica

A página foi organizada em três áreas principais — apresentadas visualmente como "fichas" de biblioteca —, cada uma delimitada por um elemento HTML5 semântico condizente com sua função, evitando o uso de `<div>` quando já existia um elemento apropriado.

- **`<header>`** — agrupa a identificação da biblioteca e o título principal do sistema (`h1`), delimitando o cabeçalho institucional da página.
- **`<nav>`** — contém a lista de links para as três fichas (Cadastro, Acervo, Resumo), indicando explicitamente a agentes de tecnologia assistiva que aquele bloco é um mecanismo de navegação.
- **`<main>`** — envolve todo o conteúdo principal da aplicação; um único `<main>` foi utilizado na página, permitindo que leitores de tela identifiquem rapidamente o conteúdo central.
- **`<section>`** — utilizada para a Ficha 1 (cadastro) e a Ficha 2 (acervo), cada uma com um agrupamento temático de conteúdo e título próprio (`h2`), o que justifica `<section>` em vez de `<div>`.
- **`<aside>`** — representa a Ficha 3 (resumo do acervo), um conteúdo complementar ao fluxo primário de cadastro e consulta, e não parte central dele.
- **`<footer>`** — reúne a informação institucional/acadêmica do projeto ao final da página.
- **`<form>`, `<fieldset>`, `<legend>`** — o formulário de cadastro é dividido em dois `<fieldset>` ("Dados da obra" e "Disponibilidade"), cada um com um `<legend>` descritivo, comunicando que os campos pertencem a um mesmo bloco lógico de informação.
- **`<label>`** — todo campo possui um `<label>` associado ao respectivo `input`/`select` pelos atributos `for`/`id`, o que garante que o nome do campo seja anunciado corretamente por leitores de tela e que um clique no texto do rótulo foque o campo.
- **`<table>`, `<caption>`, `<thead>`, `<tbody>`** — o acervo é representado por uma tabela HTML verdadeira. O `<caption>` descreve o conteúdo ("Lista de livros cadastrados"), o `<thead>` isola a linha de cabeçalhos (com `th scope="col"`) e o `<tbody>` contém os registros, incluindo os dados de exemplo que garantem a visualização da interface mesmo sem JavaScript.

## 3. Acessibilidade

- **Associação entre labels e campos**: todos os campos do formulário e o campo de busca possuem `<label>` vinculado por `for`/`id`.
- **Campos obrigatórios**: além do atributo `required` (que ativa a validação nativa do navegador), os campos obrigatórios exibem um asterisco visual acompanhado da legenda "\* Campo obrigatório", para que a obrigatoriedade não dependa apenas de cor ou de um símbolo isolado sem explicação.
- **Skip link**: um link "Pular para o conteúdo principal" foi posicionado no início do documento, visível apenas quando recebe foco, permitindo que usuários de teclado ignorem o cabeçalho e a navegação.
- **Foco por teclado**: todos os elementos interativos (links, campos, botões, incluindo o botão de busca e o de alternância de tema) são elementos nativos do HTML, o que garante foco e ativação via teclado. A pseudo-classe `:focus-visible` destaca com contorno o elemento focado.
- **Contraste**: as combinações de cor do tema claro e do tema escuro foram escolhidas para manter contraste adequado entre texto e fundo nas duas variações.
- **Mensagens de feedback**: a confirmação de cadastro, o contador de resultados da busca e a mensagem de "nenhum livro encontrado" utilizam `role="status"`/`aria-live="polite"`, para que sejam anunciadas automaticamente por leitores de tela sem interromper o fluxo de leitura.
- **Uso responsável de ARIA**: optou-se por não substituir elementos nativos por ARIA. As "fichas" são `<section>`/`<aside>` comuns, não abas controladas por `role="tab"`; os botões são `<button>`; a navegação usa `<nav>` com links reais. ARIA foi usado apenas de forma pontual (`aria-live`, `aria-describedby`, `aria-pressed` no botão de tema), quando o HTML nativo não expressava sozinho o comportamento dinâmico.

## 4. Responsividade

O layout segue a abordagem mobile-first: as regras de CSS fora de media queries definem o comportamento em telas pequenas (fichas em coluna única, na ordem Cadastro → Acervo → Resumo), e media queries progressivas ampliam a apresentação para telas maiores.

A partir de 600px, os campos do formulário passam a exibir rótulo e campo lado a lado, em um layout de grade simples. A partir de 900px, a Ficha 1 (cadastro) e a Ficha 2 (acervo) passam a ocupar colunas diferentes, com a Ficha 3 (resumo) ocupando a largura total abaixo, por meio de CSS Grid com áreas nomeadas.

A tabela do acervo recebe tratamento específico para telas muito pequenas (até 480px): os cabeçalhos de coluna são ocultados visualmente (mas mantidos para leitores de tela) e cada linha passa a se comportar como um cartão, com o rótulo de cada coluna exibido antes do valor por meio do atributo `data-label`. A semântica da tabela é preservada em todas as larguras; apenas a apresentação visual muda. A barra de busca também se adapta, com o campo de texto ocupando a largura disponível em telas estreitas.

## 5. Boas práticas de CSS

HTML, CSS e JavaScript foram mantidos em arquivos separados (`index.html`, `css/styles.css` e `js/script.js`), sem estilos inline, mantendo estrutura, apresentação e comportamento com responsabilidades independentes.

Variáveis CSS (custom properties) foram definidas em `:root` para cores, tipografia e espaçamentos, e reatribuídas dentro do seletor `[data-theme="dark"]` para implementar o tema escuro. Essa abordagem evita duplicar regras de layout entre os dois temas: apenas os valores das variáveis mudam, e todo o restante do CSS é reaproveitado.

O arquivo de estilos foi organizado em seções comentadas por responsabilidade (variáveis, reset, tipografia, layout, header, navegação, formulário, botões, busca, tabela, resumo, footer, responsividade e acessibilidade). Classes reutilizáveis, como `.card`, `.ficha`, `.form-field` e `.status`, evitam a repetição de regras entre os diferentes blocos da página.

## 6. Conclusão

A interface desenvolvida atende aos objetivos da atividade: utiliza HTML5 semântico de forma consistente, aplica boas práticas de acessibilidade, é responsiva do celular ao monitor grande, oferece tema claro e escuro, e mantém o JavaScript em papel estritamente complementar (cadastro, busca e alternância de tema, sem backend, login ou API). A organização visual em três fichas facilita a compreensão da interface sem recorrer a componentes complexos como abas ARIA, o que manteve o projeto simples de defender academicamente e alinhado à proposta de demonstrar domínio de HTML5 e CSS3 na construção de interfaces web.

*Observação sobre a bibliografia de apoio: a consulta aos capítulos introdutórios de Freeman & Freeman sobre estrutura semântica e boas práticas de CSS orientou a forma geral de organização adotada neste relatório (separação de responsabilidades, ênfase em semântica e em boas práticas de CSS). Não foram incluídas citações diretas ou afirmações específicas atribuídas aos autores, pois o conteúdo textual exato dos capítulos não estava disponível para conferência no momento da elaboração deste relatório.*
