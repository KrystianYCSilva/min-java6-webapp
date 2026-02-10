package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Ies;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo IES com foco no Registro 11 (laboratorio) e campos complementares.
 */
public class IesDAO extends AbstractHibernateDao {

    private final LayoutCampoDAO layoutCampoDAO;

    public IesDAO() {
        this(new LayoutCampoDAO());
    }

    public IesDAO(LayoutCampoDAO layoutCampoDAO) {
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final Ies ies, final Map<Long, String> camposComplementares) throws SQLException {
        return executeInTransaction(new SessionWork<Long>() {
            public Long execute(Session session) throws SQLException {
                Long iesId = toLong(session.save(ies));
                layoutCampoDAO.salvarValoresIes(session, iesId, camposComplementares);
                return iesId;
            }
        });
    }

    public void atualizar(final Ies ies, final Map<Long, String> camposComplementares) throws SQLException {
        if (ies == null || ies.getId() == null) {
            throw new IllegalArgumentException("IES/ID nao informada para atualizacao.");
        }
        executeInTransaction(new SessionWork<Void>() {
            public Void execute(Session session) {
                session.merge(ies);
                layoutCampoDAO.substituirValoresIes(session, ies.getId(), camposComplementares);
                return null;
            }
        });
    }

    public Ies buscarPorId(final Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        return executeInSession(new SessionWork<Ies>() {
            public Ies execute(Session session) {
                return (Ies) session.get(Ies.class, id);
            }
        });
    }

    public List<Ies> listar() throws SQLException {
        return executeInSession(new SessionWork<List<Ies>>() {
            public List<Ies> execute(Session session) {
                Query query = session.createQuery("from Ies i order by i.nomeLaboratorio");
                return query.list();
            }
        });
    }

    public List<Ies> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        final int page = normalizePage(pagina);
        final int size = normalizePageSize(tamanhoPagina);
        final int offset = (page - 1) * size;

        return executeInSession(new SessionWork<List<Ies>>() {
            public List<Ies> execute(Session session) {
                Query query = session.createQuery("from Ies i order by i.nomeLaboratorio");
                query.setFirstResult(offset);
                query.setMaxResults(size);
                return query.list();
            }
        });
    }

    public int contar() throws SQLException {
        return executeInSession(new SessionWork<Integer>() {
            public Integer execute(Session session) {
                Long total = (Long) session.createQuery("select count(i.id) from Ies i").uniqueResult();
                return Integer.valueOf(total == null ? 0 : total.intValue());
            }
        }).intValue();
    }

    public void excluir(final Long id) throws SQLException {
        if (id == null) {
            return;
        }
        executeInTransaction(new SessionWork<Void>() {
            public Void execute(Session session) {
                Ies ies = (Ies) session.get(Ies.class, id);
                if (ies == null) {
                    return null;
                }
                layoutCampoDAO.removerValoresIes(session, id);
                session.delete(ies);
                return null;
            }
        });
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long iesId) throws SQLException {
        return layoutCampoDAO.carregarValoresIesPorCampoId(iesId);
    }

    public Map<Integer, String> carregarCamposRegistro11PorNumero(Long iesId) throws SQLException {
        return layoutCampoDAO.carregarValoresIesPorNumero(iesId, ModulosLayout.IES_11);
    }

    private Long toLong(Serializable id) throws SQLException {
        if (id == null) {
            throw new SQLException("Falha ao gerar ID para IES.");
        }
        if (id instanceof Number) {
            return Long.valueOf(((Number) id).longValue());
        }
        return Long.valueOf(id.toString());
    }
}
