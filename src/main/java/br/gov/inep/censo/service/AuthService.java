package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.UsuarioDAO;
import br.gov.inep.censo.model.Usuario;

import java.sql.SQLException;

/**
 * Servico de autenticacao para manter a regra fora do servlet.
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService() {
        this(new UsuarioDAO());
    }

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Autentica usuario por login/senha.
     *
     * @param login login informado
     * @param senha senha em texto plano
     * @return usuario quando autenticado; {@code null} caso contrario
     * @throws SQLException erro de acesso a dados
     */
    public Usuario autenticar(String login, String senha) throws SQLException {
        return usuarioDAO.autenticar(login, senha);
    }
}
