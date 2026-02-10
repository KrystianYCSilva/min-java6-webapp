<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.gov.inep.censo.model.Aluno" %>
<%
    List<Aluno> alunos = (List<Aluno>) request.getAttribute("alunos");
    Integer paginaAtual = (Integer) request.getAttribute("paginaAtual");
    Integer totalPaginas = (Integer) request.getAttribute("totalPaginas");
    Integer totalRegistros = (Integer) request.getAttribute("totalRegistros");
    String erro = (String) request.getAttribute("erro");
    String flashAlunoMessage = (String) session.getAttribute("flashAlunoMessage");
    if (flashAlunoMessage != null) {
        session.removeAttribute("flashAlunoMessage");
    }
    if (paginaAtual == null) paginaAtual = Integer.valueOf(1);
    if (totalPaginas == null) totalPaginas = Integer.valueOf(1);
    if (totalRegistros == null) totalRegistros = Integer.valueOf(0);
    String csrfToken = br.gov.inep.censo.util.ViewUtils.csrf(session);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Alunos - Listagem</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Aluno</span>
        <h2>Listagem de Alunos (Registro 41)</h2>
        <p>Total de registros: <strong><%= br.gov.inep.censo.util.ViewUtils.e(totalRegistros) %></strong></p>
    </header>

    <% if (erro != null) { %>
    <div class="alert"><%= br.gov.inep.censo.util.ViewUtils.e(erro) %></div>
    <% } %>
    <% if (flashAlunoMessage != null) { %>
    <div class="ok"><%= br.gov.inep.censo.util.ViewUtils.e(flashAlunoMessage) %></div>
    <% } %>

    <div class="top-actions">
        <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno?acao=form">Incluir via Formulario</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno?acao=exportar">Exportar TXT (pipe)</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/menu">Menu</a>
    </div>

    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno">
        <input type="hidden" name="acao" value="importar"/>
        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>
        <div class="field field-full">
            <label for="txtConteudo">Importar TXT pipe (1 registro por linha, leiaute Registro 41)</label>
            <textarea id="txtConteudo" name="txtConteudo" rows="5"
                      style="width:100%; border:1px solid #666; font-family:'Courier New', monospace;"></textarea>
        </div>
        <button class="btn" type="submit">Importar TXT</button>
    </form>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>ID INEP</th>
                <th>Nome</th>
                <th>CPF</th>
                <th>Nascimento</th>
                <th>Nacionalidade</th>
                <th>Acoes</th>
            </tr>
            </thead>
            <tbody>
            <% if (alunos == null || alunos.isEmpty()) { %>
            <tr><td colspan="7">Nenhum aluno encontrado.</td></tr>
            <% } else {
                for (Aluno aluno : alunos) { %>
            <tr>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getIdAlunoInep() != null ? aluno.getIdAlunoInep() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getNome()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getCpf()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getDataNascimento()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getNacionalidade()) %></td>
                <td>
                    <a href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno?acao=form&id=<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %>">Alterar</a>
                    |
                    <a href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno?acao=mostrar&id=<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %>">Mostrar</a>
                    |
                    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno"
                          style="display:inline; margin:0;">
                        <input type="hidden" name="acao" value="excluir"/>
                        <input type="hidden" name="id" value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %>"/>
                        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>
                        <button type="submit"
                                style="background:none;border:none;padding:0;color:#0645AD;text-decoration:underline;cursor:pointer;font:inherit;"
                                onclick="return confirm('Confirma exclusao do aluno?');">Excluir</button>
                    </form>
                    |
                    <a href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno?acao=exportar-item&id=<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %>">Exportar TXT</a>
                </td>
            </tr>
            <% }
            } %>
            </tbody>
        </table>
    </div>

    <div class="top-actions" style="margin-top: 12px;">
        <% if (paginaAtual.intValue() > 1) { %>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno?pagina=<%= br.gov.inep.censo.util.ViewUtils.e(paginaAtual.intValue()-1) %>">Pagina Anterior</a>
        <% } %>
        <span>Pagina <strong><%= br.gov.inep.censo.util.ViewUtils.e(paginaAtual) %></strong> de <strong><%= br.gov.inep.censo.util.ViewUtils.e(totalPaginas) %></strong></span>
        <% if (paginaAtual.intValue() < totalPaginas.intValue()) { %>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno?pagina=<%= br.gov.inep.censo.util.ViewUtils.e(paginaAtual.intValue()+1) %>">Proxima Pagina</a>
        <% } %>
    </div>
</div>
</body>
</html>

