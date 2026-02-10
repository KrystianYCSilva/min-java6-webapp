package br.gov.inep.censo.web;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Ies;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.IesService;
import br.gov.inep.censo.util.RequestFieldMapper;
import br.gov.inep.censo.util.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller web do modulo IES com listagem paginada, CRUD, importacao e exportacao TXT pipe.
 */
public class IesServlet extends AbstractActionServlet {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_PAGINA = 10;

    private final IesService iesService = new IesService();
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
            throw new ServletException("Erro no modulo IES.", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String acao = normalizeAction(request.getParameter("acao"), "salvar");
        try {
            dispatchAction(acao, comandosPost, "salvar", request, response);
        } catch (Exception e) {
            request.setAttribute("erro", "Falha ao processar acao no modulo IES.");
            try {
                listar(request, response);
            } catch (Exception listException) {
                throw new ServletException("Erro ao renderizar listagem de IES apos falha.", listException);
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
        int total = iesService.contar();
        int totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
        if (pagina > totalPaginas) {
            pagina = totalPaginas;
        }

        List<Ies> itens = iesService.listarPaginado(pagina, TAMANHO_PAGINA);
        request.setAttribute("iesItens", itens);
        request.setAttribute("paginaAtual", Integer.valueOf(pagina));
        request.setAttribute("totalPaginas", Integer.valueOf(totalPaginas));
        request.setAttribute("totalRegistros", Integer.valueOf(total));
        request.getRequestDispatcher("/WEB-INF/jsp/ies-list.jsp").forward(request, response);
    }

    private void exibirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Ies ies = id != null ? iesService.buscarPorId(id) : new Ies();
        if (ies == null) {
            ies = new Ies();
        }

        List<LayoutCampo> camposLayout = catalogoService.listarCamposModulo(ModulosLayout.IES_11);
        List<LayoutCampo> camposComplementares = filtrarCamposComplementares(camposLayout);
        java.util.LinkedHashMap<Long, String> valoresComplementares = new java.util.LinkedHashMap<Long, String>();
        if (id != null) {
            valoresComplementares.putAll(iesService.carregarCamposComplementaresPorCampoId(id));
        }

        request.setAttribute("ies", ies);
        request.setAttribute("camposComplementares", camposComplementares);
        request.setAttribute("valoresComplementares", valoresComplementares);
        request.getRequestDispatcher("/WEB-INF/jsp/ies-form.jsp").forward(request, response);
    }

    private void exibirDetalhe(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Ies ies = id != null ? iesService.buscarPorId(id) : null;
        if (ies == null) {
            response.sendRedirect(request.getContextPath() + "/app/ies");
            return;
        }
        request.setAttribute("ies", ies);
        request.setAttribute("camposComplementaresValores", iesService.carregarCamposComplementaresPorCampoId(id));
        request.setAttribute("camposComplementaresRotulos", montarRotulosCampos(ModulosLayout.IES_11));
        request.getRequestDispatcher("/WEB-INF/jsp/ies-view.jsp").forward(request, response);
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Ies ies = Ies.builder()
                .id(id)
                .idIesInep(parseLongOrNull(request.getParameter("idIesInep")))
                .nomeLaboratorio(trimOrEmpty(request.getParameter("nomeLaboratorio")))
                .registroLaboratorioIes(trimOrEmpty(request.getParameter("registroLaboratorioIes")))
                .laboratorioAtivoAno(Integer.valueOf(parseIntOrDefault(request.getParameter("laboratorioAtivoAno"), 1)))
                .descricaoAtividades(trimOrEmpty(request.getParameter("descricaoAtividades")))
                .palavrasChave(trimOrEmpty(request.getParameter("palavrasChave")))
                .laboratorioInformatica(parseIntegerOrNull(request.getParameter("laboratorioInformatica")))
                .tipoLaboratorio(parseIntegerOrNull(request.getParameter("tipoLaboratorio")))
                .codigoUfLaboratorio(parseIntegerOrNull(request.getParameter("codigoUfLaboratorio")))
                .codigoMunicipioLaboratorio(trimOrEmpty(request.getParameter("codigoMunicipioLaboratorio")))
                .build();

        Map<Long, String> camposComplementares = RequestFieldMapper.mapCamposComplementares(request);
        if (id == null) {
            iesService.cadastrar(ies, camposComplementares);
            request.getSession(true).setAttribute("flashIesMessage", "IES incluida com sucesso.");
        } else {
            iesService.atualizar(ies, camposComplementares);
            request.getSession(true).setAttribute("flashIesMessage", "IES alterada com sucesso.");
        }
        response.sendRedirect(request.getContextPath() + "/app/ies");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        if (id != null) {
            iesService.excluir(id);
            request.getSession(true).setAttribute("flashIesMessage", "IES excluida com sucesso.");
        }
        response.sendRedirect(request.getContextPath() + "/app/ies");
    }

    private void importar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String conteudo = request.getParameter("txtConteudo");
        int total = iesService.importarTxtPipe(conteudo);
        request.getSession(true).setAttribute("flashHomeMessage", "Importacao de IES concluida: " + total + " registro(s).");
        request.getSession(true).setAttribute("flashIesMessage", "Importacao concluida: " + total + " registro(s).");
        response.sendRedirect(request.getContextPath() + "/app/ies");
    }

    private void exportarTodos(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String txt = iesService.exportarTodosTxtPipe();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=ies_registro11.txt");
        response.getWriter().write(txt);
    }

    private void exportarItem(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        String txt = iesService.exportarPorIdTxtPipe(id);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=ies_" + (id != null ? id : "item") + "_registro11.txt");
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
            if ((numero >= 1 && numero <= 7) || numero == 17 || numero == 18 || numero == 27 || numero == 28) {
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
