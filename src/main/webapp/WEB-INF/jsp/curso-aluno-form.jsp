<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.gov.inep.censo.model.Aluno" %>
<%@ page import="br.gov.inep.censo.model.Curso" %>
<%@ page import="br.gov.inep.censo.model.OpcaoDominio" %>
<%@ page import="br.gov.inep.censo.model.LayoutCampo" %>
<%@ page import="br.gov.inep.censo.model.enums.FormaIngressoEnum" %>
<%
    String erro = (String) request.getAttribute("erro");
    List<Aluno> alunos = (List<Aluno>) request.getAttribute("alunos");
    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
    List<OpcaoDominio> opcoesFinanciamento = (List<OpcaoDominio>) request.getAttribute("opcoesFinanciamento");
    List<OpcaoDominio> opcoesApoioSocial = (List<OpcaoDominio>) request.getAttribute("opcoesApoioSocial");
    List<OpcaoDominio> opcoesAtividade = (List<OpcaoDominio>) request.getAttribute("opcoesAtividade");
    List<OpcaoDominio> opcoesReserva = (List<OpcaoDominio>) request.getAttribute("opcoesReserva");
    List<LayoutCampo> camposComplementares = (List<LayoutCampo>) request.getAttribute("camposComplementares");
    String csrfToken = br.gov.inep.censo.util.ViewUtils.csrf(session);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Registro 42 - Formulario</title>
    <link rel="stylesheet" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/css/pdf-like.css"/>
</head>
<body>
<div class="sheet">
    <header class="sheet-header">
        <span class="official-tag">Modulo Vinculo</span>
        <h2>Formulario de Vinculo Aluno x Curso (Registro 42)</h2>
        <p>
            Normalizacao 1..N para financiamentos, apoio social, atividades e reservas de vagas +
            campos complementares do leiaute.
        </p>
    </header>

    <div class="top-actions">
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso-aluno">Voltar para Listagem</a>
        <a class="btn-outline" href="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/menu">Menu</a>
    </div>

    <% if (erro != null) { %>
    <div class="alert"><%= br.gov.inep.censo.util.ViewUtils.e(erro) %></div>
    <% } %>

    <form method="post" action="<%= br.gov.inep.censo.util.ViewUtils.e(request.getContextPath()) %>/app/curso-aluno">
        <input type="hidden" name="acao" value="salvar"/>
        <input type="hidden" name="_csrf" value="<%= br.gov.inep.censo.util.ViewUtils.e(csrfToken) %>"/>

        <div class="form-grid">
            <div class="field">
                <label for="alunoId">Aluno</label>
                <select id="alunoId" name="alunoId" required>
                    <option value="">Selecione...</option>
                    <% if (alunos != null) {
                        for (Aluno aluno : alunos) { %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(aluno.getId()) %>"><%= br.gov.inep.censo.util.ViewUtils.e(aluno.getNome()) %> - CPF <%= br.gov.inep.censo.util.ViewUtils.e(aluno.getCpf()) %></option>
                    <% }
                    } %>
                </select>
            </div>

            <div class="field">
                <label for="cursoId">Curso</label>
                <select id="cursoId" name="cursoId" required>
                    <option value="">Selecione...</option>
                    <% if (cursos != null) {
                        for (Curso curso : cursos) { %>
                    <option value="<%= br.gov.inep.censo.util.ViewUtils.e(curso.getId()) %>"><%= br.gov.inep.censo.util.ViewUtils.e(curso.getCodigoCursoEmec()) %> - <%= br.gov.inep.censo.util.ViewUtils.e(curso.getNome()) %></option>
                    <% }
                    } %>
                </select>
            </div>

            <div class="field">
                <label for="idAlunoIes">ID na IES - Identificacao unica</label>
                <input id="idAlunoIes" name="idAlunoIes" type="text" maxlength="30" required/>
            </div>

            <div class="field">
                <label for="periodoReferencia">Periodo de referencia (AAAA)</label>
                <input id="periodoReferencia" name="periodoReferencia" type="text" maxlength="4" value="2025" required/>
            </div>

            <div class="field">
                <label for="codigoPoloEad">Codigo do polo do curso a distancia</label>
                <input id="codigoPoloEad" name="codigoPoloEad" type="text" maxlength="12"/>
            </div>

            <div class="field">
                <label for="turnoAluno">Turno do aluno</label>
                <select id="turnoAluno" name="turnoAluno">
                    <option value="">Nao informado</option>
                    <option value="1">1 - Matutino</option>
                    <option value="2">2 - Vespertino</option>
                    <option value="3">3 - Noturno</option>
                    <option value="4">4 - Integral</option>
                </select>
            </div>

            <div class="field">
                <label for="situacaoVinculo">Situacao de vinculo do aluno ao curso</label>
                <select id="situacaoVinculo" name="situacaoVinculo">
                    <option value="">Nao informado</option>
                    <option value="1">1 - Cursando</option>
                    <option value="2">2 - Matricula trancada</option>
                    <option value="3">3 - Desvinculado</option>
                    <option value="4">4 - Formado</option>
                    <option value="5">5 - Transferido</option>
                </select>
            </div>

            <div class="field">
                <label for="cursoOrigem">Curso origem</label>
                <input id="cursoOrigem" name="cursoOrigem" type="text" maxlength="12"/>
            </div>

            <div class="field">
                <label for="semestreConclusao">Semestre de conclusao (01AAAA / 02AAAA)</label>
                <input id="semestreConclusao" name="semestreConclusao" type="text" maxlength="6"/>
            </div>

            <div class="field">
                <label for="alunoParfor">Aluno PARFOR</label>
                <select id="alunoParfor" name="alunoParfor">
                    <option value="0">0 - Nao</option>
                    <option value="1">1 - Sim</option>
                </select>
            </div>

            <div class="field">
                <label for="segundaLicenciaturaFormacao">Segunda Licenciatura / Formacao pedagogica</label>
                <select id="segundaLicenciaturaFormacao" name="segundaLicenciaturaFormacao">
                    <option value="0">0 - Nao</option>
                    <option value="1">1 - Sim</option>
                </select>
            </div>

            <div class="field">
                <label for="tipoSegundaLicenciaturaFormacao">Tipo - Segunda Licenciatura / Formacao pedagogica</label>
                <select id="tipoSegundaLicenciaturaFormacao" name="tipoSegundaLicenciaturaFormacao">
                    <option value="">Nao informado</option>
                    <option value="1">1 - Segunda Licenciatura</option>
                    <option value="2">2 - Formacao Pedagogica</option>
                </select>
            </div>

            <div class="field">
                <label for="semestreIngresso">Semestre de ingresso no curso (01AAAA / 02AAAA)</label>
                <input id="semestreIngresso" name="semestreIngresso" type="text" maxlength="6"/>
            </div>
        </div>

        <h3>Forma de ingresso/selecao (campos 14 a 23)</h3>
        <div class="form-grid">
            <% FormaIngressoEnum[] formasIngresso = FormaIngressoEnum.values();
                for (int i = 0; i < formasIngresso.length; i++) {
                    FormaIngressoEnum forma = formasIngresso[i];
            %>
            <div class="field">
                <label for="<%= br.gov.inep.censo.util.ViewUtils.e(forma.getParametroRequest()) %>"><%= br.gov.inep.censo.util.ViewUtils.e(forma.getDescricao()) %></label>
                <select id="<%= br.gov.inep.censo.util.ViewUtils.e(forma.getParametroRequest()) %>" name="<%= br.gov.inep.censo.util.ViewUtils.e(forma.getParametroRequest()) %>">
                    <option value="0">0 - Nao</option>
                    <option value="1">1 - Sim</option>
                </select>
            </div>
            <% } %>
        </div>

        <h3>Financiamento (1..N)</h3>
        <div class="checkbox-grid">
            <% if (opcoesFinanciamento != null) {
                for (OpcaoDominio item : opcoesFinanciamento) { %>
            <label class="checkbox-item">
                <input type="checkbox" name="opcaoFinanciamento" value="<%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %>"/>
                <span><%= br.gov.inep.censo.util.ViewUtils.e(item.getNome()) %></span>
            </label>
            <% }
            } %>
        </div>

        <h3>Apoio Social (1..N)</h3>
        <div class="checkbox-grid">
            <% if (opcoesApoioSocial != null) {
                for (OpcaoDominio item : opcoesApoioSocial) { %>
            <label class="checkbox-item">
                <input type="checkbox" name="opcaoApoioSocial" value="<%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %>"/>
                <span><%= br.gov.inep.censo.util.ViewUtils.e(item.getNome()) %></span>
            </label>
            <% }
            } %>
        </div>

        <h3>Atividades Extracurriculares (1..N)</h3>
        <div class="checkbox-grid">
            <% if (opcoesAtividade != null) {
                for (OpcaoDominio item : opcoesAtividade) { %>
            <label class="checkbox-item">
                <input type="checkbox" name="opcaoAtividade" value="<%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %>"/>
                <span><%= br.gov.inep.censo.util.ViewUtils.e(item.getNome()) %></span>
            </label>
            <% }
            } %>
        </div>

        <h3>Reserva de Vagas / Programas Diferenciados (1..N)</h3>
        <div class="checkbox-grid">
            <% if (opcoesReserva != null) {
                for (OpcaoDominio item : opcoesReserva) { %>
            <label class="checkbox-item">
                <input type="checkbox" name="opcaoReserva" value="<%= br.gov.inep.censo.util.ViewUtils.e(item.getId()) %>"/>
                <span><%= br.gov.inep.censo.util.ViewUtils.e(item.getNome()) %></span>
            </label>
            <% }
            } %>
        </div>

        <h3>Campos complementares do leiaute (Registro 42)</h3>
        <div class="form-grid">
            <% if (camposComplementares != null && !camposComplementares.isEmpty()) {
                for (LayoutCampo campo : camposComplementares) { %>
            <div class="field">
                <label for="extra_<%= br.gov.inep.censo.util.ViewUtils.e(campo.getId()) %>">[<%= br.gov.inep.censo.util.ViewUtils.e(campo.getNumeroCampo()) %>] <%= br.gov.inep.censo.util.ViewUtils.e(campo.getNomeCampo()) %></label>
                <input id="extra_<%= br.gov.inep.censo.util.ViewUtils.e(campo.getId()) %>" name="extra_<%= br.gov.inep.censo.util.ViewUtils.e(campo.getId()) %>" type="text" maxlength="4000"/>
            </div>
            <% }
            } %>
        </div>

        <button class="btn" type="submit">Salvar Registro 42</button>
    </form>
</div>
</body>
</html>

