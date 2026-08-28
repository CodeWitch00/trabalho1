<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Livros | Sistema de Gestão de Biblioteca</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <a class="skip-link" href="#conteudo-principal">Pular para o conteúdo principal</a>

    <header class="site-header">
        <div class="site-header__identidade">
            <p class="site-header__eyebrow">Biblioteca Municipal Boa Leitura</p>
            <h1>Gestão de livros</h1>
        </div>
        <div class="site-header__acoes">
            <a class="botao-link botao-link--primario" href="${pageContext.request.contextPath}/livros/novo">Novo livro</a>
        </div>
    </header>

    <nav class="site-nav" aria-label="Navegação principal">
        <ul class="nav-lista">
            <li><a class="nav-btn active" href="${pageContext.request.contextPath}/livros">Livros</a></li>
            <li><a class="nav-btn" href="${pageContext.request.contextPath}/index.html">Interface inicial</a></li>
        </ul>
    </nav>

    <main id="conteudo-principal">
        <div class="main-fichas">
            <section class="ficha card" aria-labelledby="acervo-titulo">
                <div class="ficha-header">
                    <span class="ficha-tag">CRUD</span>
                    <h2 id="acervo-titulo">Acervo cadastrado</h2>
                    <p class="ficha-subtitulo">Dados carregados do PostgreSQL pelo Controller, Service e DAO.</p>
                </div>

                <c:if test="${not empty flashMensagem}">
                    <p class="feedback" data-state="${flashTipo}" role="status">${flashMensagem}</p>
                </c:if>

                <form class="busca" method="get" action="${pageContext.request.contextPath}/livros">
                    <label for="campo-busca">Pesquisar no acervo</label>
                    <div class="busca__controles">
                        <input type="search" id="campo-busca" name="busca" value="${fn:escapeXml(busca)}" placeholder="Título, autor(a) ou categoria">
                        <button type="submit">Pesquisar</button>
                        <a class="botao-link" href="${pageContext.request.contextPath}/livros">Limpar busca</a>
                    </div>
                </form>

                <p id="contador-resultados" class="feedback" role="status">${totalLivros} livro(s) encontrado(s).</p>

                <div class="table-scroll">
                    <table>
                        <caption>Lista de livros cadastrados</caption>
                        <thead>
                            <tr>
                                <th scope="col">Título</th>
                                <th scope="col">Autor(a)</th>
                                <th scope="col">Categoria</th>
                                <th scope="col">ISBN</th>
                                <th scope="col">Ano</th>
                                <th scope="col">Exemplares</th>
                                <th scope="col">Status</th>
                                <th scope="col">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="livro" items="${livros}">
                                <tr>
                                    <td data-label="Título">${livro.titulo}</td>
                                    <td data-label="Autor(a)">${livro.autor}</td>
                                    <td data-label="Categoria">${livro.categoria}</td>
                                    <td data-label="ISBN"><c:out value="${empty livro.isbn ? '-' : livro.isbn}" /></td>
                                    <td data-label="Ano">${livro.anoPublicacao}</td>
                                    <td data-label="Exemplares">${livro.quantidadeExemplares}</td>
                                    <td data-label="Status">
                                        <span class="status ${livro.status.classeCss}">
                                            ${livro.status.descricao}
                                        </span>
                                    </td>
                                    <td data-label="Ações">
                                        <div class="acoes-tabela">
                                            <a class="botao-link" href="${pageContext.request.contextPath}/livros/editar?id=${livro.id}">Editar</a>
                                            <form method="post" action="${pageContext.request.contextPath}/livros/excluir" onsubmit="return confirm('Deseja excluir este livro?');">
                                                <input type="hidden" name="id" value="${livro.id}">
                                                <button class="botao-perigo" type="submit">Excluir</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <c:if test="${empty livros}">
                    <p class="sem-resultados">Nenhum livro encontrado.</p>
                </c:if>
            </section>
        </div>
    </main>

</body>
</html>
