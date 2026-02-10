package br.gov.inep.censo.web;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.service.AlunoService;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.CursoService;
import br.gov.inep.censo.service.CursoAlunoService;
import br.gov.inep.censo.util.RequestFieldMapper;
import br.gov.inep.censo.util.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servlet do Registro 42 com suporte a normalizacao de campos 1..N.
 */
public class CursoAlunoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CursoAlunoService cursoAlunoService = new CursoAlunoService();
    private final AlunoService alunoService = new AlunoService();
    private final CursoService cursoService = new CursoService();
    private final CatalogoService catalogoService = new CatalogoService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = normalizeAction(request.getParameter("acao"));
        try {
            if ("form".equals(acao)) {
                exibirFormulario(request, response);
                return;
            }
            listar(request, response);
        } catch (Exception e) {
            throw new ServletException("Erro no modulo CursoAluno.", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String acao = normalizeAction(request.getParameter("acao"));
        try {
            if ("salvar".equals(acao) || "lista".equals(acao)) {
                salvar(request, response);
                return;
            }
            salvar(request, response);
        } catch (Exception e) {
            request.setAttribute("erro", "Nao foi possivel salvar Registro 42.");
            try {
                exibirFormulario(request, response);
            } catch (Exception formException) {
                throw new ServletException("Erro ao renderizar formulario de Registro 42 apos falha.", formException);
            }
        }
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        CursoAluno cursoAluno = CursoAluno.builder()
                .alunoId(parseLongObrigatorio(request.getParameter("alunoId")))
                .cursoId(parseLongObrigatorio(request.getParameter("cursoId")))
                .idAlunoIes(trimOrEmpty(request.getParameter("idAlunoIes")))
                .periodoReferencia(trimOrEmpty(request.getParameter("periodoReferencia")))
                .codigoPoloEad(trimToNull(request.getParameter("codigoPoloEad")))
                .turnoAluno(parseIntOpcional(request.getParameter("turnoAluno")))
                .situacaoVinculo(parseIntOpcional(request.getParameter("situacaoVinculo")))
                .cursoOrigem(trimToNull(request.getParameter("cursoOrigem")))
                .semestreConclusao(trimToNull(request.getParameter("semestreConclusao")))
                .alunoParfor(parseIntObrigatorioComDefault(request.getParameter("alunoParfor"), 0))
                .segundaLicenciaturaFormacao(parseIntObrigatorioComDefault(
                        request.getParameter("segundaLicenciaturaFormacao"), 0))
                .tipoSegundaLicenciaturaFormacao(parseIntOpcional(
                        request.getParameter("tipoSegundaLicenciaturaFormacao")))
                .semestreIngresso(trimToNull(request.getParameter("semestreIngresso")))
                .formaIngressoVestibular(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoVestibular"), 0))
                .formaIngressoEnem(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoEnem"), 0))
                .formaIngressoAvaliacaoSeriada(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoAvaliacaoSeriada"), 0))
                .formaIngressoSelecaoSimplificada(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoSelecaoSimplificada"), 0))
                .formaIngressoEgressoBiLi(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoEgressoBiLi"), 0))
                .formaIngressoPecG(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoPecG"), 0))
                .formaIngressoTransferenciaExofficio(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoTransferenciaExofficio"), 0))
                .formaIngressoDecisaoJudicial(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoDecisaoJudicial"), 0))
                .formaIngressoVagasRemanescentes(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoVagasRemanescentes"), 0))
                .formaIngressoProgramasEspeciais(parseIntObrigatorioComDefault(
                        request.getParameter("formaIngressoProgramasEspeciais"), 0))
                .build();

        long[] financiamentoIds = RequestFieldMapper.mapSelectedIds(request.getParameterValues("opcaoFinanciamento"));
        long[] apoioSocialIds = RequestFieldMapper.mapSelectedIds(request.getParameterValues("opcaoApoioSocial"));
        long[] atividadeIds = RequestFieldMapper.mapSelectedIds(request.getParameterValues("opcaoAtividade"));
        long[] reservaIds = RequestFieldMapper.mapSelectedIds(request.getParameterValues("opcaoReserva"));
        long[] opcaoIds = mergeArrays(financiamentoIds, apoioSocialIds, atividadeIds, reservaIds);

        Map<Long, String> camposComplementares = RequestFieldMapper.mapCamposComplementares(request);

        cursoAlunoService.cadastrar(cursoAluno, opcaoIds, camposComplementares);
        request.getSession(true).setAttribute("flashCursoAlunoMessage", "Registro 42 salvo com sucesso.");
        response.sendRedirect(request.getContextPath() + "/app/curso-aluno");
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<CursoAluno> vinculos = cursoAlunoService.listar();
        request.setAttribute("vinculos", vinculos);
        request.getRequestDispatcher("/WEB-INF/jsp/curso-aluno-list.jsp").forward(request, response);
    }

    private void exibirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Aluno> alunos = alunoService.listar();
        List<Curso> cursos = cursoService.listar();

        request.setAttribute("alunos", alunos);
        request.setAttribute("cursos", cursos);
        request.setAttribute("opcoesFinanciamento", catalogoService.listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_ALUNO_TIPO_FINANCIAMENTO));
        request.setAttribute("opcoesApoioSocial", catalogoService.listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_ALUNO_APOIO_SOCIAL));
        request.setAttribute("opcoesAtividade", catalogoService.listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR));
        request.setAttribute("opcoesReserva", catalogoService.listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_ALUNO_RESERVA_VAGA));
        request.setAttribute("camposComplementares", filtrarCamposComplementares(
                catalogoService.listarCamposModulo(ModulosLayout.ALUNO_42)));
        request.getRequestDispatcher("/WEB-INF/jsp/curso-aluno-form.jsp").forward(request, response);
    }

    private List<LayoutCampo> filtrarCamposComplementares(List<LayoutCampo> campos) {
        List<LayoutCampo> filtrados = new ArrayList<LayoutCampo>();
        if (campos == null) {
            return filtrados;
        }
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            int numero = campo.getNumeroCampo().intValue();
            if ((numero >= 2 && numero <= 23) || (numero >= 29 && numero <= 39) ||
                    (numero >= 41 && numero <= 46) || (numero >= 48 && numero <= 55) ||
                    (numero >= 60 && numero <= 72)) {
                continue;
            }
            filtrados.add(campo);
        }
        return filtrados;
    }

    private String trimOrEmpty(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        String cleaned = trimOrEmpty(value);
        return cleaned.length() == 0 ? null : cleaned;
    }

    private String normalizeAction(String acao) {
        if (acao == null || acao.trim().length() == 0) {
            return "lista";
        }
        return acao.trim().toLowerCase();
    }

    private Long parseLongObrigatorio(String value) {
        String cleaned = trimOrEmpty(value);
        if (cleaned.length() == 0 || !ValidationUtils.isNumeric(cleaned)) {
            throw new IllegalArgumentException("Selecione aluno e curso.");
        }
        return Long.valueOf(Long.parseLong(cleaned));
    }

    private Integer parseIntOpcional(String value) {
        String cleaned = trimOrEmpty(value);
        if (cleaned.length() == 0) {
            return null;
        }
        if (!ValidationUtils.isNumeric(cleaned)) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(cleaned));
    }

    private Integer parseIntObrigatorioComDefault(String value, int defaultValue) {
        String cleaned = trimOrEmpty(value);
        if (cleaned.length() == 0 || !ValidationUtils.isNumeric(cleaned)) {
            return Integer.valueOf(defaultValue);
        }
        return Integer.valueOf(Integer.parseInt(cleaned));
    }

    private long[] mergeArrays(long[] a, long[] b, long[] c, long[] d) {
        int size = a.length + b.length + c.length + d.length;
        long[] merged = new long[size];
        int index = 0;
        for (int i = 0; i < a.length; i++) {
            merged[index++] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            merged[index++] = b[i];
        }
        for (int i = 0; i < c.length; i++) {
            merged[index++] = c[i];
        }
        for (int i = 0; i < d.length; i++) {
            merged[index++] = d[i];
        }
        return merged;
    }
}
