<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.gov.inep.censo.model.Usuario" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Menu Principal - Censo 2025</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Formulario Oficial</span>
        <h2>Menu Principal - Censo da Educacao Superior 2025</h2>
        <p>
            Usuario autenticado:
            <strong><%= br.gov.inep.censo.util.ViewUtils.e(usuario != null ? usuario.getNome() : "N/D") %></strong>
            (<%= br.gov.inep.censo.util.ViewUtils.e(usuario != null ? usuario.getLogin() : "-") %>)
        </p>
    </header>

    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/home.jsp">Home</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/logout">Sair</a>
    </div>

    <section class="menu-grid">
        <article class="module-card">
            <h3>Modulo Aluno 2025</h3>
            <p>
                Campos centrais do leiaute: ID do Aluno no INEP, Nome, CPF,
                Data de Nascimento, Nacionalidade e Pais de origem.
            </p>
            <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno">Abrir Modulo Aluno</a>
            <div class="status-chip">Registro 41</div>
        </article>

        <article class="module-card">
            <h3>Modulo Curso 2025</h3>
            <p>
                Campos centrais do leiaute: Codigo do Curso no E-MEC,
                Curso teve aluno vinculado, Nome, Nivel academico e Formato de oferta.
            </p>
            <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso">Abrir Modulo Curso</a>
            <div class="status-chip">Registro 21</div>
        </article>

        <article class="module-card">
            <h3>Modulo CursoAluno 2025</h3>
            <p>
                Bloco Registro 42 do leiaute de aluno:
                vinculo Aluno x Curso, situacao de vinculo e forma de ingresso/selecao.
            </p>
            <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso-aluno">Abrir Registro 42</a>
            <div class="status-chip">Registro 42</div>
        </article>

        <article class="module-card">
            <h3>Modulo Docente 2025</h3>
            <p>
                Registro 31 de docente com dados cadastrais, nacionalidade, pais/UF/municipio
                e campos complementares do leiaute.
            </p>
            <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/docente">Abrir Modulo Docente</a>
            <div class="status-chip">Registro 31</div>
        </article>

        <article class="module-card">
            <h3>Modulo IES 2025</h3>
            <p>
                Registro 11 (laboratorio) com tipo de laboratorio, localizacao,
                importacao/exportacao TXT pipe e campos complementares de leiaute.
            </p>
            <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies">Abrir Modulo IES</a>
            <div class="status-chip">Registro 11</div>
        </article>
    </section>

    <div class="footer-note">
        Visual inspirado em documentos PDF institucionais: bordas fortes, tipografia serifada e estrutura tabular.
    </div>
</div>
</body>
</html>

