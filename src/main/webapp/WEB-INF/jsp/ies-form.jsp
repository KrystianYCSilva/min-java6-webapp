<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="br.gov.inep.censo.model.Ies" %>
<%@ page import="br.gov.inep.censo.model.LayoutCampo" %>
<%@ page import="br.gov.inep.censo.model.enums.EstadoEnum" %>
<%@ page import="br.gov.inep.censo.model.enums.TipoLaboratorioEnum" %>
<%
    Ies ies = (Ies) request.getAttribute("ies");
    if (ies == null) ies = new Ies();
    List<LayoutCampo> camposComplementares = (List<LayoutCampo>) request.getAttribute("camposComplementares");
    Map<Long, String> valoresComplementares = (Map<Long, String>) request.getAttribute("valoresComplementares");
    if (valoresComplementares == null) valoresComplementares = new java.util.LinkedHashMap<Long, String>();
    String csrfToken = br.gov.inep.censo.util.ViewUtils.csrf(session);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>IES - Formulario</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">IES</span>
        <h2><%= br.gov.inep.censo.util.ViewUtils.e(ies.getId() == null ? "Nova IES" : "Alterar IES") %></h2>
    </header>

    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies">Voltar para Listagem</a>
    </div>

    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/ies">
        <input type="hidden" name="acao" value="salvar"/>
        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>
        <% if (ies.getId() != null) { %>
        <input type="hidden" name="id" value="<%= br.gov.inep.censo.util.ViewUtils.e(ies.getId()) %>"/>
        <% } %>

        <div class="form-grid">
            <div class="field">
                <label for="idIesInep">ID da IES no INEP</label>
                <input id="idIesInep" name="idIesInep" type="number"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(ies.getIdIesInep() != null ? ies.getIdIesInep() : "") %>"/>
            </div>
            <div class="field">
                <label for="nomeLaboratorio">Nome do laboratorio na IES</label>
                <input id="nomeLaboratorio" name="nomeLaboratorio" type="text" maxlength="200" required
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(ies.getNomeLaboratorio() != null ? ies.getNomeLaboratorio() : "") %>"/>
            </div>
            <div class="field">
                <label for="registroLaboratorioIes">Registro do laboratorio na IES</label>
                <input id="registroLaboratorioIes" name="registroLaboratorioIes" type="text" maxlength="14"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(ies.getRegistroLaboratorioIes() != null ? ies.getRegistroLaboratorioIes() : "") %>"/>
            </div>
            <div class="field">
                <label for="laboratorioAtivoAno">Laboratorio ativo no ano</label>
                <select id="laboratorioAtivoAno" name="laboratorioAtivoAno">
                    <option value="1" <%= br.gov.inep.censo.util.ViewUtils.e(ies.getLaboratorioAtivoAno() != null && ies.getLaboratorioAtivoAno().intValue() == 1 ? "selected" : "") %>>1 - Sim</option>
                    <option value="0" <%= br.gov.inep.censo.util.ViewUtils.e(ies.getLaboratorioAtivoAno() != null && ies.getLaboratorioAtivoAno().intValue() == 0 ? "selected" : "") %>>0 - Nao</option>
                </select>
            </div>
            <div class="field">
                <label for="descricaoAtividades">Descricao das atividades</label>
                <input id="descricaoAtividades" name="descricaoAtividades" type="text" maxlength="2000"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(ies.getDescricaoAtividades() != null ? ies.getDescricaoAtividades() : "") %>"/>
            </div>
            <div class="field">
                <label for="palavrasChave">Palavras-chave</label>
                <input id="palavrasChave" name="palavrasChave" type="text" maxlength="200"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(ies.getPalavrasChave() != null ? ies.getPalavrasChave() : "") %>"/>
            </div>
            <div class="field">
                <label for="laboratorioInformatica">Laboratorio de informatica</label>
                <select id="laboratorioInformatica" name="laboratorioInformatica">
                    <option value="">Nao informado</option>
                    <option value="1" <%= br.gov.inep.censo.util.ViewUtils.e(ies.getLaboratorioInformatica() != null && ies.getLaboratorioInformatica().intValue() == 1 ? "selected" : "") %>>1 - Sim</option>
                    <option value="0" <%= br.gov.inep.censo.util.ViewUtils.e(ies.getLaboratorioInformatica() != null && ies.getLaboratorioInformatica().intValue() == 0 ? "selected" : "") %>>0 - Nao</option>
                </select>
            </div>
            <div class="field">
                <label for="tipoLaboratorio">Tipo de laboratorio</label>
                <select id="tipoLaboratorio" name="tipoLaboratorio">
                    <option value="">Nao informado</option>
                    <% TipoLaboratorioEnum[] tipos = TipoLaboratorioEnum.values();
                        for (int i = 0; i < tipos.length; i++) {
                            TipoLaboratorioEnum tipo = tipos[i]; %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(tipo.getCodigo()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(ies.getTipoLaboratorio() != null && ies.getTipoLaboratorio().intValue() == tipo.getCodigo() ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(tipo.getCodigo()) %> - <%= br.gov.inep.censo.util.ViewUtils.e(tipo.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="codigoUfLaboratorio">UF do laboratorio</label>
                <select id="codigoUfLaboratorio" name="codigoUfLaboratorio">
                    <option value="">Nao informado</option>
                    <% EstadoEnum[] ufs = EstadoEnum.values();
                        for (int i = 0; i < ufs.length; i++) {
                            EstadoEnum uf = ufs[i]; %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(uf.getCodigo()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(ies.getCodigoUfLaboratorio() != null && ies.getCodigoUfLaboratorio().intValue() == uf.getCodigo() ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(uf.getCodigo()) %> - <%= br.gov.inep.censo.util.ViewUtils.e(uf.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="codigoMunicipioLaboratorio">Municipio do laboratorio (codigo)</label>
                <input id="codigoMunicipioLaboratorio" name="codigoMunicipioLaboratorio" type="text" maxlength="7"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(ies.getCodigoMunicipioLaboratorio() != null ? ies.getCodigoMunicipioLaboratorio() : "") %>"/>
            </div>
        </div>

        <h3>Campos complementares do leiaute (Registro 11)</h3>
        <div class="form-grid">
            <% if (camposComplementares != null) {
                for (LayoutCampo campo : camposComplementares) {
                    String valor = valoresComplementares.get(campo.getId()); %>
            <div class="field">
                <label for="extra_<%= br.gov.inep.censo.util.ViewUtils.e(campo.getId()) %>">[<%= br.gov.inep.censo.util.ViewUtils.e(campo.getNumeroCampo()) %>] <%= br.gov.inep.censo.util.ViewUtils.e(campo.getNomeCampo()) %></label>
                <input id="extra_<%= br.gov.inep.censo.util.ViewUtils.e(campo.getId()) %>" name="extra_<%= br.gov.inep.censo.util.ViewUtils.e(campo.getId()) %>" type="text"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(valor != null ? valor : "") %>"/>
            </div>
            <% }
            } %>
        </div>

        <button class="btn" type="submit">Salvar</button>
    </form>
</div>
</body>
</html>

