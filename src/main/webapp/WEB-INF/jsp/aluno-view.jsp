<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="br.gov.inep.censo.model.Aluno" %>
<%
    Aluno aluno = (Aluno) request.getAttribute("aluno");
    Map<Long, String> camposComplementaresValores = (Map<Long, String>) request.getAttribute("camposComplementaresValores");
    Map<Long, String> camposComplementaresRotulos = (Map<Long, String>) request.getAttribute("camposComplementaresRotulos");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Aluno - Visualizacao</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Aluno</span>
        <h2>Visualizar Aluno</h2>
    </header>
    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno">Voltar para Listagem</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno?acao=form&id=<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %>">Alterar</a>
    </div>

    <div class="table-wrap">
        <table>
            <tbody>
            <tr><th>ID</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %></td></tr>
            <tr><th>ID INEP</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getIdAlunoInep()) %></td></tr>
            <tr><th>Nome</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getNome()) %></td></tr>
            <tr><th>CPF</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getCpf()) %></td></tr>
            <tr><th>Data de Nascimento</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getDataNascimento()) %></td></tr>
            <tr><th>Cor/Raca</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getCorRacaEnum() != null ? aluno.getCorRacaEnum().getDescricao() + " (" + aluno.getCorRaca() + ")" : aluno.getCorRaca()) %></td></tr>
            <tr><th>Nacionalidade</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getNacionalidadeEnum() != null ? aluno.getNacionalidadeEnum().getDescricao() + " (" + aluno.getNacionalidade() + ")" : aluno.getNacionalidade()) %></td></tr>
            <tr><th>UF</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getUfNascimento()) %></td></tr>
            <tr><th>Municipio</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getMunicipioNascimento()) %></td></tr>
            <tr><th>Pais</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getPaisOrigem()) %></td></tr>
            <tr><th>Tipos Deficiencia</th><td><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getTiposDeficienciaResumo()) %></td></tr>
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
                for (Map.Entry<Long, String> entry : camposComplementaresValores.entrySet()) { %>
            <tr>
                <td><%= br.gov.inep.censo.util.ViewUtils.e(camposComplementaresRotulos != null && camposComplementaresRotulos.get(entry.getKey()) != null ? camposComplementaresRotulos.get(entry.getKey()) : entry.getKey()) %></td>
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

