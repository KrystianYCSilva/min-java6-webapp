<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.gov.inep.censo.model.Ies" %>
<%
    List<Ies> iesItens = (List<Ies>) request.getAttribute("iesItens");
    Integer paginaAtual = (Integer) request.getAttribute("paginaAtual");
    Integer totalPaginas = (Integer) request.getAttribute("totalPaginas");
    Integer totalRegistros = (Integer) request.getAttribute("totalRegistros");
    String erro = (String) request.getAttribute("erro");
    String flashIesMessage = (String) session.getAttribute("flashIesMessage");
    if (flashIesMessage != null) {
        session.removeAttribute("flashIesMessage");
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
    <title>IES - Listagem</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">IES</span>
        <h2>Listagem de IES (Registro 11 - Laboratorio)</h2>
        <p>Total de registros: <strong><%= br.gov.inep.censo.util.ViewUtils.e(totalRegistros) %></strong></p>
    </header>

    <% if (erro != null) { %>
    <div class="alert"><%= br.gov.inep.censo.util.ViewUtils.e(erro) %></div>
    <% } %>
    <% if (flashIesMessage != null) { %>
    <div class="ok"><%= br.gov.inep.censo.util.ViewUtils.e(flashIesMessage) %></div>
    <% } %>

    <div class="top-actions">
        <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies?acao=form">Incluir via Formulario</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies?acao=exportar">Exportar TXT (pipe)</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/menu">Menu</a>
    </div>

    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies">
        <input type="hidden" name="acao" value="importar"/>
        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>
        <div class="field field-full">
            <label for="txtConteudo">Importar TXT pipe (1 registro por linha, leiaute Registro 11)</label>
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
                <th>ID IES INEP</th>
                <th>Nome Laboratorio</th>
                <th>Registro Laboratorio</th>
                <th>Tipo Laboratorio</th>
                <th>UF</th>
                <th>Municipio</th>
                <th>Acoes</th>
            </tr>
            </thead>
            <tbody>
            <% if (iesItens == null || iesItens.isEmpty()) { %>
            <tr><td colspan="8">Nenhum registro de IES encontrado.</td></tr>
            <% } else {
                for (Ies item : iesItens) { %>
            <tr>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getIdIesInep() != null ? item.getIdIesInep() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getNomeLaboratorio()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getRegistroLaboratorioIes() != null ? item.getRegistroLaboratorioIes() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getTipoLaboratorioEnum() != null ? item.getTipoLaboratorioEnum().getDescricao() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getUfLaboratorioEnum() != null ? item.getUfLaboratorioEnum().getDescricao() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getCodigoMunicipioLaboratorio() != null ? item.getCodigoMunicipioLaboratorio() : "-") %></td>
                <td>
                    <a href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies?acao=form&id=<%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %>">Alterar</a>
                    |
                    <a href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies?acao=mostrar&id=<%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %>">Mostrar</a>
                    |
                    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies"
                          style="display:inline; margin:0;">
                        <input type="hidden" name="acao" value="excluir"/>
                        <input type="hidden" name="id" value="<%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %>"/>
                        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>
                        <button type="submit"
                                style="background:none;border:none;padding:0;color:#0645AD;text-decoration:underline;cursor:pointer;font:inherit;"
                                onclick="return confirm('Confirma exclusao da IES?');">Excluir</button>
                    </form>
                    |
                    <a href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies?acao=exportar-item&id=<%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %>">Exportar TXT</a>
                </td>
            </tr>
            <% }
            } %>
            </tbody>
        </table>
    </div>

    <div class="top-actions" style="margin-top: 12px;">
        <% if (paginaAtual.intValue() > 1) { %>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies?pagina=<%= br.gov.inep.censo.util.ViewUtils.e(paginaAtual.intValue()-1) %>">Pagina Anterior</a>
        <% } %>
        <span>Pagina <strong><%= br.gov.inep.censo.util.ViewUtils.e(paginaAtual) %></strong> de <strong><%= br.gov.inep.censo.util.ViewUtils.e(totalPaginas) %></strong></span>
        <% if (paginaAtual.intValue() < totalPaginas.intValue()) { %>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies?pagina=<%= br.gov.inep.censo.util.ViewUtils.e(paginaAtual.intValue()+1) %>">Proxima Pagina</a>
        <% } %>
    </div>
</div>
</body>
</html>

