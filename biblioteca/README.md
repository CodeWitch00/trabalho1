# Sistema de Gestão de Biblioteca

Interface web para cadastro, consulta e visualização de livros de um acervo bibliográfico.

O projeto foi desenvolvido como atividade acadêmica com foco em **HTML5 semântico, CSS3, responsividade, acessibilidade e boas práticas de desenvolvimento front-end**.

## Objetivo

Desenvolver uma interface gráfica de usuário (GUI) para um sistema de gestão de biblioteca, permitindo:

* cadastrar livros;
* visualizar livros cadastrados;
* pesquisar obras no acervo;
* consultar informações resumidas da biblioteca;
* utilizar a interface em diferentes tamanhos de tela;
* alternar entre tema claro e escuro.

O projeto é executado integralmente no lado cliente (*client-side*).

## Tecnologias utilizadas

* **HTML5** : estrutura e semântica da página;
* **CSS3** : layout, identidade visual, responsividade e acessibilidade visual;
* **JavaScript** : interações e comportamentos da interface.

Não são utilizados frameworks ou bibliotecas externas.

## Estrutura do projeto

```text
biblioteca/
│
├── index.html
│
├── css/
│   └── styles.css
│
├── js/
│   └── script.js
│
├── README.md
│
└── relatorio.md
```

## Organização da interface

A interface utiliza uma identidade visual inspirada em bibliotecas tradicionais e fichas catalográficas, combinando uma estética **vintage/editorial** com recursos de uma interface web moderna.

O conteúdo principal é organizado em três áreas:

### Ficha 1 : Cadastro

Área destinada ao registro de novas obras no acervo.

O formulário utiliza elementos semânticos do HTML5, como:

* `<form>`;
* `<fieldset>`;
* `<legend>`;
* `<label>`;
* `<input>`;
* `<select>`;
* `<button>`.

Os campos obrigatórios são identificados visualmente por `*` e também utilizam o atributo `required`.

Pequenas legendas de orientação fornecem sugestões para o preenchimento dos campos.

### Ficha 2 : Acervo

Área destinada à visualização dos livros cadastrados.

Os dados são apresentados utilizando uma tabela HTML semântica, composta por:

* `<table>`;
* `<caption>`;
* `<thead>`;
* `<tbody>`;
* `<th>`;
* `<td>`.

A interface possui uma barra de busca que permite localizar livros por informações como:

* título;
* autor(a);
* categoria.

A tabela contém registros de exemplo e pode receber novos livros por meio do formulário de cadastro.

### Ficha 3 : Resumo

Área destinada à apresentação de informações gerais do acervo.

São apresentados indicadores como:

* total de livros;
* livros disponíveis;
* livros emprestados;
* livros reservados;
* quantidade de categorias;
* quantidade de autores.

Os indicadores são atualizados conforme os dados do acervo são modificados.

## Responsividade

O projeto utiliza uma abordagem **mobile-first**, adaptando o conteúdo para diferentes tamanhos de tela.

São utilizados recursos do CSS3, incluindo:

* CSS Grid;
* Flexbox;
* Media Queries;
* unidades relativas;
* variáveis CSS;
* layouts adaptáveis.

Em telas menores, as áreas da aplicação são organizadas em uma única coluna. Em telas maiores, o layout utiliza melhor o espaço disponível.

A tabela também possui uma apresentação adaptada para dispositivos com telas pequenas.

## Acessibilidade

Foram aplicadas práticas de acessibilidade, incluindo:

* link para pular diretamente ao conteúdo principal;
* associação correta entre `<label>` e campos de formulário;
* utilização de elementos HTML semânticos;
* campos obrigatórios identificados por texto;
* foco visual utilizando `:focus-visible`;
* navegação por teclado;
* mensagens de feedback acessíveis;
* contraste adequado;
* utilização de `aria-live` ou `role="status"` quando necessário.

O projeto prioriza elementos HTML nativos antes da utilização de atributos ARIA.

## Tema claro e escuro

A interface possui alternância entre **tema claro e tema escuro**.

As cores são organizadas por meio de variáveis CSS, permitindo alterar a aparência da aplicação de maneira consistente.

A preferência de tema pode ser preservada no navegador utilizando armazenamento local, quando implementado.

## JavaScript

O JavaScript é utilizado como camada complementar de comportamento.

Entre as funcionalidades implementadas estão:

* cadastro de novos livros;
* atualização da tabela;
* atualização dos indicadores do resumo;
* pesquisa no acervo;
* limpeza da pesquisa;
* alternância entre tema claro e escuro;
* mensagens de feedback.

A estrutura principal da interface permanece disponível no HTML, sem depender exclusivamente do JavaScript para sua representação.

Os dados fornecidos pelo usuário são inseridos utilizando métodos como `textContent` e criação de elementos do DOM, evitando a interpretação direta de conteúdo não confiável como HTML.

## Como executar

Não é necessário instalar dependências.

Basta abrir o arquivo:

```text
index.html
```

em um navegador moderno.

Também é possível utilizar uma extensão como **Live Server** no Visual Studio Code para executar o projeto durante o desenvolvimento.

## Compatibilidade

O projeto foi desenvolvido utilizando recursos padrão do HTML5, CSS3 e JavaScript e deve funcionar nos principais navegadores modernos.

Recomenda-se utilizar versões atualizadas de navegadores como:

* Google Chrome;
* Mozilla Firefox;
* Microsoft Edge;
* Safari.

## Finalidade acadêmica

Este projeto foi desenvolvido para demonstrar a aplicação prática de conceitos relacionados a:

* estrutura semântica do HTML5;
* boas práticas de CSS3;
* design responsivo;
* acessibilidade na web;
* separação entre estrutura, apresentação e comportamento;
* desenvolvimento de interfaces do lado cliente.

As justificativas acadêmicas das decisões de implementação estão apresentadas no arquivo [`relatorio.md`](relatorio.md).

## Referência

Os conceitos utilizados no desenvolvimento foram relacionados ao livro de **FREEMAN & FREEMAN**.

