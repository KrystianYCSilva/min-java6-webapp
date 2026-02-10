package br.gov.inep.censo.dao;

import br.gov.inep.censo.model.Municipio;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela de apoio de municipios.
 */
public class MunicipioDAO extends AbstractJpaDao {

    public boolean existeCodigo(final String codigo) throws SQLException {
        final String normalized = trimToNull(codigo);
        if (normalized == null) {
            return false;
        }
        return executeInEntityManager(new EntityManagerWork<Boolean>() {
            public Boolean execute(EntityManager entityManager) {
                Long total = entityManager
                        .createQuery("select count(m.codigo) from Municipio m where m.codigo = :codigo", Long.class)
                        .setParameter("codigo", normalized)
                        .getSingleResult();
                return Boolean.valueOf(total != null && total.longValue() > 0L);
            }
        }).booleanValue();
    }

    public boolean existeCodigoNaUf(final String codigo, final Integer codigoUf) throws SQLException {
        final String normalized = trimToNull(codigo);
        if (normalized == null || codigoUf == null) {
            return false;
        }
        return executeInEntityManager(new EntityManagerWork<Boolean>() {
            public Boolean execute(EntityManager entityManager) {
                Long total = entityManager.createQuery(
                                "select count(m.codigo) from Municipio m where m.codigo = :codigo and m.codigoUf = :codigoUf",
                                Long.class)
                        .setParameter("codigo", normalized)
                        .setParameter("codigoUf", codigoUf)
                        .getSingleResult();
                return Boolean.valueOf(total != null && total.longValue() > 0L);
            }
        }).booleanValue();
    }

    public List<Municipio> listarPorUf(final Integer codigoUf) throws SQLException {
        if (codigoUf == null) {
            return new ArrayList<Municipio>();
        }
        return executeInEntityManager(new EntityManagerWork<List<Municipio>>() {
            public List<Municipio> execute(EntityManager entityManager) {
                TypedQuery<Municipio> query = entityManager.createQuery(
                        "select m from Municipio m where m.codigoUf = :codigoUf order by m.nome", Municipio.class);
                query.setParameter("codigoUf", codigoUf);
                return query.getResultList();
            }
        });
    }
}
