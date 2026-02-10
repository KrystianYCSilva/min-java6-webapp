package br.gov.inep.censo.web;

import br.gov.inep.censo.dao.UsuarioDAO;
import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService(new UsuarioDAO());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String login = trimToNull(request.getParameter("login"));
        String senha = trimToNull(request.getParameter("senha"));

        if (login == null || senha == null) {
            request.setAttribute("erro", "Informe login e senha.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            Usuario usuario = authService.autenticar(login, senha);
            if (usuario == null) {
                request.setAttribute("erro", "Credenciais invalidas.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            HttpSession sessionExistente = request.getSession(false);
            if (sessionExistente != null) {
                sessionExistente.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("usuarioLogado", usuario);
            response.sendRedirect(request.getContextPath() + "/app/menu");
        } catch (SQLException e) {
            throw new ServletException("Erro ao autenticar usuario.", e);
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() == 0 ? null : trimmed;
    }
}
