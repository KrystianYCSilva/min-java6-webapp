<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map" %>
<%@ page import="br.gov.inep.censo.model.Aluno" %>
<%@ page import="br.gov.inep.censo.model.OpcaoDominio" %>
<%@ page import="br.gov.inep.censo.model.LayoutCampo" %>
<%@ page import="br.gov.inep.censo.model.enums.CorRacaEnum" %>
<%@ page import="br.gov.inep.censo.model.enums.NacionalidadeEnum" %>
<%
    Aluno aluno = (Aluno) request.getAttribute("aluno");
    if (aluno == null) aluno = new Aluno();
    List<OpcaoDominio> opcoesDeficiencia = (List<OpcaoDominio>) request.getAttribute("opcoesDeficiencia");
    List<LayoutCampo> camposComplementares = (List<LayoutCampo>) request.getAttribute("camposComplementares");
    Set<Long> opcoesDeficienciaSelecionadas = (Set<Long>) request.getAttribute("opcoesDeficienciaSelecionadas");
    Map<Long, String> valoresComplementares = (Map<Long, String>) request.getAttribute("valoresComplementares");
    if (valoresComplementares == null) valoresComplementares = new java.util.LinkedHashMap<Long, String>();
    String csrfToken = br.gov.inep.censo.util.ViewUtils.csrf(session);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Aluno - Formulario</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Aluno</span>
        <h2><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId() == null ? "Novo Aluno" : "Alterar Aluno") %></h2>
    </header>

    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno">Voltar para Listagem</a>
    </div>

    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/aluno">
        <input type="hidden" name="acao" value="salvar"/>
        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>
        <% if (aluno.getId() != null) { %>
        <input type="hidden" name="id" value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %>"/>
        <% } %>

        <div class="form-grid">
            <div class="field">
                <label for="idAlunoInep">ID do Aluno no INEP</label>
                <input id="idAlunoInep" name="idAlunoInep" type="number" value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getIdAlunoInep() != null ? aluno.getIdAlunoInep() : "") %>"/>
            </div>
            <div class="field">
                <label for="nome">Nome</label>
                <input id="nome" name="nome" type="text" required value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getNome() != null ? aluno.getNome() : "") %>"/>
            </div>
            <div class="field">
                <label for="cpf">CPF</label>
                <input id="cpf" name="cpf" type="text" maxlength="11" required value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getCpf() != null ? aluno.getCpf() : "") %>"/>
            </div>
            <div class="field">
                <label for="dataNascimento">Data de Nascimento</label>
                <input id="dataNascimento" name="dataNascimento" type="date"
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getDataNascimento() != null ? aluno.getDataNascimento().toString() : "") %>" required/>
            </div>
            <div class="field">
                <label for="corRaca">Cor/Raca</label>
                <select id="corRaca" name="corRaca">
                    <option value="">Nao informado</option>
                    <% CorRacaEnum[] cores = CorRacaEnum.values();
                        for (int i = 0; i < cores.length; i++) {
                            CorRacaEnum cor = cores[i];
                    %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(cor.getCodigo()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(aluno.getCorRaca() != null && aluno.getCorRaca().intValue() == cor.getCodigo() ? "selected" : "") %>>
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
                            NacionalidadeEnum item = nacionalidades[i];
                    %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(item.getCodigo()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(aluno.getNacionalidade() != null && aluno.getNacionalidade().intValue() == item.getCodigo() ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(item.getCodigo()) %> - <%= br.gov.inep.censo.util.ViewUtils.e(item.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="ufNascimento">UF de nascimento</label>
                <input id="ufNascimento" name="ufNascimento" type="text" maxlength="2" value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getUfNascimento() != null ? aluno.getUfNascimento() : "") %>"/>
            </div>
            <div class="field">
                <label for="municipioNascimento">Municipio de nascimento</label>
                <input id="municipioNascimento" name="municipioNascimento" type="text" value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getMunicipioNascimento() != null ? aluno.getMunicipioNascimento() : "") %>"/>
            </div>
            <div class="field">
                <label for="paisOrigem">Pais de origem</label>
                <input id="paisOrigem" name="paisOrigem" type="text" maxlength="3" value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getPaisOrigem() != null ? aluno.getPaisOrigem() : "BRA") %>"/>
            </div>
        </div>

        <h3>Tipos de Deficiencia (1..N)</h3>
        <div class="checkbox-grid">
            <% if (opcoesDeficiencia != null) {
                for (OpcaoDominio op : opcoesDeficiencia) {
                    boolean checked = opcoesDeficienciaSelecionadas != null && opcoesDeficienciaSelecionadas.contains(op.getId());
            %>
            <label class="checkbox-item">
                <input type="checkbox" name="opcaoDeficiencia" value="<%= br.gov.inep.censo.util.ViewUtils.e(op.getId()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(checked ? "checked" : "") %>/>
                <span><%= br.gov.inep.censo.util.ViewUtils.e(op.getNome()) %></span>
            </label>
            <% }
            } %>
        </div>

        <h3>Campos complementares do Leiaute</h3>
        <div class="form-grid">
            <% if (camposComplementares != null) {
                for (LayoutCampo campo : camposComplementares) {
                    String valor = valoresComplementares.get(campo.getId());
            %>
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

