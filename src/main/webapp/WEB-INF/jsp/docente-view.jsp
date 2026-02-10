<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="br.gov.inep.censo.model.Docente" %>
<%
    Docente docente = (Docente) request.getAttribute("docente");
    Map<Long, String> camposComplementaresValores = (Map<Long, String>) request.getAttribute("camposComplementaresValores");
    Map<Long, String> camposComplementaresRotulos = (Map<Long, String>) request.getAttribute("camposComplementaresRotulos");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Docente - Visualizacao</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Docente</span>
        <h2>Visualizar Docente</h2>
    </header>
    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/docente">Voltar para Listagem</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/docente?acao=form&id=<%= br.gov.inep.censo.util.ViewUtils.e(docente.getId()) %>">Alterar</a>
    </div>

    <div class="table-wrap">
        <table>
            <tbody>
            <tr><th>ID</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getId()) %></td></tr>
            <tr><th>ID Docente IES</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getIdDocenteIes()) %></td></tr>
            <tr><th>Nome</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getNome()) %></td></tr>
            <tr><th>CPF</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getCpf()) %></td></tr>
            <tr><th>Documento estrangeiro</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getDocumentoEstrangeiro()) %></td></tr>
            <tr><th>Data de Nascimento</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getDataNascimento()) %></td></tr>
            <tr><th>Cor/Raca</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getCorRacaEnum() != null ? docente.getCorRacaEnum().getDescricao() + " (" + docente.getCorRaca() + ")" : docente.getCorRaca()) %></td></tr>
            <tr><th>Nacionalidade</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getNacionalidadeEnum() != null ? docente.getNacionalidadeEnum().getDescricao() + " (" + docente.getNacionalidade() + ")" : docente.getNacionalidade()) %></td></tr>
            <tr><th>Pais</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getPaisOrigemEnum() != null ? docente.getPaisOrigemEnum().getDescricao() + " (" + docente.getPaisOrigem() + ")" : docente.getPaisOrigem()) %></td></tr>
            <tr><th>UF</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getUfNascimentoEnum() != null ? docente.getUfNascimentoEnum().getDescricao() + " (" + docente.getUfNascimento() + ")" : docente.getUfNascimento()) %></td></tr>
            <tr><th>Municipio</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getMunicipioNascimento()) %></td></tr>
            <tr><th>Deficiencia / TEA / altas habilidades</th><td><%= br.gov.inep.censo.util.ViewUtils.e(docente.getDocenteDeficiencia()) %></td></tr>
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

