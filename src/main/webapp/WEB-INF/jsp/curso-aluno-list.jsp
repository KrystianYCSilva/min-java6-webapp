<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.gov.inep.censo.model.CursoAluno" %>
<%
    List<CursoAluno> vinculos = (List<CursoAluno>) request.getAttribute("vinculos");
    String erro = (String) request.getAttribute("erro");
    String flashCursoAlunoMessage = (String) session.getAttribute("flashCursoAlunoMessage");
    if (flashCursoAlunoMessage != null) {
        session.removeAttribute("flashCursoAlunoMessage");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>CursoAluno - Listagem</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Modulo Vinculo</span>
        <h2>Listagem de Vínculos Aluno x Curso (Registro 42)</h2>
    </header>

    <% if (erro != null) { %>
    <div class="alert"><%= br.gov.inep.censo.util.ViewUtils.e(erro) %></div>
    <% } %>
    <% if (flashCursoAlunoMessage != null) { %>
    <div class="ok"><%= br.gov.inep.censo.util.ViewUtils.e(flashCursoAlunoMessage) %></div>
    <% } %>

    <div class="top-actions">
        <a class="btn" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso-aluno?acao=form">Incluir via Formulario</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/menu">Menu</a>
    </div>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Periodo</th>
                <th>Aluno</th>
                <th>ID Aluno IES</th>
                <th>Curso</th>
                <th>Codigo Curso E-MEC</th>
                <th>Turno</th>
                <th>Situacao Vinculo</th>
                <th>Semestre Ingresso</th>
                <th>Financiamentos</th>
                <th>Apoio Social</th>
                <th>Atividades</th>
                <th>Reservas</th>
            </tr>
            </thead>
            <tbody>
            <% if (vinculos == null || vinculos.isEmpty()) { %>
            <tr>
                <td colspan="13">Nenhum Registro 42 cadastrado.</td>
            </tr>
            <% } else {
                for (CursoAluno item : vinculos) { %>
            <tr>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getPeriodoReferencia()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getAlunoNome()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getIdAlunoIes()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getCursoNome()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getCodigoCursoEmec()) %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getTurnoAluno() != null ? item.getTurnoAluno() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getSituacaoVinculo() != null ? item.getSituacaoVinculo() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getSemestreIngresso() != null ? item.getSemestreIngresso() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getFinanciamentosResumo() != null ? item.getFinanciamentosResumo() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getApoioSocialResumo() != null ? item.getApoioSocialResumo() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getAtividadesResumo() != null ? item.getAtividadesResumo() : "-") %></td>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(item.getReservasResumo() != null ? item.getReservasResumo() : "-") %></td>
            </tr>
            <% }
            } %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>

