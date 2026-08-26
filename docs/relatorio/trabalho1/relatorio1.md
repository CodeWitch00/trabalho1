# Relatório : Sistema de Gestão de Biblioteca

**Disciplina:** AAP IV - Programação para internet
**Atividade:** GUI Semâtica , Responsiva e Acessível para Front-End web
**Aluna:** Rayana Santos
**Professor:** Vander Elme
**GitHub:** https://github.com/CodeWitch00/trabalho1.git


## 1. Introdução

O objetivo deste trabalho foi desenvolver a interface de um Sistema de Gestão de Biblioteca, permitindo o cadastro, a busca e a visualização de livros de um acervo. A atividade tem propósito acadêmico: o foco principal NÃO é desenvolver um sistema completo de biblioteca com backend ou banco de dados, mas sim demonstrar a construção de uma GUI semântica, responsiva e acessível utilizando HTML5 e CSS3, mantendo o código limpo, organizado e "um básico bem feito".
 
As tecnologias utilizadas foram HTML5 para a estrutura da página, CSS3 para a apresentação visual, o tema claro/escuro e a responsividade, e JavaScript apenas como recurso complementar ,responsável pelo cadastro dinâmico de livros, pela busca no acervo,alternância de tema e alternância interativa das fichas (exibindo apenas a ficha selecionada no menu de navegação). A interface permanece completa e utilizável mesmo com o JavaScript desativado, já que a tabela, os campos do formulário e os dados de exemplo existem integralmente no HTML.

## 2. Estrutura semântica e Navegação por Fichas Selecionadas

A página foi organizada em três áreas principais, apresentadas visualmente como "fichas" de biblioteca dispostas verticalmente (uma ficha embaixo da outra), cada uma delimitada por elementos HTML5 semânticos:

- **`<header>`**: agrupa a identificação da biblioteca e o título principal do sistema (`h1`), delimitando o cabeçalho institucional da página.
- **`<nav>` e Botões de Navegação Destacados**: contém os botões de navegação para alternar entre as fichas (Ficha 1: Cadastro, Ficha 2: Acervo, Ficha 3: Resumo e a opção Exibir Todas). Os botões utilizam uma cor contrastante (bege claro com texto em azul vintage e borda bem definida no estado normal, e vermelho vintage terracota no estado ativo) para se destacarem claramente da cor de fundo da barra de navegação (azul slate navy).
- **`<main>`**: envolve o container central onde as fichas ficam organizadas verticalmente, uma abaixo da outra (`flex-direction: column`).
- **`<section>` e `<aside>`**: representam a Ficha 1 (cadastro), a Ficha 2 (acervo) e a Ficha 3 (resumo do acervo). Ao clicar no botão correspondente na barra de navegação, a interface exibe apenas a ficha selecionada (ocultando as demais), tornando a experiência de uso limpa, focada e objetiva.
- **`<footer>`**: reúne as informações acadêmicas/institucionais ao final da página.
- **`<form>`, `<fieldset>`, `<legend>`**: o formulário de cadastro é dividido em dois `<fieldset>` ("Dados da obra" e "Disponibilidade"), cada um com `<legend>` descritivo.
- **`<label>` e `input` com `placeholder`**: os exemplos de sugestão foram colocados diretamente dentro dos campos de preenchimento através do atributo `placeholder` (ex: `Ex: Dom Casmurro`, `Ex: Machado de Assis`, `Ex: 978-85-359-0277-8`), mantendo a interface limpa e os rótulos despoluídos.
- **`<table>`, `<caption>`, `<thead>`, `<tbody>`**: o acervo é representado por uma tabela HTML verdadeira com `<caption>` descritivo, `<thead>` para cabeçalhos de coluna e `<tbody>` contendo os registros de exemplo.

## 3. Acessibilidade e Identificação Visual

- **Navegação com Papéis ARIA (`role="tablist"` e `role="tab"`)**: os botões da barra de navegação e os painéis de ficha foram estruturados com atributos acessíveis (`role="tab"`, `role="tabpanel"`, `aria-selected`, `aria-controls`), permitindo alternar de maneira limpa por teclado ou clique.
- **Botões Contrastantes na Navegação**: a cor dos botões foi projetada com alta diferenciação em relação ao fundo azul da barra, permitindo visualização imediata do item selecionado.
- **Asterisco de Campo Obrigatório em Vermelho**: todos os campos obrigatórios exibem um asterisco visual padronizado na cor vermelha (`.asterisco-obrigatorio`), garantindo destaque imediato.
- **Exemplos nos Campos de Preenchimento**: as sugestões de preenchimento foram incorporadas aos próprios campos (`placeholder`), mantendo a leitura leve e objetiva.
- **Skip link**: um link "Pular para o conteúdo principal" possibilita navegar via teclado pulando o cabeçalho e menu.
- **Foco e Contraste**: os elementos interativos utilizam o seletor `:focus-visible` com contorno de foco bem definido. A paleta de cores Vintage Moderna (bege, azul e vermelho) foi ajustada para manter excelente contraste (WCAG) em ambos os temas.
- **Mensagens de Feedback Acessíveis**: avisos de validação, contador de resultados e mensagens de "nenhum resultado encontrado" utilizam `role="status"` e `aria-live="polite"`.

## 4. Responsividade (Mobile-First) e Disposição Vertical

- O layout mantém as fichas empilhadas verticalmente (uma embaixo da outra).
- Em telas pequenas, médias e grandes, ao clicar em uma ficha na barra superior, apenas a ficha escolhida permanece visível na tela central.
- Caso o usuário opte pela opção "Exibir Todas", todas as três fichas aparecem em sequência vertical simples e organizada.
- Em dispositivos móveis (até 480px), os botões de navegação se reorganizam para facilitar o toque, e a tabela do acervo se transforma em cartões individuais mantendo os rótulos via atributo `data-label`.

## 5. Design e Boas Práticas de CSS (Paleta Vintage Moderna)

HTML, CSS e JavaScript foram mantidos em arquivos separados. O CSS utiliza custom properties (`:root`) para definir o sistema de design:
- **Barra de Navegação em Azul Vintage (`--color-primary: #1f3a52`)**: cria o divisor visual superior.
- **Botões em Bege Claro (`--color-surface: #faf7f0`) e Vermelho Ativo (`--color-red: #c0392b`)**: garantem contraste total em relação à barra azul.
- **Superfície em Bege Linho (`#faf7f0`)**: tom clássico de cartão catalográfico.
- **Tema Escuro (`[data-theme="dark"]`)**: ajusta dinamicamente os valores das variáveis preservando o contraste e o efeito dos botões de navegação.

## 6. Conclusão

A interface desenvolvida atende aos objetivos da atividade: utiliza HTML5 semântico de forma consistente, aplica boas práticas de acessibilidade, é responsiva do celular ao monitor grande, oferece tema claro e escuro, e mantém o JavaScript em papel estritamente complementar (cadastro, busca e alternância de tema, sem backend, login ou API). A organização visual em três fichas facilita a compreensão da interface sem recorrer a componentes complexos como abas ARIA, o que manteve o projeto simples de defender academicamente e alinhado à proposta de demonstrar domínio de HTML5 e CSS3 na construção de interfaces web.
