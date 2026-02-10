package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Ies;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo IES com foco no Registro 11 (laboratorio) e campos complementares.
 */
public class IesDAO extends AbstractJpaDao {

    private final LayoutCampoDAO layoutCampoDAO;

    public IesDAO() {
        this(new LayoutCampoDAO());
    }

    public IesDAO(LayoutCampoDAO layoutCampoDAO) {
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final Ies ies, final Map<Long, String> camposComplementares) throws SQLException {
        return executeInTransaction(new EntityManagerWork<Long>() {
            public Long execute(EntityManager entityManager) throws SQLException {
                entityManager.persist(ies);
                entityManager.flush();
                Long iesId = ies.getId();
                if (iesId == null) {
                    throw new SQLException("Falha ao gerar ID para IES.");
                }
                layoutCampoDAO.salvarValoresIes(entityManager, iesId, camposComplementares);
                return iesId;
            }
        });
    }

    public void atualizar(final Ies ies, final Map<Long, String> camposComplementares) throws SQLException {
        if (ies == null || ies.getId() == null) {
            throw new IllegalArgumentException("IES/ID nao informada para atualizacao.");
        }
        executeInTransaction(new EntityManagerWork<Void>() {
            public Void execute(EntityManager entityManager) {
                entityManager.merge(ies);
                layoutCampoDAO.substituirValoresIes(entityManager, ies.getId(), camposComplementares);
                return null;
            }
        });
    }

    public Ies buscarPorId(final Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        return executeInEntityManager(new EntityManagerWork<Ies>() {
            public Ies execute(EntityManager entityManager) {
                return entityManager.find(Ies.class, id);
            }
        });
    }

    public List<Ies> listar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<Ies>>() {
            public List<Ies> execute(EntityManager entityManager) {
                TypedQuery<Ies> query = entityManager.createQuery(
                        "select i from Ies i order by i.nomeLaboratorio", Ies.class);
                return query.getResultList();
            }
        });
    }

    public List<Ies> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        final int page = normalizePage(pagina);
        final int size = normalizePageSize(tamanhoPagina);
        final int offset = (page - 1) * size;

        return executeInEntityManager(new EntityManagerWork<List<Ies>>() {
            public List<Ies> execute(EntityManager entityManager) {
                TypedQuery<Ies> query = entityManager.createQuery(
                        "select i from Ies i order by i.nomeLaboratorio", Ies.class);
                query.setFirstResult(offset);
                query.setMaxResults(size);
                return query.getResultList();
            }
        });
    }

    public int contar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Integer>() {
            public Integer execute(EntityManager entityManager) {
                Long total = entityManager.createQuery("select count(i.id) from Ies i", Long.class)
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
                Ies ies = entityManager.find(Ies.class, id);
                if (ies == null) {
                    return null;
                }
                layoutCampoDAO.removerValoresIes(entityManager, id);
                entityManager.remove(ies);
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

}
