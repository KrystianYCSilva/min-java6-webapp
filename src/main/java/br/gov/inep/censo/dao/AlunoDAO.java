package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Aluno (Registro 41), incluindo opcoes normalizadas e campos complementares.
 */
public class AlunoDAO extends AbstractHibernateDao {

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
        return executeInTransaction(new SessionWork<Long>() {
            public Long execute(Session session) throws SQLException {
                Long alunoId = toLong(session.save(aluno));
                opcaoDAO.salvarVinculosAluno(session, alunoId, opcaoIds);
                layoutCampoDAO.salvarValoresAluno(session, alunoId, camposComplementares);
                return alunoId;
            }
        });
    }

    public void atualizar(final Aluno aluno, final long[] opcaoIds, final Map<Long, String> camposComplementares)
            throws SQLException {
        if (aluno == null || aluno.getId() == null) {
            throw new IllegalArgumentException("Aluno/ID nao informado para atualizacao.");
        }
        executeInTransaction(new SessionWork<Void>() {
            public Void execute(Session session) throws SQLException {
                session.merge(aluno);
                opcaoDAO.substituirVinculosAluno(session, aluno.getId(), opcaoIds);
                layoutCampoDAO.substituirValoresAluno(session, aluno.getId(), camposComplementares);
                return null;
            }
        });
    }

    public Aluno buscarPorId(final Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        return executeInSession(new SessionWork<Aluno>() {
            public Aluno execute(Session session) throws SQLException {
                Aluno aluno = (Aluno) session.get(Aluno.class, id);
                if (aluno == null) {
                    return null;
                }
                hydrateResumo(session, aluno);
                return aluno;
            }
        });
    }

    public List<Aluno> listar() throws SQLException {
        return executeInSession(new SessionWork<List<Aluno>>() {
            public List<Aluno> execute(Session session) throws SQLException {
                Query query = session.createQuery("from Aluno a order by a.nome");
                List<Aluno> alunos = query.list();
                for (int i = 0; i < alunos.size(); i++) {
                    hydrateResumo(session, alunos.get(i));
                }
                return alunos;
            }
        });
    }

    public List<Aluno> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        final int page = normalizePage(pagina);
        final int size = normalizePageSize(tamanhoPagina);
        final int offset = (page - 1) * size;

        return executeInSession(new SessionWork<List<Aluno>>() {
            public List<Aluno> execute(Session session) throws SQLException {
                Query query = session.createQuery("from Aluno a order by a.nome");
                query.setFirstResult(offset);
                query.setMaxResults(size);
                List<Aluno> alunos = query.list();
                for (int i = 0; i < alunos.size(); i++) {
                    hydrateResumo(session, alunos.get(i));
                }
                return alunos;
            }
        });
    }

    public int contar() throws SQLException {
        return executeInSession(new SessionWork<Integer>() {
            public Integer execute(Session session) {
                Long total = (Long) session.createQuery("select count(a.id) from Aluno a").uniqueResult();
                return Integer.valueOf(total == null ? 0 : total.intValue());
            }
        }).intValue();
    }

    public void excluir(final Long id) throws SQLException {
        if (id == null) {
            return;
        }
        executeInTransaction(new SessionWork<Void>() {
            public Void execute(Session session) throws SQLException {
                Aluno aluno = (Aluno) session.get(Aluno.class, id);
                if (aluno == null) {
                    return null;
                }
                opcaoDAO.removerVinculosAluno(session, id);
                layoutCampoDAO.removerValoresAluno(session, id);
                session.delete(aluno);
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

    private void hydrateResumo(Session session, Aluno aluno) throws SQLException {
        aluno.setTiposDeficienciaResumo(
                opcaoDAO.resumirAluno(session, aluno.getId(), CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA));
    }

    private Long toLong(Serializable id) throws SQLException {
        if (id == null) {
            throw new SQLException("Falha ao gerar ID para aluno.");
        }
        if (id instanceof Number) {
            return Long.valueOf(((Number) id).longValue());
        }
        return Long.valueOf(id.toString());
    }
}
