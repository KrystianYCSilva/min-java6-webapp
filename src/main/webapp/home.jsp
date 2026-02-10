<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String flashHomeMessage = (String) session.getAttribute("flashHomeMessage");
    if (flashHomeMessage != null) {
        session.removeAttribute("flashHomeMessage");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Censo da Educacao Superior 2025</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Documento Oficial</span>
        <h1>Censo da Educacao Superior 2025</h1>
        <p>Prototipo funcional em Java 6, Servlet 2.5, JSP e JDBC.</p>
    </header>

    <p>
        Este ambiente simula os modulos de coleta para <strong>Aluno</strong>, <strong>Curso</strong>,
        <strong>CursoAluno</strong>, <strong>Docente</strong> e <strong>IES</strong>,
        com estrutura de banco relacional e autenticacao local.
    </p>

    <% if (flashHomeMessage != null) { %>
    <div class="ok"><%= br.gov.inep.censo.util.ViewUtils.e(flashHomeMessage) %></div>
    <% } %>

    <div class="top-actions">
        <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/login">Entrar no Sistema</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/menu">Ir para Menu (requer login)</a>
    </div>

    <div class="footer-note">
        Stack: Tomcat 6/7, Java 6, Maven, JDBC puro e banco embarcado H2.
    </div>
</div>
</body>
</html>

