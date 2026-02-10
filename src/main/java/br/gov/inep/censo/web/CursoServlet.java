package br.gov.inep.censo.web;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.CursoService;
import br.gov.inep.censo.util.RequestFieldMapper;
import br.gov.inep.censo.util.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller web do modulo Curso com listagem paginada, CRUD, importacao e exportacao TXT pipe.
 */
public class CursoServlet extends AbstractActionServlet {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_PAGINA = 10;

    private final CursoService cursoService = new CursoService();
    private final CatalogoService catalogoService = new CatalogoService();
    private transient Map<String, ActionCommand> comandosGet;
    private transient Map<String, ActionCommand> comandosPost;

    public void init() throws ServletException {
        super.init();
        comandosGet = criarComandosGet();
        comandosPost = criarComandosPost();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = normalizeAction(request.getParameter("acao"), "lista");
        try {
            dispatchAction(acao, comandosGet, "lista", request, response);
        } catch (Exception e) {
            throw new ServletException("Erro no modulo Curso.", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String acao = normalizeAction(request.getParameter("acao"), "salvar");
        try {
            dispatchAction(acao, comandosPost, "salvar", request, response);
        } catch (Exception e) {
            request.setAttribute("erro", "Falha ao processar acao no modulo Curso.");
            try {
                listar(request, response);
            } catch (Exception listException) {
                throw new ServletException("Erro ao renderizar listagem de curso apos falha.", listException);
            }
        }
    }

    private Map<String, ActionCommand> criarComandosGet() {
        Map<String, ActionCommand> commands = new HashMap<String, ActionCommand>();
        commands.put("lista", new ActionCommand() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                listar(request, response);
            }
        });
        commands.put("form", new ActionCommand() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                exibirFormulario(request, response);
            }
        });
        commands.put("mostrar", new ActionCommand() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                exibirDetalhe(request, response);
            }
        });
        commands.put("exportar", new ActionCommand() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                exportarTodos(request, response);
            }
        });
        commands.put("exportar-item", new ActionCommand() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                exportarItem(request, response);
            }
        });
        return Collections.unmodifiableMap(commands);
    }

    private Map<String, ActionCommand> criarComandosPost() {
        Map<String, ActionCommand> commands = new HashMap<String, ActionCommand>();
        commands.put("salvar", new ActionCommand() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                salvar(request, response);
            }
        });
        commands.put("excluir", new ActionCommand() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                excluir(request, response);
            }
        });
        commands.put("importar", new ActionCommand() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                importar(request, response);
            }
        });
        return Collections.unmodifiableMap(commands);
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int pagina = parseIntOrDefault(request.getParameter("pagina"), 1);
        if (pagina <= 0) {
            pagina = 1;
        }
        int total = cursoService.contar();
        int totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
        if (pagina > totalPaginas) {
            pagina = totalPaginas;
        }

        List<Curso> cursos = cursoService.listarPaginado(pagina, TAMANHO_PAGINA);
        request.setAttribute("cursos", cursos);
        request.setAttribute("paginaAtual", Integer.valueOf(pagina));
        request.setAttribute("totalPaginas", Integer.valueOf(totalPaginas));
        request.setAttribute("totalRegistros", Integer.valueOf(total));
        request.getRequestDispatcher("/WEB-INF/jsp/curso-list.jsp").forward(request, response);
    }

    private void exibirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Curso curso = id != null ? cursoService.buscarPorId(id) : new Curso();
        if (curso == null) {
            curso = new Curso();
        }

        List<OpcaoDominio> recursosAssistivos = catalogoService.listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
        List<LayoutCampo> camposLayout = catalogoService.listarCamposModulo(ModulosLayout.CURSO_21);
        List<LayoutCampo> camposComplementares = filtrarCamposComplementares(camposLayout);

        Set<Long> selecionados = new HashSet<Long>();
        java.util.LinkedHashMap<Long, String> valoresComplementares = new java.util.LinkedHashMap<Long, String>();
        if (id != null) {
            selecionados.addAll(cursoService.listarOpcaoRecursoAssistivoIds(id));
            valoresComplementares.putAll(cursoService.carregarCamposComplementaresPorCampoId(id));
        }

        request.setAttribute("curso", curso);
        request.setAttribute("recursosAssistivos", recursosAssistivos);
        request.setAttribute("camposComplementares", camposComplementares);
        request.setAttribute("recursosSelecionados", selecionados);
        request.setAttribute("valoresComplementares", valoresComplementares);
        request.getRequestDispatcher("/WEB-INF/jsp/curso-form.jsp").forward(request, response);
    }

    private void exibirDetalhe(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Curso curso = id != null ? cursoService.buscarPorId(id) : null;
        if (curso == null) {
            response.sendRedirect(request.getContextPath() + "/app/curso");
            return;
        }
        request.setAttribute("curso", curso);
        request.setAttribute("camposComplementaresValores", cursoService.carregarCamposComplementaresPorCampoId(id));
        request.setAttribute("camposComplementaresRotulos", montarRotulosCampos(ModulosLayout.CURSO_21));
        request.getRequestDispatcher("/WEB-INF/jsp/curso-view.jsp").forward(request, response);
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Curso curso = Curso.builder()
                .id(id)
                .codigoCursoEmec(trimOrEmpty(request.getParameter("codigoCursoEmec")))
                .nome(trimOrEmpty(request.getParameter("nome")))
                .nivelAcademico(trimOrEmpty(request.getParameter("nivelAcademico")))
                .formatoOferta(trimOrEmpty(request.getParameter("formatoOferta")))
                .cursoTeveAlunoVinculado(Integer.valueOf(parseIntOrDefault(
                        request.getParameter("cursoTeveAlunoVinculado"), 1)))
                .build();

        long[] recursosAssistivos = RequestFieldMapper.mapSelectedIds(request.getParameterValues("opcaoRecursoAssistivo"));
        Map<Long, String> camposComplementares = RequestFieldMapper.mapCamposComplementares(request);

        if (id == null) {
            cursoService.cadastrar(curso, recursosAssistivos, camposComplementares);
            request.getSession(true).setAttribute("flashCursoMessage", "Curso incluido com sucesso.");
        } else {
            cursoService.atualizar(curso, recursosAssistivos, camposComplementares);
            request.getSession(true).setAttribute("flashCursoMessage", "Curso alterado com sucesso.");
        }

        response.sendRedirect(request.getContextPath() + "/app/curso");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        if (id != null) {
            cursoService.excluir(id);
            request.getSession(true).setAttribute("flashCursoMessage", "Curso excluido com sucesso.");
        }
        response.sendRedirect(request.getContextPath() + "/app/curso");
    }

    private void importar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String conteudo = request.getParameter("txtConteudo");
        int total = cursoService.importarTxtPipe(conteudo);
        request.getSession(true).setAttribute("flashHomeMessage", "Importacao de curso concluida: " + total + " registro(s).");
        request.getSession(true).setAttribute("flashCursoMessage", "Importacao concluida: " + total + " registro(s).");
        response.sendRedirect(request.getContextPath() + "/app/curso");
    }

    private void exportarTodos(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String txt = cursoService.exportarTodosTxtPipe();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=cursos_registro21.txt");
        response.getWriter().write(txt);
    }

    private void exportarItem(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        String txt = cursoService.exportarPorIdTxtPipe(id);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=curso_" + (id != null ? id : "item") + "_registro21.txt");
        response.getWriter().write(txt);
    }

    private List<LayoutCampo> filtrarCamposComplementares(List<LayoutCampo> campos) {
        List<LayoutCampo> filtrados = new ArrayList<LayoutCampo>();
        if (campos == null) {
            return filtrados;
        }
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            int numero = campo.getNumeroCampo().intValue();
            if (numero == 1 || numero == 2 || numero == 3 || (numero >= 54 && numero <= 65)) {
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

    private Long parseLongOrNull(String value) {
        String cleaned = trimOrEmpty(value);
        if (!ValidationUtils.isNumeric(cleaned)) {
            return null;
        }
        return Long.valueOf(Long.parseLong(cleaned));
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        String cleaned = trimOrEmpty(value);
        if (!ValidationUtils.isNumeric(cleaned)) {
            return defaultValue;
        }
        return Integer.parseInt(cleaned);
    }

    private java.util.Map<Long, String> montarRotulosCampos(String modulo) throws Exception {
        java.util.LinkedHashMap<Long, String> rotulos = new java.util.LinkedHashMap<Long, String>();
        List<LayoutCampo> campos = catalogoService.listarCamposModulo(modulo);
        if (campos == null) {
            return rotulos;
        }
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            rotulos.put(campo.getId(), "[" + campo.getNumeroCampo() + "] " + campo.getNomeCampo());
        }
        return rotulos;
    }
}
