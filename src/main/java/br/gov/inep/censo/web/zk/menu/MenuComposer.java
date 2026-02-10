package br.gov.inep.censo.web.zk.menu;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import javax.servlet.http.HttpSession;

/**
 * Shell principal da aplicacao (header + sidebar + center + footer).
 */
public class MenuComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;

    private Label lblUsuarioHeader;
    private Label lblUsuarioLogin;
    private Label lblSubTitle;
    private Label lblFooter;
    private Include incMain;
    private Include incSub;
    private Window winSub;
    private String currentView = "dashboard";

    public void onCreate$winShell() {
        HttpSession session = currentSession(false);
        if (session == null) {
            redirect("/login.zul");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            redirect("/login.zul");
            return;
        }

        lblUsuarioHeader.setValue(usuario.getNome());
        lblUsuarioLogin.setValue(usuario.getLogin());
        lblFooter.setValue("Censo Superior 2025 | Versao 2.0.0 | Frontend ZK 3.6.2 MVC");

        String view = normalizeView(trimToNull(currentRequest().getParameter("view")));
        currentView = view;
        incMain.setSrc(resolveMainSrc(view));

        String sub = normalizeSub(trimToNull(currentRequest().getParameter("sub")));
        String id = trimToNull(currentRequest().getParameter("id"));
        if (sub != null) {
            openSubWindow(sub, id);
        } else {
            winSub.setVisible(false);
            incSub.setSrc((String) null);
        }
    }

    public void onClick$btnNavHome() {
        goShell("dashboard");
    }

    public void onClick$btnNavAluno() {
        goShell("aluno-list");
    }

    public void onClick$btnNavCurso() {
        goShell("curso-list");
    }

    public void onClick$btnNavCursoAluno() {
        goShell("curso-aluno-list");
    }

    public void onClick$btnNavDocente() {
        goShell("docente-list");
    }

    public void onClick$btnNavIes() {
        goShell("ies-list");
    }

    public void onClick$btnSairHeader() {
        HttpSession session = currentSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect("/login.zul?logout=1");
    }

    public void onClick$btnCloseSub() {
        goShell(currentView);
    }

    public void onClose$winSub() {
        goShell(currentView);
    }

    private void openSubWindow(String sub, String id) {
        String src = resolveSubSrc(sub);
        if (src == null) {
            return;
        }
        if (id != null) {
            src = src + "?id=" + id;
        }
        lblSubTitle.setValue(resolveSubTitle(sub));
        incSub.setSrc(src);
        try {
            winSub.doModal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            winSub.setVisible(true);
        }
    }

    private String normalizeView(String view) {
        if ("aluno-list".equals(view) || "curso-list".equals(view) || "curso-aluno-list".equals(view)
                || "docente-list".equals(view) || "ies-list".equals(view) || "dashboard".equals(view)) {
            return view;
        }
        return "dashboard";
    }

    private String normalizeSub(String sub) {
        if ("aluno-form".equals(sub) || "aluno-view".equals(sub)
                || "curso-form".equals(sub) || "curso-view".equals(sub)
                || "curso-aluno-form".equals(sub)
                || "docente-form".equals(sub) || "docente-view".equals(sub)
                || "ies-form".equals(sub) || "ies-view".equals(sub)) {
            return sub;
        }
        return null;
    }

    private String resolveMainSrc(String view) {
        if ("aluno-list".equals(view)) {
            return "/app/aluno-list.zul";
        }
        if ("curso-list".equals(view)) {
            return "/app/curso-list.zul";
        }
        if ("curso-aluno-list".equals(view)) {
            return "/app/curso-aluno-list.zul";
        }
        if ("docente-list".equals(view)) {
            return "/app/docente-list.zul";
        }
        if ("ies-list".equals(view)) {
            return "/app/ies-list.zul";
        }
        return "/app/home-content.zul";
    }

    private String resolveSubSrc(String sub) {
        if ("aluno-form".equals(sub)) {
            return "/app/aluno-form.zul";
        }
        if ("aluno-view".equals(sub)) {
            return "/app/aluno-view.zul";
        }
        if ("curso-form".equals(sub)) {
            return "/app/curso-form.zul";
        }
        if ("curso-view".equals(sub)) {
            return "/app/curso-view.zul";
        }
        if ("curso-aluno-form".equals(sub)) {
            return "/app/curso-aluno-form.zul";
        }
        if ("docente-form".equals(sub)) {
            return "/app/docente-form.zul";
        }
        if ("docente-view".equals(sub)) {
            return "/app/docente-view.zul";
        }
        if ("ies-form".equals(sub)) {
            return "/app/ies-form.zul";
        }
        if ("ies-view".equals(sub)) {
            return "/app/ies-view.zul";
        }
        return null;
    }

    private String resolveSubTitle(String sub) {
        if ("aluno-form".equals(sub)) {
            return "Cadastro de Aluno";
        }
        if ("aluno-view".equals(sub)) {
            return "Visualizacao de Aluno";
        }
        if ("curso-form".equals(sub)) {
            return "Cadastro de Curso";
        }
        if ("curso-view".equals(sub)) {
            return "Visualizacao de Curso";
        }
        if ("curso-aluno-form".equals(sub)) {
            return "Cadastro de Vinculo Aluno x Curso (Registro 42)";
        }
        if ("docente-form".equals(sub)) {
            return "Cadastro de Docente";
        }
        if ("docente-view".equals(sub)) {
            return "Visualizacao de Docente";
        }
        if ("ies-form".equals(sub)) {
            return "Cadastro de IES";
        }
        if ("ies-view".equals(sub)) {
            return "Visualizacao de IES";
        }
        return "Detalhes";
    }
}
