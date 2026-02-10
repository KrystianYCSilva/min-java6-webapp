package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Aluno (Registro 41), incluindo opcoes normalizadas e campos complementares.
 */
public class AlunoDAO extends AbstractJpaDao {

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public AlunoDAO() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public AlunoDAO(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final Aluno aluno, final long[] opcaoIds, final Map<Long, String> camposComplementares)
            throws SQLException {
        return executeInTransaction(new EntityManagerWork<Long>() {
            public Long execute(EntityManager entityManager) throws SQLException {
                entityManager.persist(aluno);
                entityManager.flush();
                Long alunoId = aluno.getId();
                if (alunoId == null) {
                    throw new SQLException("Falha ao gerar ID para aluno.");
                }
                opcaoDAO.salvarVinculosAluno(entityManager, alunoId, opcaoIds);
                layoutCampoDAO.salvarValoresAluno(entityManager, alunoId, camposComplementares);
                return alunoId;
            }
        });
    }

    public void atualizar(final Aluno aluno, final long[] opcaoIds, final Map<Long, String> camposComplementares)
            throws SQLException {
        if (aluno == null || aluno.getId() == null) {
            throw new IllegalArgumentException("Aluno/ID nao informado para atualizacao.");
        }
        executeInTransaction(new EntityManagerWork<Void>() {
            public Void execute(EntityManager entityManager) throws SQLException {
                entityManager.merge(aluno);
                opcaoDAO.substituirVinculosAluno(entityManager, aluno.getId(), opcaoIds);
                layoutCampoDAO.substituirValoresAluno(entityManager, aluno.getId(), camposComplementares);
                return null;
            }
        });
    }

    public Aluno buscarPorId(final Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        return executeInEntityManager(new EntityManagerWork<Aluno>() {
            public Aluno execute(EntityManager entityManager) throws SQLException {
                Aluno aluno = entityManager.find(Aluno.class, id);
                if (aluno == null) {
                    return null;
                }
                hydrateResumo(entityManager, aluno);
                return aluno;
            }
        });
    }

    public List<Aluno> listar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<Aluno>>() {
            public List<Aluno> execute(EntityManager entityManager) throws SQLException {
                TypedQuery<Aluno> query = entityManager.createQuery(
                        "select a from Aluno a order by a.nome", Aluno.class);
                List<Aluno> alunos = query.getResultList();
                for (int i = 0; i < alunos.size(); i++) {
                    hydrateResumo(entityManager, alunos.get(i));
                }
                return alunos;
            }
        });
    }

    public List<Aluno> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        final int page = normalizePage(pagina);
        final int size = normalizePageSize(tamanhoPagina);
        final int offset = (page - 1) * size;

        return executeInEntityManager(new EntityManagerWork<List<Aluno>>() {
            public List<Aluno> execute(EntityManager entityManager) throws SQLException {
                TypedQuery<Aluno> query = entityManager.createQuery(
                        "select a from Aluno a order by a.nome", Aluno.class);
                query.setFirstResult(offset);
                query.setMaxResults(size);
                List<Aluno> alunos = query.getResultList();
                for (int i = 0; i < alunos.size(); i++) {
                    hydrateResumo(entityManager, alunos.get(i));
                }
                return alunos;
            }
        });
    }

    public int contar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Integer>() {
            public Integer execute(EntityManager entityManager) {
                Long total = entityManager.createQuery("select count(a.id) from Aluno a", Long.class)
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
                Aluno aluno = entityManager.find(Aluno.class, id);
                if (aluno == null) {
                    return null;
                }
                opcaoDAO.removerVinculosAluno(entityManager, id);
                layoutCampoDAO.removerValoresAluno(entityManager, id);
                entityManager.remove(aluno);
                return null;
            }
        });
    }

    public List<Long> listarOpcaoDeficienciaIds(Long alunoId) throws SQLException {
        return opcaoDAO.listarIdsAluno(alunoId, CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
    }

    public List<String> listarOpcaoDeficienciaCodigos(Long alunoId) throws SQLException {
        return opcaoDAO.listarCodigosAluno(alunoId, CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long alunoId) throws SQLException {
        return layoutCampoDAO.carregarValoresAlunoPorCampoId(alunoId);
    }

    public Map<Integer, String> carregarCamposRegistro41PorNumero(Long alunoId) throws SQLException {
        return layoutCampoDAO.carregarValoresAlunoPorNumero(alunoId, ModulosLayout.ALUNO_41);
    }

    private void hydrateResumo(EntityManager entityManager, Aluno aluno) throws SQLException {
        aluno.setTiposDeficienciaResumo(
                opcaoDAO.resumirAluno(entityManager, aluno.getId(), CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA));
    }
}
