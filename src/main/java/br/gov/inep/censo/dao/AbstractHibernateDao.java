package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;

/**
 * Infraestrutura base para DAOs Hibernate nativos (Session + Transaction),
 * mantendo contrato checked de SQLException usado pelas camadas legadas.
 */
public abstract class AbstractHibernateDao {

    protected interface SessionWork<T> {
        T execute(Session session) throws SQLException;
    }

    protected <T> T executeInSession(SessionWork<T> work) throws SQLException {
        Session session = null;
        try {
            session = HibernateConnectionProvider.openSession();
            return work.execute(session);
        } catch (SQLException e) {
            throw e;
        } catch (RuntimeException e) {
            throw toSqlException("Falha de persistencia em sessao Hibernate.", e);
        } finally {
            closeQuietly(session);
        }
    }

    protected <T> T executeInTransaction(SessionWork<T> work) throws SQLException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateConnectionProvider.openSession();
            transaction = session.beginTransaction();
            T result = work.execute(session);
            transaction.commit();
            return result;
        } catch (SQLException e) {
            rollbackQuietly(transaction);
            throw e;
        } catch (RuntimeException e) {
            rollbackQuietly(transaction);
            throw toSqlException("Falha de persistencia transacional com Hibernate.", e);
        } finally {
            closeQuietly(session);
        }
    }

    protected int normalizePage(int pagina) {
        return pagina <= 0 ? 1 : pagina;
    }

    protected int normalizePageSize(int tamanhoPagina) {
        return tamanhoPagina <= 0 ? 10 : tamanhoPagina;
    }

    protected String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() == 0 ? null : trimmed;
    }

    private SQLException toSqlException(String message, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(message, e);
    }

    private void rollbackQuietly(Transaction transaction) {
        if (transaction != null) {
            try {
                transaction.rollback();
            } catch (RuntimeException ignored) {
                // noop
            }
        }
    }

    private void closeQuietly(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (RuntimeException ignored) {
                // noop
            }
        }
    }
}
