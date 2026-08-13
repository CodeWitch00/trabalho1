# Sistema de Gestão de Biblioteca

Interface gráfica (GUI) para cadastro, busca e visualização de livros de
um acervo de biblioteca.

## Objetivo

Demonstrar, para fins acadêmicos, a construção de uma interface web
semântica, responsiva e acessível utilizando HTML5 e CSS3, com JavaScript
apenas como complemento opcional.

## Tecnologias

- HTML5
- CSS3 (variáveis, Flexbox, Grid, Media Queries)
- JavaScript (ES5+, vanilla, sem frameworks)

Nenhum framework de CSS ou de componentes (Bootstrap, Tailwind, etc.) foi
utilizado.

## Estrutura de arquivos

```
biblioteca/
│
├── index.html
├── css/
│   └── styles.css
├── js/
│   ├── script.js
│   ├── validacao.js
│   └── cep.js
├── README.md
└── relatorio.md
```

## Funcionalidades

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

O projeto é inteiramente client-side: não há backend próprio ou build
steps. Basta abrir o arquivo `index.html` em um navegador.

A estrutura HTML e os dados de exemplo funcionam mesmo com o JavaScript
desativado; os scripts apenas adicionam cadastro dinâmico, busca no
acervo, validação em tempo real, alternância de tema e a busca de
endereço por CEP. A busca de CEP depende de conexão com a internet, pois
consulta a API pública ViaCEP (https://viacep.com.br); sem conexão, o
endereço pode ser preenchido manualmente.
