package br.gov.inep.censo.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Base para servlets com despacho de acoes por comando.
 */
public abstract class AbstractActionServlet extends HttpServlet {

    protected static interface ActionCommand {
        void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
    }

    protected void dispatchAction(String action,
                                  Map<String, ActionCommand> commands,
                                  String defaultAction,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        ActionCommand command = commands.get(action);
        if (command == null) {
            command = commands.get(defaultAction);
        }
        if (command == null) {
            throw new IllegalStateException("Acao padrao nao registrada: " + defaultAction);
        }
        command.execute(request, response);
    }

    protected String normalizeAction(String action, String defaultAction) {
        if (action == null) {
            return defaultAction;
        }
        String normalized = action.trim().toLowerCase();
        if (normalized.length() == 0) {
            return defaultAction;
        }
        return normalized;
    }
}
