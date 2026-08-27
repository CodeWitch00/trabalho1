<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${tituloPagina} | Sistema de Gestão de Biblioteca</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <a class="skip-link" href="#conteudo-principal">Pular para o conteúdo principal</a>

    <header class="site-header">
        <div class="site-header__identidade">
            <p class="site-header__eyebrow">Biblioteca Municipal Boa Leitura</p>
            <h1>${tituloPagina}</h1>
        </div>
        <div class="site-header__acoes">
            <a class="botao-link" href="${pageContext.request.contextPath}/livros">Voltar ao acervo</a>
        </div>
    </header>

    <main id="conteudo-principal">
        <div class="main-fichas">
            <section class="ficha card" aria-labelledby="formulario-titulo">
                <div class="ficha-header">
                    <span class="ficha-tag">Livro</span>
                    <h2 id="formulario-titulo">Dados da obra</h2>
                    <p class="ficha-subtitulo">O navegador ajuda no preenchimento, mas a validação definitiva ocorre no servidor.</p>
                </div>

                <c:if test="${not empty flashMensagem}">
                    <p class="feedback" data-state="${flashTipo}" role="status">${flashMensagem}</p>
                </c:if>

                <form id="form-cadastro" method="post" action="${acaoFormulario}">
                    <c:if test="${not empty livro.id}">
                        <input type="hidden" name="id" value="${livro.id}">
                    </c:if>

                    <fieldset>
                        <legend>Identificação</legend>

                        <div class="form-field ${not empty erros.titulo ? 'invalido' : ''}">
                            <label for="titulo">Título <span class="asterisco-obrigatorio" aria-hidden="true">*</span></label>
                            <input type="text" id="titulo" name="titulo" value="${livro.titulo}" required>
                            <c:if test="${not empty erros.titulo}">
                                <small class="erro-campo">${erros.titulo}</small>
                            </c:if>
                        </div>

                        <div class="form-field ${not empty erros.autor ? 'invalido' : ''}">
                            <label for="autor">Autor(a) <span class="asterisco-obrigatorio" aria-hidden="true">*</span></label>
                            <input type="text" id="autor" name="autor" value="${livro.autor}" required>
                            <c:if test="${not empty erros.autor}">
                                <small class="erro-campo">${erros.autor}</small>
                            </c:if>
                        </div>

                        <div class="form-field ${not empty erros.categoria ? 'invalido' : ''}">
                            <label for="categoria">Categoria <span class="asterisco-obrigatorio" aria-hidden="true">*</span></label>
                            <input type="text" id="categoria" name="categoria" value="${livro.categoria}" required>
                            <c:if test="${not empty erros.categoria}">
                                <small class="erro-campo">${erros.categoria}</small>
                            </c:if>
                        </div>

                        <div class="form-field ${not empty erros.isbn ? 'invalido' : ''}">
                            <label for="isbn">ISBN</label>
                            <input type="text" id="isbn" name="isbn" value="${livro.isbn}" placeholder="Ex: 9788535902778">
                            <c:if test="${not empty erros.isbn}">
                                <small class="erro-campo">${erros.isbn}</small>
                            </c:if>
                        </div>
                    </fieldset>

                    <fieldset>
                        <legend>Disponibilidade</legend>

                        <div class="form-field ${not empty erros.anoPublicacao ? 'invalido' : ''}">
                            <label for="ano">Ano de publicação <span class="asterisco-obrigatorio" aria-hidden="true">*</span></label>
                            <input type="number" id="ano" name="ano" min="1500" max="${anoAtual}" value="${livro.anoPublicacao == 0 ? '' : livro.anoPublicacao}" required>
                            <c:if test="${not empty erros.anoPublicacao}">
                                <small class="erro-campo">${erros.anoPublicacao}</small>
                            </c:if>
                        </div>

                        <div class="form-field ${not empty erros.quantidadeExemplares ? 'invalido' : ''}">
                            <label for="exemplares">Quantidade de exemplares <span class="asterisco-obrigatorio" aria-hidden="true">*</span></label>
                            <input type="number" id="exemplares" name="exemplares" min="0" value="${livro.quantidadeExemplares}" required>
                            <c:if test="${not empty erros.quantidadeExemplares}">
                                <small class="erro-campo">${erros.quantidadeExemplares}</small>
                            </c:if>
                        </div>

                        <div class="form-field ${not empty erros.status ? 'invalido' : ''}">
                            <label for="status">Status <span class="asterisco-obrigatorio" aria-hidden="true">*</span></label>
                            <select id="status" name="status" required>
                                <option value="">Selecione</option>
                                <c:forEach var="statusOpcao" items="${statusLivros}">
                                    <option value="${statusOpcao.name()}" ${livro.status == statusOpcao ? 'selected' : ''}>${statusOpcao.descricao}</option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty erros.status}">
                                <small class="erro-campo">${erros.status}</small>
                            </c:if>
                        </div>
                    </fieldset>

                    <div class="form-actions">
                        <button type="submit">${tituloPagina}</button>
                        <a class="botao-link" href="${pageContext.request.contextPath}/livros">Cancelar</a>
                    </div>
                </form>
            </section>
        </div>
    </main>

    <script src="${pageContext.request.contextPath}/js/validacao.js"></script>
</body>
</html>
