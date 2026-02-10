package br.gov.inep.censo.web.zk.home;

import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zul.Label;

/**
 * Controller MVC da pagina inicial.
 */
public class HomeComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;

    private Label lblFlash;

    public void onCreate$winHome() {
        String flash = consumeFlash("flashHomeMessage");
        if (flash == null) {
            lblFlash.setVisible(false);
            lblFlash.setValue("");
            return;
        }
        lblFlash.setVisible(true);
        lblFlash.setValue(flash);
    }

    public void onClick$btnEntrar() {
        redirect("/login.zul");
    }

    public void onClick$btnMenu() {
        goShell("dashboard");
    }
}
