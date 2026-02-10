package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Curso;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Curso (Registro 21), incluindo opcoes normalizadas e campos complementares.
 */
public class CursoDAO extends AbstractJpaDao {

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public CursoDAO() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public CursoDAO(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final Curso curso, final long[] opcaoIds, final Map<Long, String> camposComplementares)
            throws SQLException {
        return executeInTransaction(new EntityManagerWork<Long>() {
            public Long execute(EntityManager entityManager) throws SQLException {
                entityManager.persist(curso);
                entityManager.flush();
                Long cursoId = curso.getId();
                if (cursoId == null) {
                    throw new SQLException("Falha ao gerar ID para curso.");
                }
                opcaoDAO.salvarVinculosCurso(entityManager, cursoId, opcaoIds);
                layoutCampoDAO.salvarValoresCurso(entityManager, cursoId, camposComplementares);
                return cursoId;
            }
        });
    }

    public void atualizar(final Curso curso, final long[] opcaoIds, final Map<Long, String> camposComplementares)
            throws SQLException {
        if (curso == null || curso.getId() == null) {
            throw new IllegalArgumentException("Curso/ID nao informado para atualizacao.");
        }
        executeInTransaction(new EntityManagerWork<Void>() {
            public Void execute(EntityManager entityManager) throws SQLException {
                entityManager.merge(curso);
                opcaoDAO.substituirVinculosCurso(entityManager, curso.getId(), opcaoIds);
                layoutCampoDAO.substituirValoresCurso(entityManager, curso.getId(), camposComplementares);
                return null;
            }
        });
    }

    public Curso buscarPorId(final Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        return executeInEntityManager(new EntityManagerWork<Curso>() {
            public Curso execute(EntityManager entityManager) throws SQLException {
                Curso curso = entityManager.find(Curso.class, id);
                if (curso == null) {
                    return null;
                }
                hydrateResumo(entityManager, curso);
                return curso;
            }
        });
    }

    public List<Curso> listar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<Curso>>() {
            public List<Curso> execute(EntityManager entityManager) throws SQLException {
                TypedQuery<Curso> query = entityManager.createQuery(
                        "select c from Curso c order by c.nome", Curso.class);
                List<Curso> cursos = query.getResultList();
                for (int i = 0; i < cursos.size(); i++) {
                    hydrateResumo(entityManager, cursos.get(i));
                }
                return cursos;
            }
        });
    }

    public List<Curso> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        final int page = normalizePage(pagina);
        final int size = normalizePageSize(tamanhoPagina);
        final int offset = (page - 1) * size;

        return executeInEntityManager(new EntityManagerWork<List<Curso>>() {
            public List<Curso> execute(EntityManager entityManager) throws SQLException {
                TypedQuery<Curso> query = entityManager.createQuery(
                        "select c from Curso c order by c.nome", Curso.class);
                query.setFirstResult(offset);
                query.setMaxResults(size);
                List<Curso> cursos = query.getResultList();
                for (int i = 0; i < cursos.size(); i++) {
                    hydrateResumo(entityManager, cursos.get(i));
                }
                return cursos;
            }
        });
    }

    public int contar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Integer>() {
            public Integer execute(EntityManager entityManager) {
                Long total = entityManager.createQuery("select count(c.id) from Curso c", Long.class)
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
            public Void execute(EntityManager entityManager) throws SQLException {
                Curso curso = entityManager.find(Curso.class, id);
                if (curso == null) {
                    return null;
                }
                opcaoDAO.removerVinculosCurso(entityManager, id);
                layoutCampoDAO.removerValoresCurso(entityManager, id);
                entityManager.remove(curso);
                return null;
            }
        });
    }

    public List<Long> listarOpcaoRecursoAssistivoIds(Long cursoId) throws SQLException {
        return opcaoDAO.listarIdsCurso(cursoId, CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
    }

    public List<String> listarOpcaoRecursoAssistivoCodigos(Long cursoId) throws SQLException {
        return opcaoDAO.listarCodigosCurso(cursoId, CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long cursoId) throws SQLException {
        return layoutCampoDAO.carregarValoresCursoPorCampoId(cursoId);
    }

    public Map<Integer, String> carregarCamposRegistro21PorNumero(Long cursoId) throws SQLException {
        return layoutCampoDAO.carregarValoresCursoPorNumero(cursoId, ModulosLayout.CURSO_21);
    }

    private void hydrateResumo(EntityManager entityManager, Curso curso) throws SQLException {
        curso.setRecursosTecnologiaAssistivaResumo(opcaoDAO.resumirCurso(
                entityManager, curso.getId(), CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA));
    }
}
