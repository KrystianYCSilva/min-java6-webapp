<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map" %>
<%@ page import="br.gov.inep.censo.model.Curso" %>
<%@ page import="br.gov.inep.censo.model.OpcaoDominio" %>
<%@ page import="br.gov.inep.censo.model.LayoutCampo" %>
<%@ page import="br.gov.inep.censo.model.enums.NivelAcademicoEnum" %>
<%@ page import="br.gov.inep.censo.model.enums.FormatoOfertaEnum" %>
<%
    Curso curso = (Curso) request.getAttribute("curso");
    if (curso == null) curso = new Curso();
    List<OpcaoDominio> recursosAssistivos = (List<OpcaoDominio>) request.getAttribute("recursosAssistivos");
    List<LayoutCampo> camposComplementares = (List<LayoutCampo>) request.getAttribute("camposComplementares");
    Set<Long> recursosSelecionados = (Set<Long>) request.getAttribute("recursosSelecionados");
    Map<Long, String> valoresComplementares = (Map<Long, String>) request.getAttribute("valoresComplementares");
    if (valoresComplementares == null) valoresComplementares = new java.util.LinkedHashMap<Long, String>();
    String csrfToken = br.gov.inep.censo.util.ViewUtils.csrf(session);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Curso - Formulario</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Curso</span>
        <h2><%= br.gov.inep.censo.util.ViewUtils.e(curso.getId() == null ? "Novo Curso" : "Alterar Curso") %></h2>
    </header>

    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso">Voltar para Listagem</a>
    </div>

    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso">
        <input type="hidden" name="acao" value="salvar"/>
        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>
        <% if (curso.getId() != null) { %>
        <input type="hidden" name="id" value="<%= br.gov.inep.censo.util.ViewUtils.e(curso.getId()) %>"/>
        <% } %>

        <div class="form-grid">
            <div class="field">
                <label for="codigoCursoEmec">Codigo do Curso no E-MEC</label>
                <input id="codigoCursoEmec" name="codigoCursoEmec" type="text" maxlength="12" required
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(curso.getCodigoCursoEmec() != null ? curso.getCodigoCursoEmec() : "") %>"/>
            </div>
            <div class="field">
                <label for="nome">Nome do Curso</label>
                <input id="nome" name="nome" type="text" maxlength="160" required
                       value="<%= br.gov.inep.censo.util.ViewUtils.e(curso.getNome() != null ? curso.getNome() : "") %>"/>
            </div>
            <div class="field">
                <label for="nivelAcademico">Nivel Academico</label>
                <select id="nivelAcademico" name="nivelAcademico">
                    <% NivelAcademicoEnum[] niveis = NivelAcademicoEnum.values();
                        for (int i = 0; i < niveis.length; i++) {
                            NivelAcademicoEnum nivel = niveis[i];
                    %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(nivel.getCodigo()) %>"
                        <%= br.gov.inep.censo.util.ViewUtils.e(nivel.getCodigo().equalsIgnoreCase(curso.getNivelAcademico() != null ? curso.getNivelAcademico() : "") ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(nivel.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="formatoOferta">Formato de Oferta</label>
                <select id="formatoOferta" name="formatoOferta">
                    <% FormatoOfertaEnum[] formatos = FormatoOfertaEnum.values();
                        for (int i = 0; i < formatos.length; i++) {
                            FormatoOfertaEnum formato = formatos[i];
                    %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(formato.getCodigo()) %>"
                        <%= br.gov.inep.censo.util.ViewUtils.e(formato.getCodigo().equalsIgnoreCase(curso.getFormatoOferta() != null ? curso.getFormatoOferta() : "") ? "selected" : "") %>>
                        <%= br.gov.inep.censo.util.ViewUtils.e(formato.getDescricao()) %>
                    </option>
                    <% } %>
                </select>
            </div>
            <div class="field">
                <label for="cursoTeveAlunoVinculado">Curso teve aluno vinculado no ano do Censo?</label>
                <select id="cursoTeveAlunoVinculado" name="cursoTeveAlunoVinculado">
                    <option value="1" <%= br.gov.inep.censo.util.ViewUtils.e(curso.getCursoTeveAlunoVinculado() != null && curso.getCursoTeveAlunoVinculado().intValue() == 1 ? "selected" : "") %>>1 - Sim</option>
                    <option value="0" <%= br.gov.inep.censo.util.ViewUtils.e(curso.getCursoTeveAlunoVinculado() != null && curso.getCursoTeveAlunoVinculado().intValue() == 0 ? "selected" : "") %>>0 - Nao</option>
                </select>
            </div>
        </div>

        <h3>Recursos de Tecnologia Assistiva (1..N)</h3>
        <div class="checkbox-grid">
            <% if (recursosAssistivos != null) {
                for (OpcaoDominio op : recursosAssistivos) {
                    boolean checked = recursosSelecionados != null && recursosSelecionados.contains(op.getId());
            %>
            <label class="checkbox-item">
                <input type="checkbox" name="opcaoRecursoAssistivo" value="<%= br.gov.inep.censo.util.ViewUtils.e(op.getId()) %>" <%= br.gov.inep.censo.util.ViewUtils.e(checked ? "checked" : "") %>/>
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

