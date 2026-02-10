package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.sql.SQLException;

/**
 * Infraestrutura base para DAOs JPA (EntityManager + EntityTransaction),
 * mantendo contrato checked de SQLException usado pelas camadas legadas.
 */
public abstract class AbstractJpaDao {

    protected interface EntityManagerWork<T> {
        T execute(EntityManager entityManager) throws SQLException;
    }

    protected <T> T executeInEntityManager(EntityManagerWork<T> work) throws SQLException {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateConnectionProvider.openEntityManager();
            return work.execute(entityManager);
        } catch (SQLException e) {
            throw e;
        } catch (RuntimeException e) {
            throw toSqlException("Falha de persistencia em contexto JPA.", e);
        } finally {
            closeQuietly(entityManager);
        }
    }

    protected <T> T executeInTransaction(EntityManagerWork<T> work) throws SQLException {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = HibernateConnectionProvider.openEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            T result = work.execute(entityManager);
            transaction.commit();
            return result;
        } catch (SQLException e) {
            rollbackQuietly(transaction);
            throw e;
        } catch (RuntimeException e) {
            rollbackQuietly(transaction);
            throw toSqlException("Falha de persistencia transacional com JPA.", e);
        } finally {
            closeQuietly(entityManager);
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

    private void rollbackQuietly(EntityTransaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (RuntimeException ignored) {
                // noop
            }
        }
    }

    private void closeQuietly(EntityManager entityManager) {
        if (entityManager != null) {
            try {
                entityManager.close();
            } catch (RuntimeException ignored) {
                // noop
            }
        }
    }
}
