package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Docente;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Docente (Registro 31), incluindo campos complementares de leiaute.
 */
public class DocenteDAO extends AbstractHibernateDao {

    private final LayoutCampoDAO layoutCampoDAO;

    public DocenteDAO() {
        this(new LayoutCampoDAO());
    }

    public DocenteDAO(LayoutCampoDAO layoutCampoDAO) {
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final Docente docente, final Map<Long, String> camposComplementares) throws SQLException {
        return executeInTransaction(new SessionWork<Long>() {
            public Long execute(Session session) throws SQLException {
                Long docenteId = toLong(session.save(docente));
                layoutCampoDAO.salvarValoresDocente(session, docenteId, camposComplementares);
                return docenteId;
            }
        });
    }

    public void atualizar(final Docente docente, final Map<Long, String> camposComplementares) throws SQLException {
        if (docente == null || docente.getId() == null) {
            throw new IllegalArgumentException("Docente/ID nao informado para atualizacao.");
        }
        executeInTransaction(new SessionWork<Void>() {
            public Void execute(Session session) {
                session.merge(docente);
                layoutCampoDAO.substituirValoresDocente(session, docente.getId(), camposComplementares);
                return null;
            }
        });
    }

    public Docente buscarPorId(final Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        return executeInSession(new SessionWork<Docente>() {
            public Docente execute(Session session) {
                return (Docente) session.get(Docente.class, id);
            }
        });
    }

    public List<Docente> listar() throws SQLException {
        return executeInSession(new SessionWork<List<Docente>>() {
            public List<Docente> execute(Session session) {
                Query query = session.createQuery("from Docente d order by d.nome");
                return query.list();
            }
        });
    }

    public List<Docente> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        final int page = normalizePage(pagina);
        final int size = normalizePageSize(tamanhoPagina);
        final int offset = (page - 1) * size;

        return executeInSession(new SessionWork<List<Docente>>() {
            public List<Docente> execute(Session session) {
                Query query = session.createQuery("from Docente d order by d.nome");
                query.setFirstResult(offset);
                query.setMaxResults(size);
                return query.list();
            }
        });
    }

    public int contar() throws SQLException {
        return executeInSession(new SessionWork<Integer>() {
            public Integer execute(Session session) {
                Long total = (Long) session.createQuery("select count(d.id) from Docente d").uniqueResult();
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
                Docente docente = (Docente) session.get(Docente.class, id);
                if (docente == null) {
                    return null;
                }
                layoutCampoDAO.removerValoresDocente(session, id);
                session.delete(docente);
                return null;
            }
        });
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long docenteId) throws SQLException {
        return layoutCampoDAO.carregarValoresDocentePorCampoId(docenteId);
    }

    public Map<Integer, String> carregarCamposRegistro31PorNumero(Long docenteId) throws SQLException {
        return layoutCampoDAO.carregarValoresDocentePorNumero(docenteId, ModulosLayout.DOCENTE_31);
    }

    private Long toLong(Serializable id) throws SQLException {
        if (id == null) {
            throw new SQLException("Falha ao gerar ID para docente.");
        }
        if (id instanceof Number) {
            return Long.valueOf(((Number) id).longValue());
        }
        return Long.valueOf(id.toString());
    }
}
