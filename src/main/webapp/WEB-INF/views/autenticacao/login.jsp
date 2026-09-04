<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acesso | Biblioteca Boa Leitura</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body class="pagina-login">
    <main id="conteudo-principal" class="login-principal">
        <section class="login-painel" aria-labelledby="login-titulo">
            <div class="marca-parceira" aria-label="Cliente parceiro Boa Leitura"><strong>BL</strong><span>Boa Leitura</span></div>
            <p class="site-header__eyebrow">Biblioteca Municipal</p>
            <h1 id="login-titulo">Acessar o acervo</h1>
            <c:if test="${not empty sessionScope.erroLogin}">
                <p class="feedback" data-state="erro" role="alert">${sessionScope.erroLogin}</p>
                <c:remove var="erroLogin" scope="session" />
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/login">
                <input type="hidden" name="destino" value="<c:out value='${param.destino}' />">
                <div class="form-field">
                    <label for="email">E-mail</label>
                    <input id="email" name="email" type="email" autocomplete="username" maxlength="254" required>
                </div>
                <div class="form-field">
                    <label for="senha">Senha</label>
                    <input id="senha" name="senha" type="password" autocomplete="current-password" required>
                </div>
                <button type="submit">Entrar</button>
            </form>
        </section>
    </main>
</body>
</html>
