package br.gov.inep.censo.dao;

import br.gov.inep.censo.model.Municipio;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela de apoio de municipios.
 */
public class MunicipioDAO extends AbstractHibernateDao {

    public boolean existeCodigo(final String codigo) throws SQLException {
        final String normalized = trimToNull(codigo);
        if (normalized == null) {
            return false;
        }
        return executeInSession(new SessionWork<Boolean>() {
            public Boolean execute(Session session) {
                Query query = session.createQuery("select count(m.codigo) from Municipio m where m.codigo = :codigo");
                query.setString("codigo", normalized);
                Long total = (Long) query.uniqueResult();
                return Boolean.valueOf(total != null && total.longValue() > 0L);
            }
        }).booleanValue();
    }

    public boolean existeCodigoNaUf(final String codigo, final Integer codigoUf) throws SQLException {
        final String normalized = trimToNull(codigo);
        if (normalized == null || codigoUf == null) {
            return false;
        }
        return executeInSession(new SessionWork<Boolean>() {
            public Boolean execute(Session session) {
                Query query = session.createQuery(
                        "select count(m.codigo) from Municipio m where m.codigo = :codigo and m.codigoUf = :codigoUf");
                query.setString("codigo", normalized);
                query.setInteger("codigoUf", codigoUf.intValue());
                Long total = (Long) query.uniqueResult();
                return Boolean.valueOf(total != null && total.longValue() > 0L);
            }
        }).booleanValue();
    }

    public List<Municipio> listarPorUf(final Integer codigoUf) throws SQLException {
        if (codigoUf == null) {
            return new ArrayList<Municipio>();
        }
        return executeInSession(new SessionWork<List<Municipio>>() {
            public List<Municipio> execute(Session session) {
                Query query = session.createQuery(
                        "from Municipio m where m.codigoUf = :codigoUf order by m.nome");
                query.setInteger("codigoUf", codigoUf.intValue());
                return query.list();
            }
        });
    }
}
