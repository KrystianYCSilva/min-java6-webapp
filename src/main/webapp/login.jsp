<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String erro = (String) request.getAttribute("erro");
    String logout = request.getParameter("logout");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Login - Censo 2025</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet" style="max-width: 560px;">
    <header class="sheet-header">
        <span class="official-tag">Acesso Restrito</span>
        <h2>Autenticacao do Usuario</h2>
        <p>Use as credenciais cadastradas na tabela <code>usuario</code>.</p>
    </header>

    <% if (erro != null) { %>
    <div class="alert"><%= br.gov.inep.censo.util.ViewUtils.e(erro) %></div>
    <% } %>

    <% if (logout != null) { %>
    <div class="ok">Sessao encerrada com sucesso.</div>
    <% } %>

    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/login">
        <div class="form-grid" style="grid-template-columns: 1fr;">
            <div class="field field-full">
                <label for="login">Login</label>
                <input id="login" name="login" type="text" maxlength="50" required/>
            </div>
            <div class="field field-full">
                <label for="senha">Senha</label>
                <input id="senha" name="senha" type="password" maxlength="100" required/>
            </div>
        </div>
        <button class="btn" type="submit">Entrar</button>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/home.jsp">Voltar</a>
    </form>

    <div class="footer-note">
        Credencial inicial: <code>admin</code> / <code>admin123</code>.
    </div>
</div>
</body>
</html>

