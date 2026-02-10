package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Docente;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Docente (Registro 31), incluindo campos complementares de leiaute.
 */
public class DocenteDAO extends AbstractJpaDao {

    private final LayoutCampoDAO layoutCampoDAO;

    public DocenteDAO() {
        this(new LayoutCampoDAO());
    }

    public DocenteDAO(LayoutCampoDAO layoutCampoDAO) {
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final Docente docente, final Map<Long, String> camposComplementares) throws SQLException {
        return executeInTransaction(new EntityManagerWork<Long>() {
            public Long execute(EntityManager entityManager) throws SQLException {
                entityManager.persist(docente);
                entityManager.flush();
                Long docenteId = docente.getId();
                if (docenteId == null) {
                    throw new SQLException("Falha ao gerar ID para docente.");
                }
                layoutCampoDAO.salvarValoresDocente(entityManager, docenteId, camposComplementares);
                return docenteId;
            }
        });
    }

    public void atualizar(final Docente docente, final Map<Long, String> camposComplementares) throws SQLException {
        if (docente == null || docente.getId() == null) {
            throw new IllegalArgumentException("Docente/ID nao informado para atualizacao.");
        }
        executeInTransaction(new EntityManagerWork<Void>() {
            public Void execute(EntityManager entityManager) {
                entityManager.merge(docente);
                layoutCampoDAO.substituirValoresDocente(entityManager, docente.getId(), camposComplementares);
                return null;
            }
        });
    }

    public Docente buscarPorId(final Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        return executeInEntityManager(new EntityManagerWork<Docente>() {
            public Docente execute(EntityManager entityManager) {
                return entityManager.find(Docente.class, id);
            }
        });
    }

    public List<Docente> listar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<Docente>>() {
            public List<Docente> execute(EntityManager entityManager) {
                TypedQuery<Docente> query = entityManager.createQuery(
                        "select d from Docente d order by d.nome", Docente.class);
                return query.getResultList();
            }
        });
    }

    public List<Docente> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        final int page = normalizePage(pagina);
        final int size = normalizePageSize(tamanhoPagina);
        final int offset = (page - 1) * size;

        return executeInEntityManager(new EntityManagerWork<List<Docente>>() {
            public List<Docente> execute(EntityManager entityManager) {
                TypedQuery<Docente> query = entityManager.createQuery(
                        "select d from Docente d order by d.nome", Docente.class);
                query.setFirstResult(offset);
                query.setMaxResults(size);
                return query.getResultList();
            }
        });
    }

    public int contar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Integer>() {
            public Integer execute(EntityManager entityManager) {
                Long total = entityManager.createQuery("select count(d.id) from Docente d", Long.class)
                        .getSingleResult();
                return Integer.valueOf(total == null ? 0 : total.intValue());
            }
        }).intValue();
    }

    public void excluir(final Long id) throws SQLException {
        if (id == null) {
            return;
        }
        executeInTransaction(new EntityManagerWork<Void>() {
            public Void execute(EntityManager entityManager) {
                Docente docente = entityManager.find(Docente.class, id);
                if (docente == null) {
                    return null;
                }
                layoutCampoDAO.removerValoresDocente(entityManager, id);
                entityManager.remove(docente);
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

}
