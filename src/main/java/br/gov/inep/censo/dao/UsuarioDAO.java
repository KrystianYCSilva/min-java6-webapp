package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    private static final String SQL_BUSCA_LOGIN =
            "SELECT id, login, nome, senha_hash, ativo FROM usuario WHERE login = ?";
    private static final String SQL_ATUALIZA_HASH =
            "UPDATE usuario SET senha_hash = ? WHERE id = ?";

    public Usuario autenticar(String login, String senhaPlainText) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_BUSCA_LOGIN);
            statement.setString(1, login);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                boolean ativo = resultSet.getBoolean("ativo");
                String senhaHash = resultSet.getString("senha_hash");

                if (ativo && PasswordUtil.verifyPassword(senhaPlainText, senhaHash)) {
                    if (PasswordUtil.needsRehash(senhaHash)) {
                        atualizarHashSenha(connection,
                                Long.valueOf(resultSet.getLong("id")),
                                PasswordUtil.hashPassword(senhaPlainText));
                    }
                    Usuario usuario = new Usuario();
                    usuario.setId(Long.valueOf(resultSet.getLong("id")));
                    usuario.setLogin(resultSet.getString("login"));
                    usuario.setNome(resultSet.getString("nome"));
                    usuario.setAtivo(ativo);
                    return usuario;
                }
            }
            return null;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                    // noop
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    // noop
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                    // noop
                }
            }
        }
    }

    private void atualizarHashSenha(Connection connection, Long usuarioId, String novoHash) throws SQLException {
        PreparedStatement updateStatement = null;
        try {
            updateStatement = connection.prepareStatement(SQL_ATUALIZA_HASH);
            updateStatement.setString(1, novoHash);
            updateStatement.setLong(2, usuarioId.longValue());
            updateStatement.executeUpdate();
        } finally {
            if (updateStatement != null) {
                try {
                    updateStatement.close();
                } catch (SQLException ignored) {
                    // noop
                }
            }
        }
    }
}
