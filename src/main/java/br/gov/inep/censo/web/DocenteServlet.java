package br.gov.inep.censo.web;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Docente;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.DocenteService;
import br.gov.inep.censo.util.RequestFieldMapper;
import br.gov.inep.censo.util.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller web do modulo Docente com listagem paginada, CRUD, importacao e exportacao TXT pipe.
 */
public class DocenteServlet extends AbstractActionServlet {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_PAGINA = 10;

    private final DocenteService docenteService = new DocenteService();
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
            throw new ServletException("Erro no modulo Docente.", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String acao = normalizeAction(request.getParameter("acao"), "salvar");
        try {
            dispatchAction(acao, comandosPost, "salvar", request, response);
        } catch (Exception e) {
            request.setAttribute("erro", "Falha ao processar acao no modulo Docente.");
            try {
                listar(request, response);
            } catch (Exception listException) {
                throw new ServletException("Erro ao renderizar listagem de docente apos falha.", listException);
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
        int total = docenteService.contar();
        int totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
        if (pagina > totalPaginas) {
            pagina = totalPaginas;
        }
        List<Docente> docentes = docenteService.listarPaginado(pagina, TAMANHO_PAGINA);
        request.setAttribute("docentes", docentes);
        request.setAttribute("paginaAtual", Integer.valueOf(pagina));
        request.setAttribute("totalPaginas", Integer.valueOf(totalPaginas));
        request.setAttribute("totalRegistros", Integer.valueOf(total));
        request.getRequestDispatcher("/WEB-INF/jsp/docente-list.jsp").forward(request, response);
    }

    private void exibirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Docente docente = id != null ? docenteService.buscarPorId(id) : new Docente();
        if (docente == null) {
            docente = new Docente();
        }

        List<LayoutCampo> camposLayout = catalogoService.listarCamposModulo(ModulosLayout.DOCENTE_31);
        List<LayoutCampo> camposComplementares = filtrarCamposComplementares(camposLayout);
        java.util.LinkedHashMap<Long, String> valoresComplementares = new java.util.LinkedHashMap<Long, String>();
        if (id != null) {
            valoresComplementares.putAll(docenteService.carregarCamposComplementaresPorCampoId(id));
        }

        request.setAttribute("docente", docente);
        request.setAttribute("camposComplementares", camposComplementares);
        request.setAttribute("valoresComplementares", valoresComplementares);
        request.getRequestDispatcher("/WEB-INF/jsp/docente-form.jsp").forward(request, response);
    }

    private void exibirDetalhe(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Docente docente = id != null ? docenteService.buscarPorId(id) : null;
        if (docente == null) {
            response.sendRedirect(request.getContextPath() + "/app/docente");
            return;
        }
        request.setAttribute("docente", docente);
        request.setAttribute("camposComplementaresValores", docenteService.carregarCamposComplementaresPorCampoId(id));
        request.setAttribute("camposComplementaresRotulos", montarRotulosCampos(ModulosLayout.DOCENTE_31));
        request.getRequestDispatcher("/WEB-INF/jsp/docente-view.jsp").forward(request, response);
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        String dataNascimento = trimOrEmpty(request.getParameter("dataNascimento"));
        Date nascimento = null;
        if (dataNascimento.length() > 0) {
            nascimento = Date.valueOf(dataNascimento);
        }

        Docente docente = Docente.builder()
                .id(id)
                .idDocenteIes(trimOrEmpty(request.getParameter("idDocenteIes")))
                .nome(trimOrEmpty(request.getParameter("nome")))
                .cpf(trimOrEmpty(request.getParameter("cpf")))
                .documentoEstrangeiro(trimOrEmpty(request.getParameter("documentoEstrangeiro")))
                .dataNascimento(nascimento)
                .corRaca(parseIntegerOrNull(request.getParameter("corRaca")))
                .nacionalidade(Integer.valueOf(parseIntOrDefault(request.getParameter("nacionalidade"), 1)))
                .paisOrigem(trimOrEmpty(request.getParameter("paisOrigem")))
                .ufNascimento(parseIntegerOrNull(request.getParameter("ufNascimento")))
                .municipioNascimento(trimOrEmpty(request.getParameter("municipioNascimento")))
                .docenteDeficiencia(parseIntegerOrNull(request.getParameter("docenteDeficiencia")))
                .build();

        Map<Long, String> camposComplementares = RequestFieldMapper.mapCamposComplementares(request);
        if (id == null) {
            docenteService.cadastrar(docente, camposComplementares);
            request.getSession(true).setAttribute("flashDocenteMessage", "Docente incluido com sucesso.");
        } else {
            docenteService.atualizar(docente, camposComplementares);
            request.getSession(true).setAttribute("flashDocenteMessage", "Docente alterado com sucesso.");
        }
        response.sendRedirect(request.getContextPath() + "/app/docente");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        if (id != null) {
            docenteService.excluir(id);
            request.getSession(true).setAttribute("flashDocenteMessage", "Docente excluido com sucesso.");
        }
        response.sendRedirect(request.getContextPath() + "/app/docente");
    }

    private void importar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String conteudo = request.getParameter("txtConteudo");
        int total = docenteService.importarTxtPipe(conteudo);
        request.getSession(true).setAttribute("flashHomeMessage", "Importacao de docente concluida: " + total + " registro(s).");
        request.getSession(true).setAttribute("flashDocenteMessage", "Importacao concluida: " + total + " registro(s).");
        response.sendRedirect(request.getContextPath() + "/app/docente");
    }

    private void exportarTodos(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String txt = docenteService.exportarTodosTxtPipe();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=docentes_registro31.txt");
        response.getWriter().write(txt);
    }

    private void exportarItem(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        String txt = docenteService.exportarPorIdTxtPipe(id);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=docente_" + (id != null ? id : "item") + "_registro31.txt");
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
            if (numero >= 1 && numero <= 12) {
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

    private Integer parseIntegerOrNull(String value) {
        String cleaned = trimOrEmpty(value);
        if (!ValidationUtils.isNumeric(cleaned)) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(cleaned));
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
