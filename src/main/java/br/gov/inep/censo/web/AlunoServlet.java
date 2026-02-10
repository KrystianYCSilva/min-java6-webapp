package br.gov.inep.censo.web;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.service.AlunoService;
import br.gov.inep.censo.service.CatalogoService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller web do modulo Aluno com listagem paginada, CRUD, importacao e exportacao TXT pipe.
 */
public class AlunoServlet extends AbstractActionServlet {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_PAGINA = 10;

    private final AlunoService alunoService = new AlunoService();
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
            throw new ServletException("Erro no modulo Aluno.", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String acao = normalizeAction(request.getParameter("acao"), "salvar");
        try {
            dispatchAction(acao, comandosPost, "salvar", request, response);
        } catch (Exception e) {
            request.setAttribute("erro", "Falha ao processar acao no modulo Aluno.");
            try {
                listar(request, response);
            } catch (Exception listException) {
                throw new ServletException("Erro ao renderizar listagem de aluno apos falha.", listException);
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
        int total = alunoService.contar();
        int totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
        if (pagina > totalPaginas) {
            pagina = totalPaginas;
        }

        List<Aluno> alunos = alunoService.listarPaginado(pagina, TAMANHO_PAGINA);

        request.setAttribute("alunos", alunos);
        request.setAttribute("paginaAtual", Integer.valueOf(pagina));
        request.setAttribute("totalPaginas", Integer.valueOf(totalPaginas));
        request.setAttribute("totalRegistros", Integer.valueOf(total));
        request.getRequestDispatcher("/WEB-INF/jsp/aluno-list.jsp").forward(request, response);
    }

    private void exibirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Aluno aluno = id != null ? alunoService.buscarPorId(id) : new Aluno();
        if (aluno == null) {
            aluno = new Aluno();
        }

        List<OpcaoDominio> opcoesDeficiencia = catalogoService.listarOpcoesPorCategoria(
                CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
        List<LayoutCampo> camposLayout = catalogoService.listarCamposModulo(ModulosLayout.ALUNO_41);
        List<LayoutCampo> camposComplementares = filtrarCamposComplementares(camposLayout);

        Set<Long> selecionados = new HashSet<Long>();
        java.util.LinkedHashMap<Long, String> valoresComplementares = new java.util.LinkedHashMap<Long, String>();
        if (id != null) {
            selecionados.addAll(alunoService.listarOpcaoDeficienciaIds(id));
            valoresComplementares.putAll(alunoService.carregarCamposComplementaresPorCampoId(id));
        }

        request.setAttribute("aluno", aluno);
        request.setAttribute("opcoesDeficiencia", opcoesDeficiencia);
        request.setAttribute("camposComplementares", camposComplementares);
        request.setAttribute("opcoesDeficienciaSelecionadas", selecionados);
        request.setAttribute("valoresComplementares", valoresComplementares);
        request.getRequestDispatcher("/WEB-INF/jsp/aluno-form.jsp").forward(request, response);
    }

    private void exibirDetalhe(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        Aluno aluno = id != null ? alunoService.buscarPorId(id) : null;
        if (aluno == null) {
            response.sendRedirect(request.getContextPath() + "/app/aluno");
            return;
        }
        request.setAttribute("aluno", aluno);
        request.setAttribute("camposComplementaresValores", alunoService.carregarCamposComplementaresPorCampoId(id));
        request.setAttribute("camposComplementaresRotulos", montarRotulosCampos(ModulosLayout.ALUNO_41));
        request.getRequestDispatcher("/WEB-INF/jsp/aluno-view.jsp").forward(request, response);
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        String dataNascimento = trimOrEmpty(request.getParameter("dataNascimento"));
        Date nascimento = null;
        if (dataNascimento.length() > 0) {
            nascimento = Date.valueOf(dataNascimento);
        }

        Aluno aluno = Aluno.builder()
                .id(id)
                .idAlunoInep(parseLongOrNull(request.getParameter("idAlunoInep")))
                .nome(trimOrEmpty(request.getParameter("nome")))
                .cpf(trimOrEmpty(request.getParameter("cpf")))
                .dataNascimento(nascimento)
                .corRaca(parseIntegerOrNull(request.getParameter("corRaca")))
                .nacionalidade(Integer.valueOf(parseIntOrDefault(request.getParameter("nacionalidade"), 1)))
                .ufNascimento(trimOrEmpty(request.getParameter("ufNascimento")))
                .municipioNascimento(trimOrEmpty(request.getParameter("municipioNascimento")))
                .paisOrigem(trimOrEmpty(request.getParameter("paisOrigem")))
                .build();

        long[] opcaoDeficienciaIds = RequestFieldMapper.mapSelectedIds(request.getParameterValues("opcaoDeficiencia"));
        Map<Long, String> camposComplementares = RequestFieldMapper.mapCamposComplementares(request);

        if (id == null) {
            alunoService.cadastrar(aluno, opcaoDeficienciaIds, camposComplementares);
            request.getSession(true).setAttribute("flashHomeMessage", "Cadastro de aluno realizado com sucesso.");
            request.getSession(true).setAttribute("flashAlunoMessage", "Aluno incluido com sucesso.");
        } else {
            alunoService.atualizar(aluno, opcaoDeficienciaIds, camposComplementares);
            request.getSession(true).setAttribute("flashAlunoMessage", "Aluno alterado com sucesso.");
        }
        response.sendRedirect(request.getContextPath() + "/app/aluno");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        if (id != null) {
            alunoService.excluir(id);
            request.getSession(true).setAttribute("flashAlunoMessage", "Aluno excluido com sucesso.");
        }
        response.sendRedirect(request.getContextPath() + "/app/aluno");
    }

    private void importar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String conteudo = request.getParameter("txtConteudo");
        int total = alunoService.importarTxtPipe(conteudo);
        request.getSession(true).setAttribute("flashHomeMessage", "Importacao de aluno concluida: " + total + " registro(s).");
        request.getSession(true).setAttribute("flashAlunoMessage", "Importacao concluida: " + total + " registro(s).");
        response.sendRedirect(request.getContextPath() + "/app/aluno");
    }

    private void exportarTodos(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String txt = alunoService.exportarTodosTxtPipe();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=alunos_registro41.txt");
        response.getWriter().write(txt);
    }

    private void exportarItem(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long id = parseLongOrNull(request.getParameter("id"));
        String txt = alunoService.exportarPorIdTxtPipe(id);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=aluno_" + (id != null ? id : "item") + "_registro41.txt");
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
            if (numero == 1 || numero == 2 || numero == 3 || numero == 4 || numero == 6 || numero == 7 ||
                    numero == 8 || numero == 9 || numero == 10 || numero == 11 || (numero >= 13 && numero <= 22)) {
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
