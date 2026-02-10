<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="br.gov.inep.censo.model.Curso" %>
<%@ page import="br.gov.inep.censo.model.enums.NivelAcademicoEnum" %>
<%@ page import="br.gov.inep.censo.model.enums.FormatoOfertaEnum" %>
<%
    Curso curso = (Curso) request.getAttribute("curso");
    Map<Long, String> camposComplementaresValores = (Map<Long, String>) request.getAttribute("camposComplementaresValores");
    Map<Long, String> camposComplementaresRotulos = (Map<Long, String>) request.getAttribute("camposComplementaresRotulos");
    NivelAcademicoEnum nivel = curso != null ? curso.getNivelAcademicoEnum() : null;
    FormatoOfertaEnum formato = curso != null ? curso.getFormatoOfertaEnum() : null;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Curso - Visualizacao</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Curso</span>
        <h2>Visualizar Curso</h2>
    </header>
    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso">Voltar para Listagem</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso?acao=form&id=<%= br.gov.inep.censo.util.ViewUtils.e(curso.getId()) %>">Alterar</a>
    </div>

    <div class="table-wrap">
        <table>
            <tbody>
            <tr><th>ID</th><td><%= br.gov.inep.censo.util.ViewUtils.e(curso.getId()) %></td></tr>
            <tr><th>Codigo E-MEC</th><td><%= br.gov.inep.censo.util.ViewUtils.e(curso.getCodigoCursoEmec()) %></td></tr>
            <tr><th>Nome</th><td><%= br.gov.inep.censo.util.ViewUtils.e(curso.getNome()) %></td></tr>
            <tr><th>Nivel Academico</th><td><%= br.gov.inep.censo.util.ViewUtils.e(nivel != null ? nivel.getDescricao() + " (" + nivel.getCodigo() + ")" : curso.getNivelAcademico()) %></td></tr>
            <tr><th>Formato de Oferta</th><td><%= br.gov.inep.censo.util.ViewUtils.e(formato != null ? formato.getDescricao() + " (" + formato.getCodigo() + ")" : curso.getFormatoOferta()) %></td></tr>
            <tr><th>Teve aluno vinculado</th><td><%= br.gov.inep.censo.util.ViewUtils.e(curso.getCursoTeveAlunoVinculado()) %></td></tr>
            <tr><th>Recursos assistivos</th><td><%= br.gov.inep.censo.util.ViewUtils.e(curso.getRecursosTecnologiaAssistivaResumo()) %></td></tr>
            </tbody>
        </table>
    </div>

    <h3>Campos complementares do leiaute</h3>
    <div class="table-wrap">
        <table>
            <thead><tr><th>Campo</th><th>Valor</th></tr></thead>
            <tbody>
            <% if (camposComplementaresValores == null || camposComplementaresValores.isEmpty()) { %>
            <tr><td colspan="2">Nenhum campo complementar informado.</td></tr>
            <% } else {
                for (Map.Entry<Long, String> entry : camposComplementaresValores.entrySet()) {
                    String label = camposComplementaresRotulos != null ? camposComplementaresRotulos.get(entry.getKey()) : null;
            %>
            <tr>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(label != null ? label : entry.getKey()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(entry.getValue()) %></td>
            </tr>
            <% }
            } %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>

