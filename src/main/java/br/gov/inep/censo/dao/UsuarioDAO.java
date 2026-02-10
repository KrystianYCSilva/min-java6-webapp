package br.gov.inep.censo.dao;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.util.PasswordUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;

public class UsuarioDAO extends AbstractHibernateDao {

    public Usuario autenticar(final String login, final String senhaPlainText) throws SQLException {
        return executeInTransaction(new SessionWork<Usuario>() {
            public Usuario execute(Session session) {
                Query query = session.createSQLQuery(
                        "SELECT id, login, nome, senha_hash, ativo FROM usuario WHERE login = :login");
                query.setString("login", login);

                Object rowObject = query.uniqueResult();
                if (!(rowObject instanceof Object[])) {
                    return null;
                }
                Object[] row = (Object[]) rowObject;
                if (row.length < 5) {
                    return null;
                }

                Long usuarioId = toLong(row[0]);
                String usuarioLogin = row[1] == null ? null : row[1].toString();
                String nome = row[2] == null ? null : row[2].toString();
                String senhaHash = row[3] == null ? null : row[3].toString();
                boolean ativo = toBoolean(row[4]);

                if (!ativo || senhaHash == null || !PasswordUtil.verifyPassword(senhaPlainText, senhaHash)) {
                    return null;
                }

                if (PasswordUtil.needsRehash(senhaHash)) {
                    Query updateQuery = session.createSQLQuery(
                            "UPDATE usuario SET senha_hash = :senhaHash WHERE id = :id");
                    updateQuery.setString("senhaHash", PasswordUtil.hashPassword(senhaPlainText));
                    updateQuery.setLong("id", usuarioId.longValue());
                    updateQuery.executeUpdate();
                }

                Usuario usuario = new Usuario();
                usuario.setId(usuarioId);
                usuario.setLogin(usuarioLogin);
                usuario.setNome(nome);
                usuario.setAtivo(ativo);
                return usuario;
            }
        });
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        return Long.valueOf(String.valueOf(value));
    }

    private boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        String text = value.toString();
        return "true".equalsIgnoreCase(text) || "1".equals(text) || "t".equalsIgnoreCase(text);
    }
}
