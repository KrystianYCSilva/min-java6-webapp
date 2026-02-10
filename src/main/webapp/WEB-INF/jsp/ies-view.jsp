<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="br.gov.inep.censo.model.Ies" %>
<%
    Ies ies = (Ies) request.getAttribute("ies");
    Map<Long, String> camposComplementaresValores = (Map<Long, String>) request.getAttribute("camposComplementaresValores");
    Map<Long, String> camposComplementaresRotulos = (Map<Long, String>) request.getAttribute("camposComplementaresRotulos");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>IES - Visualizacao</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">IES</span>
        <h2>Visualizar IES</h2>
    </header>
    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies">Voltar para Listagem</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies?acao=form&id=<%= br.gov.inep.censo.util.ViewUtils.e(ies.getId()) %>">Alterar</a>
    </div>

    <div class="table-wrap">
        <table>
            <tbody>
            <tr><th>ID</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getId()) %></td></tr>
            <tr><th>ID IES INEP</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getIdIesInep()) %></td></tr>
            <tr><th>Nome laboratorio</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getNomeLaboratorio()) %></td></tr>
            <tr><th>Registro laboratorio IES</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getRegistroLaboratorioIes()) %></td></tr>
            <tr><th>Laboratorio ativo</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getLaboratorioAtivoAno()) %></td></tr>
            <tr><th>Descricao atividades</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getDescricaoAtividades()) %></td></tr>
            <tr><th>Palavras-chave</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getPalavrasChave()) %></td></tr>
            <tr><th>Laboratorio de informatica</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getLaboratorioInformatica()) %></td></tr>
            <tr><th>Tipo de laboratorio</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getTipoLaboratorioEnum() != null ? ies.getTipoLaboratorioEnum().getDescricao() + " (" + ies.getTipoLaboratorio() + ")" : ies.getTipoLaboratorio()) %></td></tr>
            <tr><th>UF laboratorio</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getUfLaboratorioEnum() != null ? ies.getUfLaboratorioEnum().getDescricao() + " (" + ies.getCodigoUfLaboratorio() + ")" : ies.getCodigoUfLaboratorio()) %></td></tr>
            <tr><th>Municipio laboratorio</th><td><%= br.gov.inep.censo.util.ViewUtils.e(ies.getCodigoMunicipioLaboratorio()) %></td></tr>
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

