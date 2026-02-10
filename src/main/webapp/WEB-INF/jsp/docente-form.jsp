<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="br.gov.inep.censo.model.Docente" %>
<%@ page import="br.gov.inep.censo.model.LayoutCampo" %>
<%@ page import="br.gov.inep.censo.model.enums.CorRacaEnum" %>
<%@ page import="br.gov.inep.censo.model.enums.NacionalidadeEnum" %>
<%@ page import="br.gov.inep.censo.model.enums.PaisEnum" %>
<%@ page import="br.gov.inep.censo.model.enums.EstadoEnum" %>
<%
    Docente docente = (Docente) request.getAttribute("docente");
    if (docente == null) docente = new Docente();
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
    <title>Docente - Formulario</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Docente</span>
        <h2><%= br.gov.inep.censo.util.ViewUtils.e(docente.getId() == null ? "Novo Docente" : "Alterar Docente") %></h2>
    </header>

    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/docente">Voltar para Listagem</a>
    </div>

    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/docente">
        <input type="hidden" name="acao" value="salvar"/>
        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>
        <% if (docente.getId() != null) { %>
        <input type="hidden" name="id" value="<%= br.gov.inep.censo.util.ViewUtils.e(docente.getId()) %>"/>
        <% } %>

        <div class="form-grid">
            <div class="field">
                <label for="idDocenteIes">ID do Docente na IES</label>
                <input id="idDocenteIes" name="idDocenteIes" type="text" maxlength="20"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(docente.getIdDocenteIes() != null ? docente.getIdDocenteIes() : "") %>"/>
            </div>
            <div class="field">
                <label for="nome">Nome</label>
                <input id="nome" name="nome" type="text" maxlength="120" required
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(docente.getNome() != null ? docente.getNome() : "") %>"/>
            </div>
            <div class="field">
                <label for="cpf">CPF</label>
                <input id="cpf" name="cpf" type="text" maxlength="11" required
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(docente.getCpf() != null ? docente.getCpf() : "") %>"/>
            </div>
            <div class="field">
                <label for="documentoEstrangeiro">Documento estrangeiro</label>
                <input id="documentoEstrangeiro" name="documentoEstrangeiro" type="text" maxlength="20"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(docente.getDocumentoEstrangeiro() != null ? docente.getDocumentoEstrangeiro() : "") %>"/>
            </div>
            <div class="field">
                <label for="dataNascimento">Data de Nascimento</label>
                <input id="dataNascimento" name="dataNascimento" type="date" required
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(docente.getDataNascimento() != null ? docente.getDataNascimento().toString() : "") %>"/>
            </div>
            <div class="field">
                <label for="corRaca">Cor/Raca</label>
                <select id="corRaca" name="corRaca">
                    <option value="">Nao informado</option>
                    <% CorRacaEnum[] cores = CorRacaEnum.values();
                        for (int i = 0; i < cores.length; i++) {
                            CorRacaEnum cor = cores[i]; %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(cor.getCodigo()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(docente.getCorRaca() != null && docente.getCorRaca().intValue() == cor.getCodigo() ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(cor.getCodigo()) %> - <%= br.gov.inep.censo.util.ViewUtils.e(cor.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="nacionalidade">Nacionalidade</label>
                <select id="nacionalidade" name="nacionalidade">
                    <% NacionalidadeEnum[] nacionalidades = NacionalidadeEnum.values();
                        for (int i = 0; i < nacionalidades.length; i++) {
                            NacionalidadeEnum item = nacionalidades[i]; %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(item.getCodigo()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(docente.getNacionalidade() != null && docente.getNacionalidade().intValue() == item.getCodigo() ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(item.getCodigo()) %> - <%= br.gov.inep.censo.util.ViewUtils.e(item.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="paisOrigem">Pais de origem</label>
                <select id="paisOrigem" name="paisOrigem">
                    <% PaisEnum[] paises = PaisEnum.values();
                        for (int i = 0; i < paises.length; i++) {
                            PaisEnum pais = paises[i]; %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(pais.getCodigo()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(pais.getCodigo().equalsIgnoreCase(docente.getPaisOrigem() != null ? docente.getPaisOrigem() : "BRA") ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(pais.getCodigo()) %> - <%= br.gov.inep.censo.util.ViewUtils.e(pais.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="ufNascimento">UF de nascimento</label>
                <select id="ufNascimento" name="ufNascimento">
                    <option value="">Nao informado</option>
                    <% EstadoEnum[] ufs = EstadoEnum.values();
                        for (int i = 0; i < ufs.length; i++) {
                            EstadoEnum uf = ufs[i]; %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(uf.getCodigo()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(docente.getUfNascimento() != null && docente.getUfNascimento().intValue() == uf.getCodigo() ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(uf.getCodigo()) %> - <%= br.gov.inep.censo.util.ViewUtils.e(uf.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="municipioNascimento">Municipio de nascimento (codigo)</label>
                <input id="municipioNascimento" name="municipioNascimento" type="text" maxlength="7"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(docente.getMunicipioNascimento() != null ? docente.getMunicipioNascimento() : "") %>"/>
            </div>
            <div class="field">
                <label for="docenteDeficiencia">Docente com deficiencia / TEA / altas habilidades</label>
                <select id="docenteDeficiencia" name="docenteDeficiencia">
                    <option value="">Nao informado</option>
                    <option value="0" <%= br.gov.inep.censo.util.ViewUtils.e(docente.getDocenteDeficiencia() != null && docente.getDocenteDeficiencia().intValue() == 0 ? "selected" : "") %>>0 - Nao</option>
                    <option value="1" <%= br.gov.inep.censo.util.ViewUtils.e(docente.getDocenteDeficiencia() != null && docente.getDocenteDeficiencia().intValue() == 1 ? "selected" : "") %>>1 - Sim</option>
                    <option value="2" <%= br.gov.inep.censo.util.ViewUtils.e(docente.getDocenteDeficiencia() != null && docente.getDocenteDeficiencia().intValue() == 2 ? "selected" : "") %>>2 - Nao dispoe da informacao</option>
                </select>
            </div>
        </div>

        <h3>Campos complementares do leiaute (Registro 31)</h3>
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

